package com.example.elliotsymons.positioningtestbed;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.FingerprintManager;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.FingerprintPlacementButtonsFragment;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.FingerprintPoint;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.FingerprintingIntentService;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.JSONFingerprintManager;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.StageProvider;

import java.util.Set;

import static com.example.elliotsymons.positioningtestbed.MapViewFragment.GENERIC_DOT;
import static com.example.elliotsymons.positioningtestbed.MapViewFragment.startX;
import static com.example.elliotsymons.positioningtestbed.MapViewFragment.startY;

/**
 * Activity allowing users to place fingerprints on the map, and instruct the capture of data at 
 * that point. 
 */
public class FingerprintPlacementActivity extends AppCompatActivity implements
        StageProvider, FingerprintPlacementButtonsFragment.DatasetStatusListener {
    private final String TAG = "Pl.Fing.Activity";
    Preferences prefs;

    private MapViewFragment map;
    MapView mapView;
    private FingerprintPlacementButtonsFragment buttons;
    private Button placeCaptureButton;

    private String stage = "Place";

    private FingerprintManager fm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement_fingerprinting);
        getSupportActionBar().setTitle("Fingerprint capture");

        // Setup map
        map = (MapViewFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_mapView);
        mapView = map.getMapView();
        //Add a generic dot, to be placed by the users for fingerprints
        mapView.addNavDot(GENERIC_DOT, startX, startY, R.color.colorGenericDot);
        mapView.setNavDotRadius(GENERIC_DOT, 15);

        buttons = (FingerprintPlacementButtonsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_placementButtons);
        prefs = Preferences.getInstance(getApplicationContext());
        placeCaptureButton = (Button) buttons.getView().findViewById(R.id.btn_multiPurpose);

        // Load fingerprints from file
        fm = JSONFingerprintManager.getInstance(getApplicationContext());
        new FingerprintLoaderTask().execute();
        Log.i(TAG, "onCreate: Loaded fingerprints from file");

        // Register to receive termination signals from background service
        LocalBroadcastManager.getInstance(this).registerReceiver(msgReceiver,
                new IntentFilter("fingerprinting-finished"));

        drawExistingFingerprints();

    }

    @Override
    public String getStage() {
        return stage;
    }

    @Override
    /**
     * Remove all fingerprints from both the UI and the file system.
     */
    public void clearDataset() {
        JSONFingerprintManager.getInstance(getApplicationContext()).deleteAllFingerprints();
        mapView.removeAllPeristentDots();
    }

    /**
     * Draw all the existing fingerprints on the map.
     */
    public void drawExistingFingerprints() {
        Set<FingerprintPoint> existingFingerprints = fm.getAllFingerprints();
        for (FingerprintPoint point : existingFingerprints) {
            mapView.addPersistentDot(point.x, point.y);
        }
    }

    /**
     * Task to asynchronously load all of the fingerprints from the file
     */
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

    /**
     * Start (or continue) the foreground service to fingerprint the point specified.
     * @param x X coordinate for the resulting fingerprint point.
     * @param y Y coordinate.
     */
    private void startFingerprintService(int x, int y) {
        Intent serviceIntent = new Intent(this, FingerprintingIntentService.class);
        serviceIntent.putExtra("x", x);
        serviceIntent.putExtra("y", y);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    /**
     * Handle a press of any of the directional buttons.
     * @param v Button view
     */
    public void directionClick(View v) {
        int increment = 1;
        switch (v.getId()) {
            case R.id.btn_up:
                mapView.setDotY(GENERIC_DOT, mapView.getDotY(GENERIC_DOT) - increment);
                break;
            case R.id.btn_right:
                mapView.setDotX(GENERIC_DOT, mapView.getDotX(GENERIC_DOT) + increment);
                break;
            case R.id.btn_down:
                mapView.setDotY(GENERIC_DOT, mapView.getDotY(GENERIC_DOT) + increment);
                break;
            case R.id.btn_left:
                mapView.setDotX(GENERIC_DOT, mapView.getDotX(GENERIC_DOT) - increment);
                break;
            default:
                Log.w(TAG, "Invalid direction received");
        }
    }

    /**
     * Control flow method for fingerprinting process.
     *
     * The user can be in one of 3 stages:
     * - Place: the user is able to position the dot to specify the fingerprint location,
     * using the arrow keys, or pressing directly on the map.
     * - Locked: The user can no longer move the dot.
     * - Capture: The fingerprinting process is in progress, and the user cannot do anything.
     *
     * This method handles the actions as a result of ENTERING the state in the case statement.
     */
    public void placeOrCaptureStep() {
        Log.d(TAG, "placeOrCaptureStep: Called");
        switch (stage) {
            case "Place":
                //User is to place the fingerprint mapBitmap
                stage = "Locked";
                //Lock blue dot
                mapView.lockNavDot(GENERIC_DOT);
                //Change button text
                placeCaptureButton.setText(R.string.capture);
                break;
            case "Locked":
                //User has pressed capture. Phone needs to record RSSI values.
                Toast.makeText(this, "Fingerprinting...", Toast.LENGTH_SHORT).show();
                stage = "Capture";
                startFingerprintService(mapView.getDotX(GENERIC_DOT),
                        mapView.getDotY(GENERIC_DOT));
                break;
            case "Capture":
                //Capture is complete
                stage = "Place";

                mapView.addPersistentDot(mapView.getDotX(GENERIC_DOT),
                        mapView.getDotY(GENERIC_DOT));
                mapView.unlockNavDot(GENERIC_DOT);
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

    /**
     * Tidy up and return to the main activity (called when the user presses 'save/exit').
     * @param view Button view.
     */
    public void finishCapturing(View view) {
        Intent intent = new Intent(this, WiFiHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    @Override
    protected void onPause() {
        super.onPause();
        new FingerprintSaverTask().execute(fm);
        Log.i(TAG, "onDestroy: Saving fingerprints to file");
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(msgReceiver);
        Log.i(TAG, "onDestroy: Unregistered receivers");
        super.onDestroy();
    }

    /**
     * Task for saving fingerprints to file asynchronously.
     */
    private static class FingerprintSaverTask extends AsyncTask<FingerprintManager, Void, Void> {
        public static final String TAG = "FingerprintSaverTask";

        @Override
        protected Void doInBackground(FingerprintManager... fm) {
            fm[0].save();
            return null;
        }
    }



}
