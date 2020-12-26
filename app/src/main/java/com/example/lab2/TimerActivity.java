package com.example.lab2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TimerActivity extends AppCompatActivity {
    private boolean running, is_service_active;
    private long start_time, time_left, full_time;
    private int index;
    ArrayList<Integer> time_list;
    ArrayList<String> task_list;

    private CountDownTimer countDownTimer;
    TextView textViewTime, textViewTask;
    ImageView imageViewPause;
    ListView listViewTimer;
    DbAdapter dbAdapter;
    ImageView btnPrev, btnNext;
    Intent serviceIntent;
    ServiceConnection sConn;
    TimerService timerService;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        mediaPlayer = MediaPlayer.create(this, R.raw.finish);

        is_service_active = false;
        sConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                is_service_active = true;
                timerService = ((TimerService.MyBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                is_service_active = false;
            }
        };


        index = 0;
        textViewTime = findViewById(R.id.textViewTime);
        textViewTask = findViewById(R.id.textViewTimerTask);
        imageViewPause = findViewById(R.id.imageViewPause);

        listViewTimer = findViewById(R.id.listViewTimer);

        btnPrev = findViewById(R.id.imageViewPrevious);
        btnNext = findViewById(R.id.imageViewNext);

        dbAdapter = new DbAdapter(this);
        dbAdapter.open();
        Bundle extras = getIntent().getExtras();
        long id = extras.getLong("id");
        final Timer timer = dbAdapter.getTimer(id);
        Log.i("title", timer.getTitle());
        TextView textView = findViewById(R.id.textViewTimerTitle);
        textView.setText(timer.getTitle());

        task_list = new ArrayList<String>();
        time_list = new ArrayList<Integer>();
        task_list.add(this.getString(R.string.Ready) + ": " + timer.getReady());
        start_time = timer.getReady() * 1000;
        time_left = start_time;
        time_list.add((int)start_time);
        full_time = start_time;
        for (int j = 0; j < timer.getSets(); j++) {
            for (int i = 0; i < timer.getCycles(); i++) {
                full_time += timer.getWork() * 1000 + timer.getRelax() * 1000;
                task_list.add(this.getString(R.string.Work) + ": " + timer.getWork());
                time_list.add(timer.getWork() * 1000);
                task_list.add(this.getString(R.string.Relax) + ": " + timer.getRelax());
                time_list.add(timer.getRelax() * 1000);
            }
            if (timer.getSets() > 1) {
                full_time += timer.getRelax_sets() * 1000;
                task_list.add(this.getString(R.string.SetsRelax) + ": " + timer.getRelax_sets());
                time_list.add(timer.getRelax_sets() * 1000);
            }
        }
        task_list.add(this.getString(R.string.Finish));
        time_list.add(1000);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, task_list);
        listViewTimer.setAdapter(adapter);
        dbAdapter.close();

        //textViewTask.setText(task_list.get(index));
        //startTimer();
        mediaPlayer.start();

        imageViewPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (running) {
                    pauseTimer();
                } else {
                    startTimer();
                    running = true;
                }
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index > 0) {
                    Log.i("index", String.valueOf(index));
                    if (index == time_list.size()) {
                        index-=2;
                    }
                    else {
                        index--;
                    }
                    mediaPlayer.stop();
                    try {
                        mediaPlayer.prepare();
                        mediaPlayer.seekTo(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();

                    textViewTask.setText(task_list.get(index));
                    time_left = time_list.get(index);
                    countDownTimer.cancel();
                    startTimer();
                }

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index < time_list.size() - 1) {
                    index++;
                    textViewTask.setText(task_list.get(index));
                    mediaPlayer.stop();
                    try {
                        mediaPlayer.prepare();
                        mediaPlayer.seekTo(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mediaPlayer.start();
                    time_left = time_list.get(index);
                    countDownTimer.cancel();
                    startTimer();
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (running) {
            serviceIntent = new Intent(this, TimerService.class);
            serviceIntent.putExtra("index", index);
            serviceIntent.putExtra("time_list", time_list);
            serviceIntent.putExtra("time_left", time_left);
            countDownTimer.cancel();
            startService(serviceIntent);
            bindService(serviceIntent, sConn, BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (is_service_active) {
            time_left = timerService.getTime_left();
            index = timerService.getIndex();
            unbindService(sConn);
            stopService(serviceIntent);
        }
        if (index < task_list.size()) {
            textViewTask.setText(task_list.get(index));
            //time_left = time_list.get(index);
            startTimer();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(sConn);
        stopService(serviceIntent);
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(time_left, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time_left = millisUntilFinished;
                if (index == time_list.size() - 1) {
                    textViewTime.setText(String.valueOf(0));
                }
                else {
                    textViewTime.setText(String.valueOf(time_left / 1000 + 1));
                }

                Log.i("time", String.valueOf(time_left / 1000 + 1));
            }

            @Override
            public void onFinish() {
                running = false;
                index++;

                //Log.i("time", String.valueOf(time_list.size()));
                if (index < time_list.size()) {
                    listViewTimer.setSelection(index);
                    textViewTask.setText(task_list.get(index));
                    start_time = time_list.get(index);
                    time_left = start_time;
                    mediaPlayer.start();
                    //full_time -= start_time;
                    time_left--;
                    startTimer();
                }
            }
        }.start();
        running = true;
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        running = false;
    }
}