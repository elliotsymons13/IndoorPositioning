package com.example.elliotsymons.positioningtestbed;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MapViewFragment extends Fragment {
    private View rootView;

    private MyMapView myMapView;

    private int startX, startY;



    public MapViewFragment() {
        // Required empty public constructor
    }

    public int getCurrentX() {
        return myMapView.getBlueDot_x();
    }

    public int getCurrentY() {
        return myMapView.getBlueDot_y();
    }

    public void setCurrentX(int x) {
        myMapView.setBlueDot_x(x);
    }

    public void setCurrentY(int y) {
        myMapView.setBlueDot_y(y);
    }

    public void resetBlueDot() {
        myMapView.updateBlueDot(startX,startY);
    }

    public void setBlueDotLocked() {
        myMapView.setBlueDotLocked(true);
    }

    public void setBlueDotUnlocked() {
        myMapView.setBlueDotLocked(false);
    }

    public boolean blueDotLocked() {
        return myMapView.isBlueDotLocked();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        myMapView.updateBlueDot(startX,startY);

        return rootView;

    }



}
