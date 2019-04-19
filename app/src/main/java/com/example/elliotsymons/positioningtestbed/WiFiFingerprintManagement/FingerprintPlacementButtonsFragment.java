package com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.elliotsymons.positioningtestbed.FingerprintPlacementActivity;
import com.example.elliotsymons.positioningtestbed.R;

/**
 * Fragment containing control buttons for and associated functionality for placing fingerprints
 * on a map view in the same activity.
 */
public class FingerprintPlacementButtonsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "PlacementButtonsFragmen";

    TextView tvInfo;
    private StageProvider stageProvider;


    DatasetStatusListener datasetStatusListener;
    public interface DatasetStatusListener {
        void clearDataset();
    }

    public FingerprintPlacementButtonsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fingerprint_placement_buttons, container, false);
        tvInfo = (TextView) view.findViewById(R.id.tv_info);
        stageProvider = (StageProvider) getActivity();
        if (stageProvider == null)
            Log.e(TAG, "onCreateView: STAGE PROVIDER NULL");

        Button btn_multipurpose= view.findViewById(R.id.btn_multiPurpose);
        btn_multipurpose.setOnClickListener(this);
        ImageButton btn_left = view.findViewById(R.id.btn_left);
        btn_left.setOnClickListener(this);
        ImageButton btn_up = view.findViewById(R.id.btn_up);
        btn_up.setOnClickListener(this);
        ImageButton btn_right = view.findViewById(R.id.btn_right);
        btn_right.setOnClickListener(this);
        ImageButton btn_down = view.findViewById(R.id.btn_down);
        btn_down.setOnClickListener(this);
        Button btn_delete = view.findViewById(R.id.btn_deleteDataset);
        btn_delete.setOnClickListener(this);
        return view;
    }

    /**
     * Handles all click actions for the fragment on a case-by-case basis.
     * @param view Calling view.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_up:
            case R.id.btn_down:
            case R.id.btn_left:
            case R.id.btn_right:
                Log.d(TAG, "onClick: direction");
                ((FingerprintPlacementActivity) getActivity()).directionClick(view);
                break;
            case R.id.btn_multiPurpose:
                Log.d(TAG, "onClick: place/capture");
                ((FingerprintPlacementActivity) getActivity()).placeOrCaptureStep();
                break;
            case R.id.btn_deleteDataset:
                Log.d(TAG, "onClick: delete fingerprints");
                // dialog for user to confirm irreversible action:
                AlertDialog.Builder confirmDeleteDialog = new AlertDialog.Builder(getActivity());
                confirmDeleteDialog.setTitle("Really delete all fingeprints?");
                confirmDeleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        datasetStatusListener.clearDataset();
                        Log.d(TAG, "onClick: Fingerprints deleted by user");
                    }
                });
                confirmDeleteDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        Log.d(TAG, "onClick: delete fingerprints cancelled by user");
                    }
                });
                confirmDeleteDialog.show();
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
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            datasetStatusListener = (DatasetStatusListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DatasetStatusListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateButtonStates(stageProvider.getStage());
    }

    /**
     * Update the disables/enabled state of all fragment buttons based on the stage provided.
     * @param stage Stage in the fingerprinting process.
     */
    public void updateButtonStates(String stage) {
        Button placeCaptureButton = getView().findViewById(R.id.btn_multiPurpose);
        Log.d(TAG, "updateButtonStates: Updating based on stage: " + stage);
        switch (stage) {
            case "Place":
                getView().findViewById(R.id.btn_up).setEnabled(true);
                getView().findViewById(R.id.btn_right).setEnabled(true);
                getView().findViewById(R.id.btn_down).setEnabled(true);
                getView().findViewById(R.id.btn_left).setEnabled(true);
                getView().findViewById(R.id.btn_deleteDataset).setEnabled(true);
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
                getView().findViewById(R.id.btn_deleteDataset).setEnabled(false);
                getView().invalidate();
                break;
        }
    }
}
