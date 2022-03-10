package com.example.horsetracker.utils;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.example.horsetracker.MainActivity;
import com.example.horsetracker.R;
import com.example.horsetracker.database.DatabaseHelper;
import com.example.horsetracker.database.model.LogLine;

import java.time.Instant;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class BlueToothLEManager {


    public static final String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private final BluetoothLeScanner bluetoothLeScanner;
    private final MainActivity activity;
    private boolean scanning;
    private final Handler handler = new Handler();
    private static final long SCAN_PERIOD = 5000;
    private static final long SCAN_PAUSE = 1000 * 60 * 5;


    public BlueToothLEManager(MainActivity activity) {
        this.scanning = false;
        this.activity = activity;
        this.bluetoothLeScanner = ((BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE))
                .getAdapter()
                .getBluetoothLeScanner();
    }

    public void scanLeDevice() {
        if (!scanning) {
            // Stops scanning after a predefined scan period.
            handler.postDelayed(() -> {
                scanning = false;
                bluetoothLeScanner.stopScan(leScanCallback);
                Log.i("BleScan", "Stopped");
            }, SCAN_PERIOD);

            scanning = true;
            ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setScanMode(ScanSettings.CALLBACK_TYPE_FIRST_MATCH)
                    .build();

            ScanFilter filter = new ScanFilter.Builder()
                    .setDeviceAddress("7C:2F:80:90:44:3D")
                    .build();

            ArrayList<ScanFilter> scanFilters = new ArrayList<>();
            scanFilters.add(filter);

            bluetoothLeScanner.startScan(scanFilters, settings, leScanCallback);
            Log.i("BleScan", "Started, Runtime: " + SCAN_PERIOD);
        } else {
            scanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
            Log.i("BleScan", "Stopped, because already running");
        }
    }


    private final ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("BleScan", "Got callback: " + result.getDevice());
            try (DatabaseHelper databaseHelper = new DatabaseHelper(activity)) {
                databaseHelper.insertLogLine(result.getRssi(), Instant.now().toString(), result.getDevice().getAddress());
            }

            try (DatabaseHelper databaseHelper = new DatabaseHelper(activity)) {
                TextView textView = activity.findViewById(R.id.log);
                String lines = databaseHelper.getAllLogLines().stream()
                        .map(LogLine::toDisplayString)
                        .collect(Collectors.joining("\n"));
                textView.setText(lines);
            }
        }
    };

    public void stopScan() {
        this.scanning = false;
    }
}
