package com.example.elliotsymons.positioningtestbed;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.preference.Preference;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.FingerprintManager;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.FingerprintPlacementButtonsFragment;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.FingerprintingIntentService;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.JSONFingerprintManager;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.StageProvider;

import static com.example.elliotsymons.positioningtestbed.MapViewFragment.GENERIC_DOT;
import static com.example.elliotsymons.positioningtestbed.MapViewFragment.startX;
import static com.example.elliotsymons.positioningtestbed.MapViewFragment.startY;


public class PlacementFingerprintingActivity extends AppCompatActivity implements MapViewFragment.LocationPassListener, StageProvider {
    private final String TAG = "Pl.Fing.Activity";
    Preferences prefs;

    private MapViewFragment map;
    private FingerprintPlacementButtonsFragment buttons;

    //0 represent placing dot, 1 represents capturing dot, 2 represents captured, -1 for not yet ready (file loading)
    private String stage = "Place";

    public int mapWidth;
    public int mapHeight;
    private int mapID;

    private Button placeCaptureButton;
    private TextView infoTextView;

    private FingerprintManager fm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement_fingerprinting);
        getSupportActionBar().setTitle("Fingerprint capture");

        map = (MapViewFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_mapView);
        MyMapView myMapView = map.getMyMapView();
        myMapView.addNavDot(GENERIC_DOT, startX, startY, R.color.colorGenericDot);
        myMapView.setNavDotRadius(GENERIC_DOT, 15);
        buttons = (FingerprintPlacementButtonsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_placementButtons);
        prefs = Preferences.getInstance(getApplicationContext());
        //mapID = prefs.getMapID();
        //mapID = getIntent().getIntExtra("mapID", 0);
        //map.setMapBackground(mapID);

        placeCaptureButton = (Button) buttons.getView().findViewById(R.id.btn_multiPurpose);

        fm = JSONFingerprintManager.getInstance(getApplicationContext());
        new FingerprintLoaderTask().execute();
        Log.i(TAG, "onCreate: Loaded fingerprints from file");

        LocalBroadcastManager.getInstance(this).registerReceiver(msgReceiver,
                new IntentFilter("fingerprinting-finished"));

    }



    @Override
    public void passLocation(int x, int y) {
        Log.i(TAG, "passLocation: Called");
        /*FingerprintPlacementButtonsFragment newButtons = new FingerprintPlacementButtonsFragment();
        Bundle args = new Bundle();
        args.putInt("x", x);
        args.putInt("y", y);
        args.putString("stage", stage);
        newButtons.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_placementButtons, newButtons).commit();
        buttons = newButtons;*/ //FIXME
    }

    @Override
    public String getStage() {
        return stage;
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
            fm.loadIfNotAlready();
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
                map.setCurrentY(GENERIC_DOT, map.getCurrentY(GENERIC_DOT) - increment);
                break;
            case R.id.btn_right:
                map.setCurrentX(GENERIC_DOT, map.getCurrentX(GENERIC_DOT) + increment);
                break;
            case R.id.btn_down:
                map.setCurrentY(GENERIC_DOT, map.getCurrentY(GENERIC_DOT) + increment);
                break;
            case R.id.btn_left:
                map.setCurrentX(GENERIC_DOT, map.getCurrentX(GENERIC_DOT) - increment);
                break;
            default:
                Log.w(TAG, "Invalid direction received");
        }
    }

    public void placeOrCaptureStep() {
        Log.d(TAG, "placeOrCaptureStep: Called");
        switch (stage) {
            case "Place":
                //User is to place the fingerprint location
                stage = "Locked";

                //Lock blue dot
                map.lockNavDot(GENERIC_DOT);

                //Change button text
                placeCaptureButton.setText(R.string.capture);
                break;
            case "Locked":
                //User has pressed capture. Phone needs to record RSSI values.
                Toast.makeText(this, "Fingerprinting...", Toast.LENGTH_SHORT).show();
                stage = "Capture";
                startFingerprintService(map.getCurrentX(GENERIC_DOT), map.getCurrentY(GENERIC_DOT));
                break;
            case "Capture":
                //Capture is complete
                stage = "Place";

                map.addPersistentDot(map.getCurrentX(GENERIC_DOT), map.getCurrentY(GENERIC_DOT));
                map.unlockNavDot(GENERIC_DOT);
                break;
        }
        buttons.updateButtonStates(stage); //redraws UI with buttons updates to guide user
    }


    private BroadcastReceiver msgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            placeOrCaptureStep();
        }
    };

    public void finishCapturing(View view) {
        Intent intent = new Intent(this, WiFiHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }




    @Override
    protected void onDestroy() {
        new FingerprintSaverTask().execute(fm);
        Log.i(TAG, "onDestroy: Saving fingerprints to file");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(msgReceiver);
        Log.i(TAG, "onDestroy: Unregistered receivers");
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



}
