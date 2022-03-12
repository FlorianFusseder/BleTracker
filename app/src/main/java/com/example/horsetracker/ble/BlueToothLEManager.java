package com.example.horsetracker.ble;

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

import com.example.horsetracker.MainActivity;
import com.example.horsetracker.database.LogLineDatabaseHelper;

import java.time.Instant;
import java.util.ArrayList;

public class BlueToothLEManager {


    public static final String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.FOREGROUND_SERVICE,
    };

    private final BluetoothLeScanner bluetoothLeScanner;
    private boolean scanning;
    private final Handler handler = new Handler();
    private final long scanPeriod;
    private final ScanCallback callback;


    public BlueToothLEManager(long scanPeriod, ScanCallback callback) {
        this.scanPeriod = scanPeriod;
        this.callback = callback;
        this.scanning = false;
        this.bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
    }

    public void scanLeDevice() {
        if (!scanning) {
            // Stops scanning after a predefined scan period.
            handler.postDelayed(() -> {
                scanning = false;
                bluetoothLeScanner.stopScan(callback);
                Log.i("BleScan", "Stopped");
            }, this.scanPeriod);

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

            bluetoothLeScanner.startScan(scanFilters, settings, callback);
            Log.i("BleScan", "Started, Runtime: " + scanPeriod);
        } else {
            scanning = false;
            bluetoothLeScanner.stopScan(callback);
            Log.i("BleScan", "Stopped, because already running");
        }
    }

}
