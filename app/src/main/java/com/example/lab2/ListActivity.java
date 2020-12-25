package com.example.lab2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;
import java.util.Objects;

public class ListActivity extends AppCompatActivity {
    private ListView listView;
    private DbAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.listView1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dbAdapter = new DbAdapter(this);
        dbAdapter.open();
        List<Timer> timers = dbAdapter.getTimers();

        TimerAdapter adapter = new TimerAdapter(this, timers);
        listView.setAdapter(adapter);
        dbAdapter.close();
    }
}