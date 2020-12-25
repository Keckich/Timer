package com.example.lab2;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "timerstore.db"; // название бд
    private static final int SCHEMA = 1; // версия базы данных
    static final String TABLE = "timers"; // название таблицы в бд
    // названия столбцов
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_READY = "ready";
    public static final String COLUMN_WORK = "work";
    public static final String COLUMN_RELAX = "relax";
    public static final String COLUMN_CYCLES = "cycles";
    public static final String COLUMN_SETS = "sets";
    public static final String COLUMN_RELAX_SETS = "relax_sets";
    public static final String COLUMN_COLOR = "color";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_TITLE
                + " TEXT, " + COLUMN_READY + " INTEGER, " + COLUMN_WORK
                + " INTEGER, " + COLUMN_RELAX + " INTEGER, " + COLUMN_CYCLES
                + " INTEGER, " + COLUMN_SETS + " INTEGER, " + COLUMN_RELAX_SETS
                + " INTEGER, " + COLUMN_COLOR + " INTEGER);");

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE);
        onCreate(db);
    }
}