package com.example.elliotsymons.positioningtestbed;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class PlacementFingerprintingActivity extends AppCompatActivity {
    private final String TAG = "Pl.Fing.Activity";

    private MapViewFragment map;
    private PlacementButtonsFragment buttons;

    //0 represent placing dot, 1 represents capturing dot, 2 represents captured
    private int stage = 0;

    public int mapWidth;
    public int mapHeight;

    private Button placeCaptureButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement_fingerprinting);
        getSupportActionBar().setTitle("Fingerprint capture");

        map = (MapViewFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_mapView);
        buttons = (PlacementButtonsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_placementButtons);

        placeCaptureButton = (Button) buttons.getView().findViewById(R.id.btn_multiPurpose);

        //TODO ???

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
                //Status bar?

                //re-enable button etc. only when capture is finished.
                //FIXME stage 2 needs to be triggered asynchronously by the capture completing (below code is redundant)
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
        //TODO
    }


}
