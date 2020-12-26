package com.example.lab2;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

public class TimerService extends Service {
    long time_left;
    int index;
    ArrayList<Integer> time_list;
    CountDownTimer countDownTimer;
    MediaPlayer mediaPlayer;


    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.finish);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        time_left = extras.getLong("time_left");
        index = extras.getInt("index");
        time_list = extras.getIntegerArrayList("time_list");
        Log.i("servindex", String.valueOf(index));
        startTimer();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(time_left, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time_left = millisUntilFinished;
                Log.i("servtime", String.valueOf(time_left / 1000 + 1));
            }

            @Override
            public void onFinish() {
                index++;
                if (index < time_list.size()) {
                    time_left = time_list.get(index);
                    mediaPlayer.start();
                    time_left--;
                    startTimer();
                }
            }
        }.start();
    }

    public long getTime_left() {
        return time_left;
    }

    public int getIndex() {
        return index;
    }

    public class MyBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }
}

