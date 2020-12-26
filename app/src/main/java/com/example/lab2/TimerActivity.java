package com.example.lab2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TimerActivity extends AppCompatActivity {
    private boolean running;
    private long start_time, time_left, full_time;
    private int index;
    ArrayList<Integer> time_list;
    private CountDownTimer countDownTimer;
    TextView textViewTime;
    ImageView imageViewPause;
    ListView listViewTimer;
    DbAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        index = 0;
        textViewTime = findViewById(R.id.textViewTime);
        imageViewPause = findViewById(R.id.imageViewPause);

        listViewTimer = findViewById(R.id.listViewTimer);

        dbAdapter = new DbAdapter(this);
        dbAdapter.open();
        Bundle extras = getIntent().getExtras();
        long id = extras.getLong("id");
        final Timer timer = dbAdapter.getTimer(id);
        Log.i("title", timer.getTitle());
        TextView textView = findViewById(R.id.textViewTimerTitle);
        textView.setText(timer.getTitle());

        ArrayList<String> task_list = new ArrayList<String>();
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, task_list);
        listViewTimer.setAdapter(adapter);
        dbAdapter.close();
        startTimer();
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

    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(time_left, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time_left = millisUntilFinished;
                textViewTime.setText(String.valueOf(time_left / 1000));
                Log.i("time", String.valueOf(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                running = false;
                index++;
                if (index < time_list.size()) {
                    start_time = time_list.get(index);
                    time_left = start_time;
                    full_time -= start_time;

                    if (full_time > 0) {
                        time_left--;
                        startTimer();
                    } else {
                        countDownTimer.cancel();
                    }
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