package com.example.elliotsymons.positioningtestbed;


import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment wrapper around a MapView.
 */
public class MapViewFragment extends Fragment {
    private static final String TAG = "MapViewFragment";

    public static final int GENERIC_DOT = 1;
    public static final int TRILATERATION_DOT = 2;
    public static final int FINGERPRINT_DOT = 3;

    private MapView mapView;
    public static int startX, startY;

    public MapViewFragment() {
        // Required empty public constructor
    }

    public MapView getMapView() {
        return mapView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map_view, container, false);

        ConstraintLayout rootLayout = rootView.findViewById(R.id.constraintLayout);
        ConstraintSet set = new ConstraintSet();
        mapView = new MapView(getContext());
        mapView.setId(View.generateViewId());
        mapView.setLayoutParams(new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootLayout.addView(mapView,0);

        //Add constraints
        set.clone(rootLayout);
        set.connect(mapView.getId(), ConstraintSet.TOP, rootLayout.getId(), ConstraintSet.TOP);
        set.connect(mapView.getId(), ConstraintSet.LEFT, rootLayout.getId(), ConstraintSet.LEFT);
        set.connect(mapView.getId(), ConstraintSet.RIGHT, rootLayout.getId(), ConstraintSet.RIGHT);
        set.connect(mapView.getId(), ConstraintSet.BOTTOM, rootLayout.getId(), ConstraintSet.BOTTOM);
        set.applyTo(rootLayout);

        //Move dot to starting location (center)
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);
        startX = displaySize.x/2;
        startY = mapView.getMapHeight()/2;

        return rootView;

    }


}
