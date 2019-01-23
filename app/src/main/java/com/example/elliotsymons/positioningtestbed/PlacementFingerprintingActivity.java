package com.example.elliotsymons.positioningtestbed;

import android.app.ActionBar;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class PlacementFingerprintingActivity extends AppCompatActivity {

    MyMapView myMapView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement_fingerprinting);
        getSupportActionBar().setTitle("Fingerprint capture");

        //Setup UI root
        RelativeLayout.LayoutParams llp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT); //WRAP_CONTENT
        RelativeLayout llRoot = (RelativeLayout) findViewById(R.id.relativeLayout);




        //Add buttons (programmatically)
        ConstraintLayout buttons = (ConstraintLayout) View.inflate(
                this, R.layout.capture_button_layout, null);
        buttons.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)); //llp);
        llRoot.addView(buttons);

        //Add MapView (map) programmatically
        myMapView = new MyMapView(this);
        myMapView.setLayoutParams(llp);
        llRoot.addView(myMapView);


        //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        //this.addContentView(llRoot, layoutParams);
        //myMapView.updateBlueDot(30,30);

    }


}
