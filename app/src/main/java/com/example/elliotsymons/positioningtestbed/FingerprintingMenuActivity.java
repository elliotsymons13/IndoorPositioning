package com.example.elliotsymons.positioningtestbed;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class FingerprintingMenuActivity extends AppCompatActivity {
    int mapID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprinting_menu);
        //mapID = getIntent().getIntExtra("mapID", 0);

        getSupportActionBar().setTitle("WiFi fingerprinting menu");
    }


    public void gridMethodSelected(View view) {
        Toast.makeText(this, "Grid method not yet implemented", Toast.LENGTH_SHORT).show();
    }
    public void placementMethodSelected(View view) {
        Intent transitionToPlacementFingerprinting = new Intent(getBaseContext(),
                PlacementFingerprintingActivity.class);
        //transitionToPlacementFingerprinting.putExtra("mapID", mapID);
        startActivity(transitionToPlacementFingerprinting);
    }
}
