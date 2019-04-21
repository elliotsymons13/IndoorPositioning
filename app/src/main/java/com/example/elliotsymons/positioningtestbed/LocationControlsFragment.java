package com.example.elliotsymons.positioningtestbed;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

/**
 * Fragment containing all the UI elements for the Locating activity.
 */
public class LocationControlsFragment extends Fragment implements
        SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "LocationControlsFragmen";

    private TextView pathLossTV, correlationThresholdTV;
    private SeekBar pathlossSeekBar, correlationSeekBar;
    private Button locateButton;
    private int pathLossProgress;
    private int correlationProgress;


    public LocationControlsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location_buttons, container, false);
        pathlossSeekBar = (SeekBar) view.findViewById(R.id.seekBar_pathLoss);
        correlationSeekBar = view.findViewById(R.id.seekBar_correlationThreshold);
        pathlossSeekBar.setOnSeekBarChangeListener(this);
        correlationSeekBar.setOnSeekBarChangeListener(this);
        pathLossTV = (TextView) view.findViewById(R.id.tv_pathLoss);
        correlationThresholdTV = view.findViewById(R.id.tv_correlationThreshold);
        locateButton = view.findViewById(R.id.btn_locate);

        //initialise seek bar-associated variables based on initial bar state
        pathLossProgress = pathlossSeekBar.getProgress();
        correlationProgress = correlationSeekBar.getProgress();

        double scaledProgress = (((double) pathLossProgress) / 10);
        ((WiFiLocatingActivity) getActivity()).setPathLossExponent(scaledProgress);
        setPathLossText(scaledProgress);

        setCorrelationText(correlationProgress);
        ((WiFiLocatingActivity) getActivity()).setCorrelationThreshold(correlationProgress);
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
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        if (progress == 0) {
            Toast.makeText(getContext(), "Cannot be 0", Toast.LENGTH_SHORT).show();
            locateButton.setEnabled(false);
        }
        switch(seekBar.getId()) {
            case R.id.seekBar_pathLoss:
                pathLossProgress = progress;
                double scaledProgress = (((double) progress) / 10);
                ((WiFiLocatingActivity) getActivity()).setPathLossExponent(scaledProgress);
                setPathLossText(scaledProgress);
                break;
            case R.id.seekBar_correlationThreshold:
                correlationProgress = progress;
                ((WiFiLocatingActivity) getActivity()).setCorrelationThreshold(progress);
                setCorrelationText(progress);

        }
        if (correlationProgress != 0 && pathLossProgress != 0) {
            locateButton.setEnabled(true);
            correlationSeekBar.setEnabled(true);
            pathlossSeekBar.setEnabled(true);
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

    public interface LocationControllerListener {
        void updateLocation(View view);
    }

}
