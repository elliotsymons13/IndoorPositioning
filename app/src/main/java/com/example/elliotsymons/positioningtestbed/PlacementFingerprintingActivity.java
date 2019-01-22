package com.example.elliotsymons.positioningtestbed;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;


public class PlacementFingerprintingActivity extends AppCompatActivity {

    MapView myMapView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement_fingerprinting);

        getSupportActionBar().setTitle("Fingerprint capture");

        //Add MapView (map) programmatically
        myMapView = new MapView(this);
        setContentView(myMapView);

        //myMapView.updateBlueDot(30,30);
    }


}
