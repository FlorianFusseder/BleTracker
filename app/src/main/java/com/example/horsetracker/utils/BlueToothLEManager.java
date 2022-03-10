package com.example.horsetracker.utils;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.horsetracker.MainActivity;

public class BlueToothLEManager {

    private final BluetoothLeScanner bluetoothLeScanner;

    public static final String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private boolean scanning;
    private final Handler handler = new Handler();

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 5000;


    public BlueToothLEManager(BluetoothManager bluetoothManager) {
        this.scanning = false;
        BluetoothAdapter adapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = adapter.getBluetoothLeScanner();
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
            ScanSettings build = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();


            bluetoothLeScanner.startScan(null, build, leScanCallback);
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
            Log.i("BleScan", "Got callback!");
        }
    };


}
