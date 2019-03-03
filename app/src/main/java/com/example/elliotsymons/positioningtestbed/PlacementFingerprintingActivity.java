package com.example.elliotsymons.positioningtestbed;


import android.app.IntentService;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.Capture;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.FingerprintManager;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.JSONFingerprintManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.elliotsymons.positioningtestbed.App.CHANNEL_ID;


public class PlacementFingerprintingActivity extends AppCompatActivity implements MapViewFragment.LocationPassListener {
    private final String TAG = "Pl.Fing.Activity";

    private MapViewFragment map;
    private PlacementButtonsFragment buttons;

    //0 represent placing dot, 1 represents capturing dot, 2 represents captured, -1 for not yet ready (file loading)
    private int stage = 0;
    public void setStage(int stage) { this.stage = stage; }
    public int getStage() { return stage; }

    public int mapWidth;
    public int mapHeight;

    private Button placeCaptureButton;
    private TextView infoTextView;

    private FingerprintManager fm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement_fingerprinting);
        getSupportActionBar().setTitle("Fingerprint capture");

        map = (MapViewFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_mapView);
        buttons = (PlacementButtonsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_placementButtons);

        placeCaptureButton = (Button) buttons.getView().findViewById(R.id.btn_multiPurpose);
        //infoTextView = (TextView) buttons.getView().findViewById(R.id.tv_info);

        fm = JSONFingerprintManager.getInstance(getApplicationContext());
        new FingerprintLoaderTask().execute();
        Log.i(TAG, "onCreate: Loaded fingerprints from file");

    }

    @Override
    public void passLocation(int x, int y) {
        Log.i(TAG, "passLocation: Called");
        PlacementButtonsFragment newButtons = new PlacementButtonsFragment();
        Bundle args = new Bundle();
        args.putInt("x", x);
        args.putInt("y", y);
        newButtons.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_placementButtons, newButtons).commit();
        buttons = newButtons;
    }

    private class FingerprintLoaderTask extends AsyncTask<Void, Void, Void> {
        public static final String TAG = "FingerprintLoaderTask";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            placeCaptureButton.setEnabled(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            fm.load();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            placeCaptureButton.setEnabled(true);
            super.onPostExecute(aVoid);
        }
    }

    private void startFingerprintService(int x, int y) {
        Intent serviceIntent = new Intent(this, FingerprintingIntentService.class);
        serviceIntent.putExtra("x", x);
        serviceIntent.putExtra("y", y);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void directionClick(View v) {
        int increment = 1;
        switch (v.getId()) {
            case R.id.btn_up:
                map.setCurrentY(map.getCurrentY() - increment);
                break;
            case R.id.btn_right:
                map.setCurrentX(map.getCurrentX() + increment);
                break;
            case R.id.btn_down:
                map.setCurrentY(map.getCurrentY() + increment);
                break;
            case R.id.btn_left:
                map.setCurrentX(map.getCurrentX() - increment);
                break;
            default:
                Log.w(TAG, "Invalid direction received");
        }
    }

    public void placeOrCaptureClick(View view) { placeOrCaptureStep(); }
    public void placeOrCaptureStep() {
        switch (stage) {
            case 0:
                //User is placing the fingerprint location
                Toast.makeText(this, "Stage 0", Toast.LENGTH_SHORT).show();
                //Lock blue dot
                map.setBlueDotLocked();
                //Disable other buttons
                placeCaptureButton.setEnabled(true);
                findViewById(R.id.btn_up).setEnabled(false);
                findViewById(R.id.btn_right).setEnabled(false);
                findViewById(R.id.btn_down).setEnabled(false);
                findViewById(R.id.btn_left).setEnabled(false);
                //Change button text
                placeCaptureButton.setText(R.string.capture);
                stage++;
                break;
            case 1:
                //User has pressed capture. Phone needs to record RSSI values.
                Toast.makeText(this, "Stage 1", Toast.LENGTH_SHORT).show();
                //Lock all buttons
                placeCaptureButton.setEnabled(false);
                //Inform user of intent
                Toast.makeText(this, "Fingerprinting...", Toast.LENGTH_SHORT).show();
                //TODO Status bar?

                //TODO
                startFingerprintService(map.getCurrentX(), map.getCurrentY());
                //TODO

                //re-enable button etc. only when capture is finished.
                //FIXME stage 2 needs to be triggered asynchronously by the capture completing (below code is redundant/wrong place)
                stage++;
                break;
            case 2:
                //Capture is complete
                Toast.makeText(this, "Stage 2", Toast.LENGTH_SHORT).show();
                //Update user
                findViewById(R.id.btn_up).setEnabled(true);
                findViewById(R.id.btn_right).setEnabled(true);
                findViewById(R.id.btn_down).setEnabled(true);
                findViewById(R.id.btn_left).setEnabled(true);
                placeCaptureButton.setText(R.string.place);
                placeCaptureButton.setEnabled(true);
                //Move on to next capture
                stage = 0;
                break;
        }
    }

    public void finishCapturing(View view) {
        Toast.makeText(this, "Finished...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, WiFiHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        new FingerprintSaverTask().execute(fm);
        Log.i(TAG, "onDestroy: Saving fingerprints to file");
        super.onDestroy();
    }

    private static class FingerprintSaverTask extends AsyncTask<FingerprintManager, Void, Void> {
        public static final String TAG = "FingerprintSaverTask";

        @Override
        protected Void doInBackground(FingerprintManager... fm) {
            fm[0].save();
            return null;
        }
    }

    private class FingerprintingIntentService extends IntentService {
        private static final String TAG = "Fingerpr.IntentServ";

        private boolean resultReceived;

        WifiManager wifiManager;
        FingerprintManager fm;

        public FingerprintingIntentService() {
            super(TAG);
            setIntentRedelivery(false); //TODO true if we want service to restart if killed by system


        }

        @Override
        public void onCreate() {
            super.onCreate();

            //Notification must be shown on oreo and higher (API 26+) to maintain service
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.i(TAG, "onCreate: Creating notification for IntentService");
                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("WiFi Fingerprinting Service")
                        .setContentText("Running...")
                        .setSmallIcon(R.drawable.ic_wifi_black)
                        .build();

                startForeground(1, notification);
            }

            //Set up wifi manager
            wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            enableWifi();
            registerReceiver(wifiScanReceiver,
                    new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

            fm = JSONFingerprintManager.getInstance(getApplicationContext());
        }

        public void postToastMessage(final String message) {
            Handler handler = new Handler(Looper.getMainLooper());

            handler.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        protected void onHandleIntent(@Nullable Intent intent) {
            Log.d(TAG, "onHandleIntent: called");
            int x, y;
            try {
                x = intent.getIntExtra("x", -1);
                y = intent.getIntExtra("y", -1);
            } catch (NullPointerException nptre) {
                Log.e(TAG, "onHandleIntent: NO COORDINATES PROVIDED BY INTENT FROM CALLER");
                nptre.printStackTrace();
                x = -1;
                y = -1;
            }


            resultReceived = false;

            //TODO

            //TODO get scan result (see POC earlier on)
            Log.i(TAG, "onHandleIntent: Requesting scan");
            wifiManager.startScan();
            while (!resultReceived) {
                SystemClock.sleep(100);
            }
            List<ScanResult> scanResults = wifiManager.getScanResults();


            //TODO extract needed values
            //TODO pass to fingerprint manager
            Set<Capture> captures = new HashSet<>();
            for (ScanResult result : scanResults) {
                captures.add(new Capture(result.BSSID, Math.abs(result.level)));
            }
            fm.addFingerprint(x,y,captures);
            fm.save(); //TODO no call here


            //TODO trigger 'stage 2' in UI thread
            Intent intenta = new Intent();
            LocalBroadcastManager.getInstance(PlacementFingerprintingActivity.this).sendBroadcast(intenta);

            //TODO ...

            Log.i(TAG, "onHandleIntent: finished");
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
            Log.i(TAG, "onScanSuccess: Scan result received");
            List<ScanResult> scanResults = wifiManager.getScanResults();
            postToastMessage("Scan completed");
            resultReceived = true;

            //Process results:
            String text = "";
            for (ScanResult result : scanResults) {
                text += "SSID: " + result.SSID + "\n";
                text += "MAC : " + result.BSSID + "\n";
                text += "RSSI: " + result.level + "\n";
            }
            postToastMessage(text);
        }

        private void onScanFailure() {
            postToastMessage("Scan failed");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            unregisterReceiver(wifiScanReceiver);
            Log.d(TAG, "onDestroy: IntentService closed");
        }
    }

}
