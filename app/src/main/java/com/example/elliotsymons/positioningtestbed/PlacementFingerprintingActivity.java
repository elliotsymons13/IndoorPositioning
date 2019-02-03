package com.example.elliotsymons.positioningtestbed;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


public class PlacementFingerprintingActivity extends AppCompatActivity {
    private final String TAG = "Pl.Fing.Activity";

    private MapViewFragment map;
    private PlacementButtonsFragment buttons;

    public int mapWidth;
    public int mapHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement_fingerprinting);
        getSupportActionBar().setTitle("Fingerprint capture");

        map = (MapViewFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_mapView);
        buttons = (PlacementButtonsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_placementButtons);

        //TODO ???

    }

    public void directionClick(View v) {
        int increment = 10;
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




}
