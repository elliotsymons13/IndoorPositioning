package com.example.elliotsymons.positioningtestbed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elliotsymons.positioningtestbed.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        enableWifi();
        registerReceiver(wifiScanReciever,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

    }

    private final BroadcastReceiver wifiScanReciever = new BroadcastReceiver() {
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
