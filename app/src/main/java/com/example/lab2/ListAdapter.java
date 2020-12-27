package com.example.lab2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    List<Timer> timers;
    ImageView imageViewSettings;
    DbAdapter dbAdapter;
    LinearLayout linearLayout;


    ListAdapter(Context context, List<Timer> timers) {
        this.context = context;
        this.timers = timers;
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dbAdapter = new DbAdapter(context);
    }

    @Override
    public int getCount() {
        return timers.size();
    }

    @Override
    public Object getItem(int position) {
        return timers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item, parent, false);
        }
        final Timer timer = getTimer(position);
        imageViewSettings = view.findViewById(R.id.imageViewSettings);
        TextView textViewTimer = view.findViewById(R.id.textView);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            float font = Float.parseFloat(sharedPreferences.getString("font_preference", "21"));
            textViewTimer.setTextSize(font);
        } catch (Exception e) {
            textViewTimer.setTextSize(21);
        }

        linearLayout = view.findViewById(R.id.linearLayout1);
        linearLayout.setBackgroundColor(timer.getTimerColor());
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TimerActivity.class);
                intent.putExtra("id", timer.getId());
                context.startActivity(intent);
            }
        });
        imageViewSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.inflate(R.menu.popupmenu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:
                                Intent intent = new Intent(context, AddActivity.class);
                                intent.putExtra("id", timer.getId());
                                context.startActivity(intent);
                                return true;
                            case R.id.menu2:
                                dbAdapter.open();
                                dbAdapter.delete(timer.getId());
                                dbAdapter.close();
                                return true;
                            default:
                                return false;

                        }
                    }
                });
            }
        });
        textViewTimer.setText(timer.getTitle());
        return view;
    }

    Timer getTimer(int position) {
        return (Timer) getItem(position);
    }
}
