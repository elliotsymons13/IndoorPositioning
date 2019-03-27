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

import static android.app.Activity.RESULT_OK;
import static android.support.v4.content.ContextCompat.startActivity;

public class MapViewFragment extends Fragment {
    private static final String TAG = "MapViewFragment";
    private View rootView;

    public static final int GENERIC_DOT = 1;
    public static final int TRILAT_DOT = 2;
    public static final int FINGERPRINT_DOT = 3;


    private MyMapView myMapView;

    public static int startX, startY;

    LocationPassListener locationPassListener;
    public interface LocationPassListener {
        public void passLocation(int x, int y);
    }

    public MapViewFragment() {
        // Required empty public constructor
    }

    public MyMapView getMyMapView() {
        return myMapView;
    }

    public int getCurrentX(int dotID) {
        return myMapView.getDotX(dotID);
    }

    public int getCurrentY(int dotID) {
        return myMapView.getDotY(dotID);
    }

    public void setCurrentX(int dotID, int x) {
        myMapView.setDotX(dotID, x);
    }

    public void setCurrentY(int dotID, int y) {
        myMapView.setDotY(dotID, y);
    }

    public void hideNavDot(int dotID) {
        myMapView.hideNavDot(dotID);
    }

    public void showNavDot(int dotID) {
        myMapView.showNavDot(dotID);
    }

    public void lockNavDot(int dotID) {
        myMapView.lockNavDot(dotID);
    }

    public void unlockNavDot(int dotID) {
        myMapView.unlockNavDot(dotID);
    }

    public void addNavDot(int ID, int x, int y, int colourResource) {
        myMapView.addNavDot(ID, x, y, colourResource);
    }

    public void addPersistentDot(int x, int y) { myMapView.addPersistentDot(x, y);}

    public void setMapBackground(int mapResourceID) { myMapView.setMapBackground(mapResourceID);}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //make sure the required interfaces are implemented by the parent activity
        try {
            locationPassListener = (LocationPassListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement LocationPassListener");
        }
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

        //Move dot to starting location (center)
        startX = myMapView.getMapWidth()/2;
        startY = myMapView.getMapHeight()/2;

        return rootView;

    }


}
