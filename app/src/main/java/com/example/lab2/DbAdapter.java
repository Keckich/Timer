package com.example.lab2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class DbAdapter {

    private final DbHelper dbHelper;
    private SQLiteDatabase database;

    public DbAdapter(Context context){
        dbHelper = new DbHelper(context.getApplicationContext());
    }

    public DbAdapter open(){
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    private Cursor getAllEntries(){
        String[] columns = new String[] {DbHelper.COLUMN_ID, DbHelper.COLUMN_TITLE, DbHelper.COLUMN_READY,
                DbHelper.COLUMN_WORK, DbHelper.COLUMN_RELAX, DbHelper.COLUMN_CYCLES, DbHelper.COLUMN_SETS,
                DbHelper.COLUMN_RELAX_SETS, DbHelper.COLUMN_COLOR};
        return  database.query(DbHelper.TABLE, columns, null, null, null, null, null);
    }

    public List<Timer> getTimers(){
        ArrayList<Timer> Timers = new ArrayList<>();
        Cursor cursor = getAllEntries();
        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_TITLE));
            int ready = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_READY));
            int work = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_WORK));
            int relax = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_RELAX));
            int cycles = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_CYCLES));
            int sets = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_SETS));
            int relax_sets = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_RELAX_SETS));
            int color = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_COLOR));
            Timers.add(new Timer(id, title, ready, work, relax, cycles, sets, relax_sets, color));
        }
        cursor.close();
        return  Timers;
    }

    public long getCount(){
        return DatabaseUtils.queryNumEntries(database, DbHelper.TABLE);
    }

    public Timer getTimer(long id){
        Timer Timer = null;
        String query = String.format("SELECT * FROM %s WHERE %s=?",DbHelper.TABLE, DbHelper.COLUMN_ID);
        Cursor cursor = database.rawQuery(query, new String[]{ String.valueOf(id)});
        if(cursor.moveToFirst()){
            String title = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_TITLE));
            int ready = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_READY));
            int work = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_WORK));
            int relax = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_RELAX));
            int cycles = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_CYCLES));
            int sets = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_SETS));
            int relax_sets = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_RELAX_SETS));
            int color = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_COLOR));
            Timer = new Timer(id, title, ready, work, relax, cycles, sets, relax_sets, color);
        }
        cursor.close();
        return  Timer;
    }

    public long insert(Timer Timer){

        ContentValues cv = new ContentValues();
        cv.put(DbHelper.COLUMN_TITLE, Timer.getTitle());
        cv.put(DbHelper.COLUMN_READY, Timer.getReady());
        cv.put(DbHelper.COLUMN_WORK, Timer.getWork());
        cv.put(DbHelper.COLUMN_RELAX, Timer.getRelax());
        cv.put(DbHelper.COLUMN_CYCLES, Timer.getCycles());
        cv.put(DbHelper.COLUMN_SETS, Timer.getSets());
        cv.put(DbHelper.COLUMN_RELAX_SETS, Timer.getRelax_sets());
        cv.put(DbHelper.COLUMN_COLOR, Timer.getTimerColor());

        return  database.insert(DbHelper.TABLE, null, cv);
    }

    public long delete(long TimerId){

        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(TimerId)};
        return database.delete(DbHelper.TABLE, whereClause, whereArgs);
    }

    public void deleteAll() {
        dbHelper.onUpgrade(database, 0, 0);
    }

    public long update(Timer Timer){

        String whereClause = DbHelper.COLUMN_ID + "=" + String.valueOf(Timer.getId());
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.COLUMN_TITLE, Timer.getTitle());
        cv.put(DbHelper.COLUMN_READY, Timer.getReady());
        cv.put(DbHelper.COLUMN_WORK, Timer.getWork());
        cv.put(DbHelper.COLUMN_RELAX, Timer.getRelax());
        cv.put(DbHelper.COLUMN_CYCLES, Timer.getCycles());
        cv.put(DbHelper.COLUMN_SETS, Timer.getSets());
        cv.put(DbHelper.COLUMN_RELAX_SETS, Timer.getRelax_sets());
        cv.put(DbHelper.COLUMN_COLOR, Timer.getTimerColor());
        return database.update(DbHelper.TABLE, cv, whereClause, null);
    }
}
