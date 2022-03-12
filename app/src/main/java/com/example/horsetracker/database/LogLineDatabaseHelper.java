package com.example.horsetracker.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.horsetracker.database.model.LogLine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LogLineDatabaseHelper extends SQLiteOpenHelper {


    public LogLineDatabaseHelper(Context context) {
        super(context, "horsetracker_db", null, 1);
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(LogLine.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LogLine.TABLE_NAME);
        this.onCreate(sqLiteDatabase);
    }

    public void insertLogLine(int rssi, String timestamp, String address) {
        try (SQLiteDatabase writableDatabase = this.getWritableDatabase()) {
            LogLine logLine = new LogLine(rssi, timestamp, address);
            writableDatabase.insert(LogLine.TABLE_NAME, null, logLine.toContentValue());
        }
    }

    public List<LogLine> getAllLogLines() {
        int size = 32;
        LinkedList<LogLine> logLines = new LinkedList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + LogLine.TABLE_NAME + " ORDER BY " +
                LogLine.COL_TIMESTAMP + " ASC";


        try (SQLiteDatabase db = this.getWritableDatabase()) {
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToLast()) {
                do {
                    LogLine logLine = new LogLine();
                    logLine.setId(cursor.getInt(cursor.getColumnIndex(LogLine.COL_ID)));
                    logLine.setRssi(cursor.getInt(cursor.getColumnIndex(LogLine.COL_RSSI)));
                    logLine.setTimestamp(cursor.getString(cursor.getColumnIndex(LogLine.COL_TIMESTAMP)));
                    logLine.setAddress(cursor.getString(cursor.getColumnIndex(LogLine.COL_ADDRESS)));
                    logLine.setDistance(cursor.getFloat(cursor.getColumnIndex(LogLine.COL_DISTANCE)));
                    logLines.addFirst(logLine);
                } while (cursor.moveToPrevious() && logLines.size() < size);
            }
        }
        return logLines;
    }

    public int getLogLinesCount() {
        String countQuery = "SELECT  * FROM " + LogLine.TABLE_NAME;
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            return db.rawQuery(countQuery, null).getCount();
        }
    }
}
