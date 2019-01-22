package com.example.elliotsymons.positioningtestbed;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class PlacementFingerprintingActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement_fingerprinting);

        getSupportActionBar().setTitle("Fingerprint capture");

        //Add MapView (map) programmatically
        MapView myMapView;
        myMapView = new MapView(this);
        //myMapView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(myMapView);
    }


}
