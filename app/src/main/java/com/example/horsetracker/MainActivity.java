package com.example.horsetracker;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.horsetracker.ble.BleScannerService;
import com.example.horsetracker.database.LogLineDatabaseHelper;
import com.example.horsetracker.database.model.LogLine;
import com.example.horsetracker.ble.BlueToothLEManager;

import java.util.Arrays;
import java.util.stream.Collectors;


public class MainActivity extends AppCompatActivity {


    private Intent intent;
    private BroadcastReceiver broadcastReceiver;
    private Button startButton;
    private Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED
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

        try (LogLineDatabaseHelper databaseHelper = new LogLineDatabaseHelper(this)) {
            TextView textView = this.findViewById(R.id.log);
            String lines = databaseHelper.getAllLogLines().stream()
                    .map(LogLine::toDisplayString)
                    .collect(Collectors.joining("\n"));
            textView.setText(lines);
        }

        this.intent = new Intent(this, BleScannerService.class);

        startButton = findViewById(R.id.start);
        stopButton = findViewById(R.id.stop);
        stopButton.setEnabled(false);

        startButton.setOnClickListener(view -> {
            this.startService(this.intent);
        });

        stopButton.setOnClickListener(view -> {
            this.stopService(this.intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) broadcastReceiver = new DataUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter(BleScannerService.SCAN_STATE_CHANGED);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (broadcastReceiver != null) unregisterReceiver(broadcastReceiver);
    }

    private class DataUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!BleScannerService.SCAN_STATE_CHANGED.equals(intent.getAction()))
                return;

            stopButton.setEnabled(!stopButton.isEnabled());
            startButton.setEnabled(!startButton.isEnabled());
        }
    }
}