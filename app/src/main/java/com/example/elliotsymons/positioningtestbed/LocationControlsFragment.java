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
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

/**
 * Fragment containing all the UI elements for the Locating activity.
 */
public class LocationControlsFragment extends Fragment implements
        SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "LocationControlsFragmen";

    TextView pathLossTV, correlationThresholdTV;
    Button locateButton;
    int pathLostProgress;
    int correlationProgress;


    public LocationControlsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location_buttons, container, false);
        SeekBar pathlossSeekBar = (SeekBar) view.findViewById(R.id.seekBar_pathLoss);
        SeekBar correlationSeekBar = view.findViewById(R.id.seekBar_correlationThreshold);
        pathlossSeekBar.setOnSeekBarChangeListener(this);
        correlationSeekBar.setOnSeekBarChangeListener(this);
        pathLossTV = (TextView) view.findViewById(R.id.tv_pathLoss);
        correlationThresholdTV = view.findViewById(R.id.tv_correlationThreshold);
        locateButton = view.findViewById(R.id.btn_locate);

        //initialise seek bar-associated variables based on initial bar state
        pathLostProgress = pathlossSeekBar.getProgress();
        correlationProgress = correlationSeekBar.getProgress();

        double scaledProgress = (((double) pathLostProgress) / 10);
        ((WiFiLocatingActivity) getActivity()).setPathLossExponent(scaledProgress);
        setPathLossText(scaledProgress);

        setCorrelationText(correlationProgress);

        return view;
    }

    private void setPathLossText(double scaledProgress) {
        DecimalFormat df = new DecimalFormat("#.0");
        String scaledRoundedProgress = df.format(scaledProgress);
        pathLossTV.setText("PthLss\n"+ scaledRoundedProgress + "/10");
    }

    private void setCorrelationText(int progress) {
        correlationThresholdTV.setText("Correlation threshold\n"+progress + "%");
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
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        if (progress == 0) {
            Toast.makeText(getContext(), "Cannot be 0", Toast.LENGTH_SHORT).show();
            locateButton.setEnabled(false);
        }
        switch(seekBar.getId()) {
            case R.id.seekBar_pathLoss:
                pathLostProgress = progress;
                double scaledProgress = (((double) progress) / 10);
                ((WiFiLocatingActivity) getActivity()).setPathLossExponent(scaledProgress);
                setPathLossText(scaledProgress);
                break;
            case R.id.seekBar_correlationThreshold:
                correlationProgress = progress;
                ((WiFiLocatingActivity) getActivity()).setCorrelationThreshold(progress);
                setCorrelationText(progress);

        }
        if (correlationProgress != 0 && pathLostProgress != 0) {
            locateButton.setEnabled(true);
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
