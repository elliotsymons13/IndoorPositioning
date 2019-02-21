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
        Log.d(TAG, "onHandleIntent called");

        SystemClock.sleep(3000);
        Log.d(TAG, "onHandleIntent: Finished waiting");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: IntentService closed");
    }
}
