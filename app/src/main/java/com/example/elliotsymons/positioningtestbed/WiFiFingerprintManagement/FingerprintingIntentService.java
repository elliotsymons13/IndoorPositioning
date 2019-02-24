package com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement;

import android.app.IntentService;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elliotsymons.positioningtestbed.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.elliotsymons.positioningtestbed.App.CHANNEL_ID;

public class FingerprintingIntentService extends IntentService {
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
        resultReceived = false;

        //TODO

        //TODO get scan result (see POC earlier on)
        Log.i(TAG, "onHandleIntent: Requesting scan");
        wifiManager.startScan();
        while (!resultReceived) {
            SystemClock.sleep(100);
        }



        //TODO extract needed values
        //TODO pass to fingerprint manager

        Set<Capture> captures = new HashSet<>();
        captures.add(new Capture("mac15", -32));
        captures.add(new Capture("mac65", -45));
        fm.addFingerprint(7,7,captures);
//        fm.addFingerprint(25,26, captures);
//        fm.save();

        //TODO trigger location state update
        //TODO trigger 'stage 2' in UI thread

        //TODO ...

        Log.i(TAG, "onHandleIntent: Finished waiting");
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
