package com.example.horsetracker;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.horsetracker.ble.BleScannerService;
import com.example.horsetracker.database.LogLineDatabaseHelper;
import com.example.horsetracker.database.model.LogLine;
import com.example.horsetracker.ble.BlueToothLEManager;

import java.util.Arrays;
import java.util.stream.Collectors;


public class MainActivity extends AppCompatActivity {


    private Intent intent;
    private Button startButton;
    private Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(BlueToothLEManager.PERMISSIONS, 1);
        } else {
            Log.i("Permissions", "Existent");
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this,
                    "BLUETOOTH_LE not supported in this device!",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        setContentView(R.layout.activity_main);

        BroadcastReceiver broadcastReceiver = new DataUpdateReceiver(this);
        registerReceiver(broadcastReceiver, new IntentFilter(BleScannerService.SCAN_STATE_CHANGED));
        registerReceiver(broadcastReceiver, new IntentFilter(BleScannerService.NEW_ENTRY));

        refreshText();

        this.intent = new Intent(this, BleScannerService.class);

        startButton = findViewById(R.id.start);
        stopButton = findViewById(R.id.stop);
        Button clearButton = findViewById(R.id.clear);

        stopButton.setEnabled(false);

        startButton.setOnClickListener(view -> {
            this.startForegroundService(this.intent);
        });

        clearButton.setOnClickListener(view -> {
            new LogLineDatabaseHelper(this).clearLogLines();
            refreshText();
        });

        stopButton.setOnClickListener(view -> {
            this.stopService(this.intent);
        });
    }

    private void refreshText() {
        try (LogLineDatabaseHelper databaseHelper = new LogLineDatabaseHelper(this)) {
            TextView textView = this.findViewById(R.id.log);
            String lines = databaseHelper.getAllLogLines().stream()
                    .map(LogLine::toDisplayString)
                    .collect(Collectors.joining("\n"));
            textView.setText(lines);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshText();
    }

    private class DataUpdateReceiver extends BroadcastReceiver {

        private final MainActivity mainActivity;

        public DataUpdateReceiver(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (BleScannerService.SCAN_STATE_CHANGED.equals(intent.getAction())) {
                stopButton.setEnabled(!stopButton.isEnabled());
                startButton.setEnabled(!startButton.isEnabled());
            } else if (BleScannerService.NEW_ENTRY.equals(intent.getAction())) {

                int rssi = intent.getIntExtra("rssi", 1);
                if (rssi == 1) return;
                String address = intent.getStringExtra("address");
                String timestamp = intent.getStringExtra("timestamp");

                try (LogLineDatabaseHelper logLineDatabaseHelper = new LogLineDatabaseHelper(this.mainActivity)) {
                    logLineDatabaseHelper.insertLogLine(rssi, timestamp, address);
                    TextView textView = this.mainActivity.findViewById(R.id.log);
                    String lines = logLineDatabaseHelper.getAllLogLines().stream()
                            .map(LogLine::toDisplayString)
                            .collect(Collectors.joining("\n"));
                    textView.setText(lines);
                }
            }
        }
    }
}