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

        //Check for location permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //If permission is NOT granted, request it:
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_RQ_FINE_LOCATION);
        } else {
            Toast.makeText(this, "Location permission already granted", Toast.LENGTH_SHORT).show();//else, the permission is already granted...
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
    

    /**
     * Enable WiFi if not already enabled.
     */
    private void enableWifi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            Toast.makeText(this, "Enabled WiFi", Toast.LENGTH_SHORT).show();
        }
    }



}
