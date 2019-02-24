package com.example.elliotsymons.positioningtestbed;


import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.Capture;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.FingerprintManager;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.FingerprintingIntentService;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.JSONFingerprintManager;

import java.util.HashSet;
import java.util.Set;


public class PlacementFingerprintingActivity extends AppCompatActivity {
    private final String TAG = "Pl.Fing.Activity";

    private MapViewFragment map;
    private PlacementButtonsFragment buttons;

    //0 represent placing dot, 1 represents capturing dot, 2 represents captured, -1 for not yet ready (file loading)
    private int stage = 0;

    public int mapWidth;
    public int mapHeight;

    private Button placeCaptureButton;

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

        fm = JSONFingerprintManager.getInstance(getApplicationContext());
        new FingerprintLoaderTask().execute();
        Log.i(TAG, "onCreate: Loaded fingerprints from file");

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

    private void startFingerprintService() {
        Intent serviceIntent = new Intent(this, FingerprintingIntentService.class);
        //serviceIntent.putExtra("inputExtra", "Hello world");
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

    public void placeOrCaptureClick(View view) {
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
                startFingerprintService();
                //TODO
                /*Set<Capture> captures = new HashSet<>();
                captures.add(new Capture("mac15", -32));
                captures.add(new Capture("mac65", -45));
                fm.addFingerprint(25,26, captures);
                fm.save();*/

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
        //TODO intent to move back to main screen?
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

}
