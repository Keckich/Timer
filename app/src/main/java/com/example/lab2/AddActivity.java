package com.example.lab2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.jaredrummler.android.colorpicker.ColorShape;

public class AddActivity extends AppCompatActivity implements ColorPickerDialogListener {
    private EditText editTitle;
    private EditText editReady;
    private EditText editWork;
    private EditText editRelax;
    private EditText editCycles;
    private EditText editSets;
    private EditText editRelaxSets;

    private Button buttonColor;

    private DbAdapter dbAdapter;
    private long timerId=0;
    private int colorId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbAdapter = new DbAdapter(this);
        buttonColor = findViewById(R.id.button_color);
        editTitle = findViewById(R.id.edit1);
        editReady = findViewById(R.id.edit2);
        editWork = findViewById(R.id.edit3);
        editRelax = findViewById(R.id.edit4);
        editCycles = findViewById(R.id.edit5);
        editSets = findViewById(R.id.edit6);
        editRelaxSets = findViewById(R.id.edit7);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            timerId = extras.getLong("id");
        }
        if (timerId > 0) {
            dbAdapter.open();
            Timer timer = dbAdapter.getTimer(timerId);
            editTitle.setText(timer.getTitle());
            editReady.setText(String.valueOf(timer.getReady()));
            editWork.setText(String.valueOf(timer.getWork()));
            editRelax.setText(String.valueOf(timer.getRelax()));
            editCycles.setText(String.valueOf(timer.getCycles()));
            editSets.setText(String.valueOf(timer.getSets()));
            editRelaxSets.setText(String.valueOf(timer.getRelax_sets()));
            colorId = timer.getTimerColor();
        }
        dbAdapter.close();
    }

    public void addTimer(View view) {
        save(view);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void save(View view) {
        String title = editTitle.getText().toString();
        int ready = Integer.parseInt(editReady.getText().toString());
        int work = Integer.parseInt(editWork.getText().toString());
        int relax = Integer.parseInt(editRelax.getText().toString());
        int cycles = Integer.parseInt(editCycles.getText().toString());
        int sets = Integer.parseInt(editSets.getText().toString());
        int relax_sets = Integer.parseInt(editRelaxSets.getText().toString());
        Timer timer = new Timer(timerId, title, ready, work, relax, cycles, sets, relax_sets,
                colorId);

        dbAdapter.open();
        if (timerId > 0) {
            dbAdapter.update(timer);
        }
        else {
            dbAdapter.insert(timer);
        }
        dbAdapter.close();
    }

    private void createColorPickerDialog() {
        ColorPickerDialog.newBuilder()
                .setColor(Color.RED)
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowCustom(true)
                .setAllowPresets(true)
                .setColorShape(ColorShape.SQUARE)
                .show(this);
    }

    public void onClickColor(View view) {
        createColorPickerDialog();
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        colorId = color;
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.setting) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}