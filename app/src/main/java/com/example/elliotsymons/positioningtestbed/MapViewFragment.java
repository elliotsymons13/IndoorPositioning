package com.example.elliotsymons.positioningtestbed;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.util.HashSet;

import static android.app.Activity.RESULT_OK;
import static android.support.v4.content.ContextCompat.startActivity;

public class MapViewFragment extends Fragment {
    private static final String TAG = "MapViewFragment";
    private View rootView;

    public static final int GENERIC_DOT = 1;
    public static final int TRILATERATION_DOT = 2;
    public static final int FINGERPRINT_DOT = 3;


    private MyMapView myMapView;
    public static int startX, startY;

    public MapViewFragment() {
        // Required empty public constructor
    }

    public MyMapView getMyMapView() {
        return myMapView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_map_view, container, false);

        ConstraintLayout rootLayout = (ConstraintLayout) rootView.findViewById(R.id.constraintLayout);
        ConstraintSet set = new ConstraintSet();
        myMapView = new MyMapView(getContext());
        //myMapView = new Button(this);
        myMapView.setId(View.generateViewId());
        myMapView.setLayoutParams(new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootLayout.addView(myMapView,0);

        //Add constraints
        set.clone(rootLayout);
        set.connect(myMapView.getId(), ConstraintSet.TOP, rootLayout.getId(), ConstraintSet.TOP);
        set.connect(myMapView.getId(), ConstraintSet.LEFT, rootLayout.getId(), ConstraintSet.LEFT);
        set.connect(myMapView.getId(), ConstraintSet.RIGHT, rootLayout.getId(), ConstraintSet.RIGHT);
        set.connect(myMapView.getId(), ConstraintSet.BOTTOM, rootLayout.getId(), ConstraintSet.BOTTOM);

        set.applyTo(rootLayout);

        //Move dot to starting mapBitmap (center)
        startX = myMapView.getMapWidth()/2;
        startY = myMapView.getMapHeight()/2;

        return rootView;

    }


}
