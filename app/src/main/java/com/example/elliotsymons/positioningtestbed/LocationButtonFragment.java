package com.example.elliotsymons.positioningtestbed;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class LocationButtonFragment extends Fragment {

    private LocationControllerFragmentInteractionListener locationControllerListener;

    public LocationButtonFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location_buttons, container, false);
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
        void updateLocation(View view);
    }

}
