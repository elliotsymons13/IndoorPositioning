package com.example.elliotsymons.positioningtestbed;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elliotsymons.positioningtestbed.R;

public class MainActivity extends AppCompatActivity {

    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

    }

    /**
     * Enable WiFi if not already enabled.
     */
    private void enableWifi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    /**
     * Get basic info about WiFi state and print some of it to the screen.
     *
     * Gets:
     *  - SSID
     *  -
     */
    public void getWifiInfo(View view) {
        enableWifi();

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (String.valueOf(wifiInfo.getSupplicantState()).equals("COMPLETED")) {
            TextView tv = (TextView) findViewById(R.id.tv_info);
            String text = "";

            text += "SSID: " + wifiInfo.getSSID() + "\n";
            text += "RSSI: " + wifiInfo.getRssi() + "\n";


            tv.setText(text);
        } else {
            Toast.makeText(this, "Please connect...", Toast.LENGTH_SHORT).show();
        }
    }
}
