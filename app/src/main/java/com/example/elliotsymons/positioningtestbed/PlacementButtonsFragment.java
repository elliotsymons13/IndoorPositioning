package com.example.elliotsymons.positioningtestbed;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class PlacementButtonsFragment extends Fragment {
    private static final String TAG = "PlacementButtonsFragmen";

    TextView tvInfo;
    private String stage = "";
    public void setStage(String stage) { this.stage = stage; }

    public PlacementButtonsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_placement_buttons, container, false);
        tvInfo = (TextView) view.findViewById(R.id.tv_info);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
            tvInfo.setText(Integer.toString(args.getInt("x")) + ", " + Integer.toString(args.getInt("y")));
            this.stage = args.getString("stage");
        }
        updateButtonStates();
    }

    public void updateButtonStates() {
        Button placeCaptureButton = getView().findViewById(R.id.btn_multiPurpose);
        Log.e(TAG, "updateButtonStates: Updating based on stage: " + stage);
        switch (stage) {
            case "Place":
                getView().findViewById(R.id.btn_up).setEnabled(true);
                getView().findViewById(R.id.btn_right).setEnabled(true);
                getView().findViewById(R.id.btn_down).setEnabled(true);
                getView().findViewById(R.id.btn_left).setEnabled(true);
                placeCaptureButton.setText(R.string.place);
                placeCaptureButton.setEnabled(true);
                break;
            case "Locked":
                placeCaptureButton.setEnabled(true);
                getView().findViewById(R.id.btn_up).setEnabled(false);
                getView().findViewById(R.id.btn_right).setEnabled(false);
                getView().findViewById(R.id.btn_down).setEnabled(false);
                getView().findViewById(R.id.btn_left).setEnabled(false);
                placeCaptureButton.setText(R.string.capture);
                break;
            case "Capture":
                placeCaptureButton.setEnabled(false);
                break;
        }
    }
}
