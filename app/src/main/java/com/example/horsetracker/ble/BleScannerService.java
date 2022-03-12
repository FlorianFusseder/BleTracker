package com.example.horsetracker.ble;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.horsetracker.MainActivity;
import com.example.horsetracker.R;

import java.time.Instant;

public class BleScannerService extends IntentService {

    public static final String SCAN_STATE_CHANGED = "BLESCANNER_SCAN_STATE_CHANGED";
    public static final String NEW_ENTRY = "BLESCANNER_NEW_ENTRY";
    private boolean keepScanning;
    private static final String TAG = "Ble";
    private int scanPeriod;
    private int waitPeriod;

    public BleScannerService() {
        super("BleScannerService");
        this.keepScanning = false;
        this.scanPeriod = 5000;
        this.waitPeriod = 5000;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel("channelID", "name", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Hello! This is a notification.");
        notificationManager.createNotificationChannel(channel);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, "channelID")
                .setContentTitle("HorseTracker")
                .setContentText("scanning...")
                .setSmallIcon(R.drawable.horse)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);


        Toast.makeText(this, "Started Scanning", Toast.LENGTH_SHORT).show();
        sendBroadcast(new Intent(SCAN_STATE_CHANGED));

        keepScanning = true;


        ScanCallback leScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                Intent intent = new Intent(NEW_ENTRY);
                intent.putExtra("rssi", result.getRssi());
                intent.putExtra("timestamp", Instant.now().toString());
                intent.putExtra("address", result.getDevice().getAddress());
                sendBroadcast(intent);
            }
        };

        BlueToothLEManager blueToothLEManager = new BlueToothLEManager(scanPeriod, leScanCallback);

        while (keepScanning)
            try {
                Log.i(TAG, "onHandleIntent: start scan");
                blueToothLEManager.scanLeDevice();
                Log.i(TAG, "onHandleIntent: done scan, wait");
                Thread.sleep(waitPeriod);
                Log.i(TAG, "onHandleIntent: done waiting");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
    }

    @Override
    public void onDestroy() {
        keepScanning = false;
        sendBroadcast(new Intent(SCAN_STATE_CHANGED));
        Toast.makeText(this, "Stopped Scanning", Toast.LENGTH_SHORT).show();
    }

}