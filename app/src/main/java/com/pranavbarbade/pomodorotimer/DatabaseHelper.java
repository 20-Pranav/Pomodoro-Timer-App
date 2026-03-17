package com.pranavbarbade.pomodorotimer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PomodoroDB";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "sessions";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_PURPOSE = "purpose";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_TIME + " TEXT, " +
                COLUMN_DURATION + " INTEGER, " +
                COLUMN_PURPOSE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Add a new session with purpose
    public void addSession(String date, String time, int duration, String purpose) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_PURPOSE, purpose);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // Get all sessions
    public List<Session> getAllSessions() {
        List<Session> sessionList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_DATE + " DESC, " + COLUMN_TIME + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Session session = new Session(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4)
                );
                sessionList.add(session);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return sessionList;
    }

    // Get sessions by purpose
    public List<Session> getSessionsByPurpose(String purpose) {
        List<Session> sessionList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PURPOSE + " = ? ORDER BY " + COLUMN_DATE + " DESC, " + COLUMN_TIME + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{purpose});

        if (cursor.moveToFirst()) {
            do {
                Session session = new Session(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4)
                );
                sessionList.add(session);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return sessionList;
    }

    // Get session count by purpose
    public int getSessionCountByPurpose(String purpose) {
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + COLUMN_PURPOSE + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{purpose});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    // Get total sessions count
    public int getSessionCount() {
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    // Get total focus time (minutes)
    public int getTotalFocusTime() {
        String query = "SELECT SUM(" + COLUMN_DURATION + ") FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return total;
    }
}
