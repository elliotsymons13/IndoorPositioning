package com.example.elliotsymons.positioningtestbed;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MapViewFragment extends Fragment {
    View rootView;

    MyMapView myMapView;

    private int currentX;
    private int currentY;

    public MapViewFragment() {
        // Required empty public constructor
    }

    public void setCurrentX(int x) {
        currentX = x;
        myMapView.updateBlueDot(currentX, currentY);
        //TODO?
    }

    public void setCurrentY(int y) {
        currentY = y;
        myMapView.updateBlueDot(currentX, currentY);
        //TODO?
    }

    public void setCurrentPosition(int x, int y) {
        setCurrentX(x);
        setCurrentY(y);
    }

    public int getCurrentX() { return currentX; }
    public int getCurrentY() { return currentY; }


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
        currentX = myMapView.getMapWidth()/2;
        currentY = myMapView.getMapHeight()/2;
        myMapView.updateBlueDot(currentX,currentY);

        return rootView;

    }

}
