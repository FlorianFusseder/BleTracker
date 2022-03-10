package com.example.horsetracker;

import android.Manifest;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.horsetracker.database.DatabaseHelper;
import com.example.horsetracker.database.model.LogLine;
import com.example.horsetracker.utils.BlueToothLEManager;

import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
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

        try (DatabaseHelper databaseHelper = new DatabaseHelper(this)) {
            TextView textView = this.findViewById(R.id.log);
            String lines = databaseHelper.getAllLogLines().stream()
                    .map(LogLine::toDisplayString)
                    .collect(Collectors.joining("\n"));
            textView.setText(lines);
        }

        BlueToothLEManager blueToothLEManager = new BlueToothLEManager(this);


        Button startButton = findViewById(R.id.start);
        startButton.setOnClickListener(view -> {
            Toast.makeText(this,
                    "Started Scanning!",
                    Toast.LENGTH_SHORT).show();

            blueToothLEManager.scanLeDevice();
        });


        Button stopButton = findViewById(R.id.stop);
        stopButton.setOnClickListener(view -> {
            blueToothLEManager.stopScan();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("result", Arrays.toString(grantResults));
    }

}