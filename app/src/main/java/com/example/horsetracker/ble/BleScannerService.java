package com.example.horsetracker.ble;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
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

import com.example.horsetracker.R;

import java.time.Instant;

public class BleScannerService extends Service {

    public static final String SCAN_STATE_CHANGED = "BLESCANNER_SCAN_STATE_CHANGED";
    public static final String NEW_ENTRY = "BLESCANNER_NEW_ENTRY";

    private ServiceHandler serviceHandler;
    private Looper serviceLooper;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {


        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
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

            BlueToothLEManager blueToothLEManager = new BlueToothLEManager(5000, leScanCallback);

//            try {
            blueToothLEManager.scanLeDevice();
//                Thread.sleep(1000 * 60);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
        }
    }


    public BleScannerService() {
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Started Scanning", Toast.LENGTH_SHORT).show();
        sendBroadcast(new Intent(SCAN_STATE_CHANGED));

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not Implemented");
    }


    @Override
    public void onDestroy() {
        sendBroadcast(new Intent(SCAN_STATE_CHANGED));

        Toast.makeText(this, "Stopped Scanning", Toast.LENGTH_SHORT).show();

    }

}