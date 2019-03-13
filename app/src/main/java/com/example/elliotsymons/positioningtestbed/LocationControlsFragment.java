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


public class LocationControlsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "LocationControlsFragmen";

    private LocationControllerFragmentInteractionListener locationControllerListener;


    public LocationControlsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location_buttons, container, false);

        RadioButton radioButton_fingerprinting = view.findViewById(R.id.rb_fingerprinting);
        RadioButton radioButton_trilateraton = view.findViewById(R.id.rb_trilateration);
        radioButton_fingerprinting.setOnClickListener(this);
        radioButton_trilateraton.setOnClickListener(this);
        Button btnLocate = view.findViewById(R.id.btn_locate);
        btnLocate.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // Method selectors:
            case R.id.rb_fingerprinting:
                Log.d(TAG, "onClick: fingerprinting mode");
                locationControllerListener.selectMode(
                        LocationControllerFragmentInteractionListener.MODE_FINGERPRINTING
                );
                break;
            case R.id.rb_trilateration:
                Log.d(TAG, "onClick: trilateration mode");
                locationControllerListener.selectMode(
                        LocationControllerFragmentInteractionListener.MODE_TRILATERATION
                );
                break;

            case R.id.btn_locate:
                Log.d(TAG, "onClick: locate");
                locationControllerListener.updateLocation(view);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //make sure the required interfaces are implemented by the parent activity
        try {
            locationControllerListener = (LocationControllerFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement LocationPassListener");
        }
    }


    public interface LocationControllerFragmentInteractionListener {
        int MODE_FINGERPRINTING = 1;
        int MODE_TRILATERATION = 2;

        void updateLocation(View view);
        void selectMode(int mode);
    }

}
