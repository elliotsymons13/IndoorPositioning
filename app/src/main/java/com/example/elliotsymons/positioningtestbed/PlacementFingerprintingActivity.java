package com.example.elliotsymons.positioningtestbed;

import android.app.ActionBar;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class PlacementFingerprintingActivity extends AppCompatActivity {

    MyMapView myMapView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement_fingerprinting);
        getSupportActionBar().setTitle("Fingerprint capture");

        //Setup constraint layout
        ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.constraintLayout);
        ConstraintSet set = new ConstraintSet();

        //Add buttons to constraint layout
        ConstraintLayout buttons = (ConstraintLayout) View.inflate(
                this, R.layout.capture_button_layout, null);
        buttons.setId(View.generateViewId());
        buttons.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(buttons, 0);

        //Add map to constraint layout
        myMapView = new MyMapView(this);
        //myMapView = new Button(this);
        myMapView.setId(View.generateViewId());
        myMapView.setLayoutParams(new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(myMapView,0);

        //Add constraints
        set.clone(layout);
        set.connect(myMapView.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP);
        set.connect(myMapView.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT);
        set.connect(myMapView.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT);

        set.connect(myMapView.getId(), ConstraintSet.BOTTOM, buttons.getId(), ConstraintSet.TOP, 8);

        set.connect(buttons.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT);
        set.connect(buttons.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT);
        set.connect(buttons.getId(), ConstraintSet.BOTTOM, layout.getId(), ConstraintSet.BOTTOM);
        set.applyTo(layout);

        //myMapView.updateBlueDot(30,30);

    }


}
