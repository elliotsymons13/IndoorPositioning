package com.example.elliotsymons.positioningtestbed;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int PERMISSIONS_RQ_WIFI_STATE = 1; //TODO what value?
    private static final int PERMISSIONS_RQ_FINE_LOCATION = 2;

    WifiManager wifiManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ( Non-dangerous permissions are granted automatically and do not need checking.)

        //Set up wifi manager
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        enableWifi();
        registerReceiver(wifiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        //Check for location permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //If permission is NOT granted, request it:
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_RQ_FINE_LOCATION);
        } else {
            Toast.makeText(this, "Permission location already got", Toast.LENGTH_SHORT).show();//else, the permission is already granted...
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_RQ_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted
                    Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    //permission was not granted
                    Toast.makeText(this, "Location permission required for app to function", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private final BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    onScanSuccess();
                } else {
                    onScanFailure();
                }
            }
        }
    };

    /**
     * Enable WiFi if not already enabled.
     */
    private void enableWifi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    private void onScanSuccess() {
        List<ScanResult> scanResults = wifiManager.getScanResults();
        Toast.makeText(this, "Scan completed", Toast.LENGTH_SHORT).show();

        //Process results:
        TextView tv = findViewById(R.id.tv_info);
        String text = "";
        for (ScanResult result : scanResults) {
            text += "SSID: " + result.SSID + "\n";
            text += "RSSI: " + result.level + "\n";
        }
        tv.setText(text);

    }

    private void onScanFailure() {
        Toast.makeText(this, "Scan failed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Get basic info about WiFi state and print some of it to the screen.
     *
     * Gets:
     *  - SSID
     *  -
     */
    public void getWifiInfo(View view) {
        Toast.makeText(this, "Requesting WiFi scan...", Toast.LENGTH_SHORT).show();
        wifiManager.startScan();
    }
}
