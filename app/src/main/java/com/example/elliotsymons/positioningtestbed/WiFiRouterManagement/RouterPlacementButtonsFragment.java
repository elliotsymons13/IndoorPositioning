package com.example.elliotsymons.positioningtestbed.WiFiRouterManagement;


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

import com.example.elliotsymons.positioningtestbed.R;
import com.example.elliotsymons.positioningtestbed.RouterPlacementActivity;


public class RouterPlacementButtonsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "RouterButtonsFragment";

    TextView tvInfo;

    DatasetStatusListener datasetStatusListener;
    public interface DatasetStatusListener {
        void clearDataset();
    }


    public RouterPlacementButtonsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_router_placement_buttons, container, false);
        tvInfo = (TextView) view.findViewById(R.id.tv_info);

        Button btn_multipurpose= (Button) view.findViewById(R.id.btn_multiPurpose);
        btn_multipurpose.setOnClickListener(this);
        ImageButton btn_left = view.findViewById(R.id.btn_left);
        btn_left.setOnClickListener(this);
        ImageButton btn_up = view.findViewById(R.id.btn_up);
        btn_up.setOnClickListener(this);
        ImageButton btn_right = view.findViewById(R.id.btn_right);
        btn_right.setOnClickListener(this);
        ImageButton btn_down = view.findViewById(R.id.btn_down);
        btn_down.setOnClickListener(this);
        Button btn_finish = (Button) view.findViewById(R.id.btn_finishPlacing);
        btn_finish.setOnClickListener(this);
        Button btn_delete = (Button) view.findViewById(R.id.btn_deleteDataset);
        btn_delete.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_up:
            case R.id.btn_down:
            case R.id.btn_left:
            case R.id.btn_right:
                Log.d(TAG, "onClick: direction");
                ((RouterPlacementActivity) getActivity()).directionClick(v);
                break;
            case R.id.btn_finishPlacing:
                Log.d(TAG, "onClick: finish");
                ((RouterPlacementActivity) getActivity()).finishCapturing(v);
                break;
            case R.id.btn_multiPurpose:
                Log.d(TAG, "onClick: place/capture");
                ((RouterPlacementActivity) getActivity()).placeRouter(v);
                break;
            case R.id.btn_deleteDataset:
                Log.d(TAG, "onClick: delete routers");
                AlertDialog.Builder confirmDeleteDialog = new AlertDialog.Builder(getActivity());
                confirmDeleteDialog.setTitle("Really delete all routers?");
                confirmDeleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        datasetStatusListener.clearDataset();
                        Log.d(TAG, "onClick: Routers deleted by user");
                    }
                });
                confirmDeleteDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        Log.d(TAG, "onClick: delete routers cancelled by user");
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
    public void onResume() {
        super.onResume();
        //updateButtonStates(stageProvider.getStage());
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
}
