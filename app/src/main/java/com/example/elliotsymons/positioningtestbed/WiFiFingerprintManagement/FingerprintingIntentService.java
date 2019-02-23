package com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.elliotsymons.positioningtestbed.R;

import java.util.HashSet;
import java.util.Set;

import static com.example.elliotsymons.positioningtestbed.App.CHANNEL_ID;

public class FingerprintingIntentService extends IntentService {
    private static final String TAG = "Fingerpr.IntentServ";

    public FingerprintingIntentService() {
        super(TAG);
        setIntentRedelivery(false); //TODO true if we want service to restart if killed by system


    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: Creating notification for IntentService");

        //Notification must be shown on oreo and higher (API 26+) to maintain service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("WiFi Fingerprinting Service")
                    .setContentText("Running...")
                    .setSmallIcon(R.drawable.ic_wifi_black)
                    .build();

            startForeground(1, notification);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent: called");

        //TODO

        //TODO get scan result (see POC earlier on)
        //TODO extract needed values
        //TODO pass to fingerprint manager

        Set<Capture> captures = new HashSet<>();
        captures.add(new Capture("mac15", -32));
        captures.add(new Capture("mac65", -45));
//        fm.addFingerprint(25,26, captures);
//        fm.save();

        //TODO trigger location state update
        //TODO trigger 'stage 2' in UI thread

        //TODO ...

        Log.d(TAG, "onHandleIntent: Finished waiting");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: IntentService closed");
    }
}
