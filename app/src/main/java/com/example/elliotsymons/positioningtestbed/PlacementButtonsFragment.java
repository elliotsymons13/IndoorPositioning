package com.example.elliotsymons.positioningtestbed;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.StageProvider;


public class PlacementButtonsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "PlacementButtonsFragmen";

    TextView tvInfo;
    private StageProvider stageProvider;

    public PlacementButtonsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_placement_buttons, container, false);
        tvInfo = (TextView) view.findViewById(R.id.tv_info);
        stageProvider = (StageProvider) getActivity();
        if (stageProvider == null)
            Log.e(TAG, "onCreateView: STAGE PROVIDER NULL");

        Button btn_multipurpose= (Button) view.findViewById(R.id.btn_multiPurpose);
        btn_multipurpose.setOnClickListener(this);
        Button btn_left = (Button) view.findViewById(R.id.btn_left);
        btn_left.setOnClickListener(this);
        Button btn_up = (Button) view.findViewById(R.id.btn_up);
        btn_up.setOnClickListener(this);
        Button btn_right = (Button) view.findViewById(R.id.btn_right);
        btn_right.setOnClickListener(this);
        Button btn_down = (Button) view.findViewById(R.id.btn_down);
        btn_down.setOnClickListener(this);
        Button btn_finish = (Button) view.findViewById(R.id.btn_finishFingerprinting);
        btn_finish.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_up:
            case R.id.btn_down:
            case R.id.btn_left:
            case R.id.btn_right:
                Log.e(TAG, "onClick: direction");
                ((PlacementFingerprintingActivity) getActivity()).directionClick(v);
                updateButtonStates(stageProvider.getStage());
                break;
            case R.id.btn_finishFingerprinting:
                Log.e(TAG, "onClick: finish");
                ((PlacementFingerprintingActivity) getActivity()).finishCapturing(v);
                updateButtonStates(stageProvider.getStage());
                break;
            case R.id.btn_multiPurpose:
                Log.e(TAG, "onClick: place/capture");
                ((PlacementFingerprintingActivity) getActivity()).placeOrCaptureStep();
                updateButtonStates(stageProvider.getStage());
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
            tvInfo.setText(Integer.toString(args.getInt("x")) + ", " + Integer.toString(args.getInt("y")));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateButtonStates(stageProvider.getStage());
    }



    public void updateButtonStates(String stage) {
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
                getView().invalidate();
                placeCaptureButton.setText(R.string.capture);
                break;
            case "Capture":
                placeCaptureButton.setEnabled(false);
                getView().invalidate();
                break;
        }
    }
}
