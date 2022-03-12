package com.example.horsetracker.database.model;

import android.content.ContentValues;

import java.text.DecimalFormat;
import java.util.Objects;

public class LogLine {

    public static final String TABLE_NAME = "log_lines";

    public static final String COL_ID = "id";
    public static final String COL_RSSI = "rssi";
    public static final String COL_TIMESTAMP = "timestamp";
    public static final String COL_ADDRESS = "address";
    public static final String COL_DISTANCE = "distance";

    private int id;
    private int rssi;
    private float distance;
    private String timestamp;
    private String address;

    private final DecimalFormat df = new DecimalFormat("###.#");

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COL_RSSI + " INTEGER,"
                    + COL_DISTANCE + " INTEGER,"
                    + COL_ADDRESS + " TEXT,"
                    + COL_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setDistanceFromRSSI(int rssi) {
        this.distance = calcDistance(rssi);
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LogLine() {
    }

    public LogLine(int rssi, String timestamp, String address) {
        this.rssi = rssi;
        this.timestamp = timestamp;
        this.address = address;
        this.distance = calcDistance(rssi);
    }

    private float calcDistance(int rssi) {
        return (float) Math.pow(10, ((-69 - rssi) / (10f * 2f)));
    }

    public ContentValues toContentValue() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ADDRESS, this.getAddress());
        contentValues.put(COL_RSSI, this.getRssi());
        contentValues.put(COL_DISTANCE, this.getDistance());
        return contentValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogLine logLine = (LogLine) o;
        return id == logLine.id && rssi == logLine.rssi && Float.compare(logLine.distance, distance) == 0 && Objects.equals(timestamp, logLine.timestamp) && Objects.equals(address, logLine.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rssi, distance, timestamp, address);
    }

    public String toDisplayString() {
        return timestamp + ": (" + rssi + ") -> ~" + df.format(distance) + "m";
    }

    @Override
    public String toString() {
        return "LogLine{" +
                "id=" + id +
                ", rssi=" + rssi +
                ", distance=" + distance +
                ", timestamp='" + timestamp + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}


