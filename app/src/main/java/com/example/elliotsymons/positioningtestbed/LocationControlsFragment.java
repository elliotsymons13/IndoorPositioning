package com.example.elliotsymons.positioningtestbed;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Fragment containing all the UI elements for the Locating activity.
 */
public class LocationControlsFragment extends Fragment implements
        SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "LocationControlsFragmen";

    TextView pathLossTV;


    public LocationControlsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location_buttons, container, false);
        SeekBar pathLoss = (SeekBar) view.findViewById(R.id.seekBar_pathLoss);
        pathLoss.setOnSeekBarChangeListener(this);
        pathLossTV = (TextView) view.findViewById(R.id.tv_pathLoss);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //make sure the required interfaces are implemented by the parent activity
        try {
            LocationControllerFragmentInteractionListener locationControllerListener = (LocationControllerFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement LocationControllerFragmentInteractionListener");
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch(seekBar.getId()) {
            case R.id.seekBar_pathLoss:
                ((WiFiLocatingActivity) getActivity()).setPathLossExponent(i);
                pathLossTV.setText("PthLss\n"+ i + "/10");
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //do nothing
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //do nothing
    }

    public interface LocationControllerFragmentInteractionListener {
        void updateLocation(View view);
    }

}
