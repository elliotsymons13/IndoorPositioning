package com.example.elliotsymons.positioningtestbed;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class WiFiHomeActivity extends AppCompatActivity {

    private static final String TAG = "WiFiHomeActivity";

    private static final int PERMISSIONS_RQ_FINE_LOCATION = 2;

    WifiManager wifiManager;
    Preferences prefs;

    private int mapID = R.drawable.floor_plan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifihome);

        getSupportActionBar().setTitle("WiFi positioning");

        // ( Non-dangerous permissions are granted automatically and do not need checking.)

        //Set up wifi manager
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        enableWifi();

        //Set up preferences singleton
        prefs = Preferences.getInstance(getApplicationContext());

        //Check for location permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //If permission is NOT granted, request it:
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_RQ_FINE_LOCATION);
        } else {
            //Toast.makeText(this, "Location permission already granted", Toast.LENGTH_SHORT).show();//else, the permission is already granted...
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

    /**
     * Transition to fingerprinting menu activity.
     * @param view
     */
    public void fingerprintingSelected(View view) {
        Intent transitionToFingerprinting = new Intent(getBaseContext(),
                FingerprintingMenuActivity.class);
        //transitionToFingerprinting.putExtra("mapID", mapID);
        startActivity(transitionToFingerprinting);
    }

    public void locatingSelected(View view) {
        Intent transitionToLocating = new Intent(getBaseContext(),
                WiFiLocatingActivity.class);
        //transitionToLocating.putExtra("mapID", mapID);
        startActivity(transitionToLocating);
    }

    public void setMapBackground(View view) {
        switch(view.getId()) {
            case R.id.rb_dcs:
                Log.d(TAG, "onClick: dcs selected");
                mapID = R.drawable.floor_plan;
                break;
            case R.id.rb_home:
                Log.d(TAG, "onClick: home selected");
                mapID = R.drawable.house_floor_plan;
                break;
            case R.id.rb_msb:
                Log.d(TAG, "setMapBackground: msb selected");
                mapID = R.drawable.msb_floor_plan;
                break;
        }
        prefs.setMapID(mapID);
    }

    public void routerPlacementSelected(View view) {
        Intent transitionToRouterPlacement = new Intent(getBaseContext(),
                RouterPlacementActivity.class);
        //transitionToRouterPlacement.putExtra("mapID", mapID);
        startActivity(transitionToRouterPlacement);
    }



}
