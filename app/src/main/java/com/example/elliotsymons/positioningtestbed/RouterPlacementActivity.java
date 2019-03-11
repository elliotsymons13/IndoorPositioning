package com.example.elliotsymons.positioningtestbed;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.FingerprintManager;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.FingerprintPlacementButtonsFragment;
import com.example.elliotsymons.positioningtestbed.WiFiRouterManagement.JSONRouterManager;
import com.example.elliotsymons.positioningtestbed.WiFiRouterManagement.RouterManager;
import com.example.elliotsymons.positioningtestbed.WiFiRouterManagement.RouterPlacementButtonsFragment;

public class RouterPlacementActivity extends AppCompatActivity implements MapViewFragment.LocationPassListener {
    private static final String TAG = "RouterPlacementActivity";

    private MapViewFragment map;
    private RouterPlacementButtonsFragment buttons;
    int mapID;

    private RouterManager rm;

    @Override
    public void passLocation(int x, int y) {
        //TODO?
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router_placement);
        getSupportActionBar().setTitle("Router placement");

        map = (MapViewFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_mapViewRouter);
        buttons = (RouterPlacementButtonsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_placementButtonsRouter);
        mapID = getIntent().getIntExtra("mapID", 0);
        map.setMapBackground(mapID);

        rm = JSONRouterManager.getInstance(getApplicationContext());
        new RouterLoaderTask().execute();
        Log.i(TAG, "onCreate: Loaded routers from file");
        //TODO
    }

    private class RouterLoaderTask extends AsyncTask<Void, Void, Void> {
        public static final String TAG = "RouterLoaderTask";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //TODO disable button
        }

        @Override
        protected Void doInBackground(Void... voids) {
            rm.loadIfNotAlready();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //TODO enable button
            super.onPostExecute(aVoid);
        }
    }

    public void directionClick(View v) {
        int increment = 1;
        switch (v.getId()) {
            case R.id.btn_up:
                map.setCurrentY(map.getCurrentY() - increment);
                break;
            case R.id.btn_right:
                map.setCurrentX(map.getCurrentX() + increment);
                break;
            case R.id.btn_down:
                map.setCurrentY(map.getCurrentY() + increment);
                break;
            case R.id.btn_left:
                map.setCurrentX(map.getCurrentX() - increment);
                break;
            default:
                Log.w(TAG, "Invalid direction received");
        }
    }
    
    public void placeRouter(View v) {
        Log.d(TAG, "placeRouter: called");

        //TODO


        
    }


    public void finishCapturing(View view) {
        Intent intent = new Intent(this, WiFiHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        new RouterSaverTask().execute(rm);
        Log.i(TAG, "onDestroy: Saving routers to file");
        super.onDestroy();
    }

    private static class RouterSaverTask extends AsyncTask<RouterManager, Void, Void> {
        public static final String TAG = "RouterSaverTask";

        @Override
        protected Void doInBackground(RouterManager... rm) {
            rm[0].save();
            return null;
        }
    }

}
