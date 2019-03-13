package com.example.elliotsymons.positioningtestbed;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.FingerprintManager;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.FingerprintPlacementButtonsFragment;
import com.example.elliotsymons.positioningtestbed.WiFiRouterManagement.JSONRouterManager;
import com.example.elliotsymons.positioningtestbed.WiFiRouterManagement.RouterManager;
import com.example.elliotsymons.positioningtestbed.WiFiRouterManagement.RouterPlacementButtonsFragment;
import com.example.elliotsymons.positioningtestbed.WiFiRouterManagement.RouterPoint;

public class RouterPlacementActivity extends AppCompatActivity implements MapViewFragment.LocationPassListener {
    private static final String TAG = "RouterPlacementActivity";

    private MapViewFragment map;
    private RouterPlacementButtonsFragment buttons;
    Button placeCaptureButton;
    int mapID;
    Preferences prefs;

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
        //prefs = Preferences.getInstance(getApplicationContext());
        //mapID = prefs.getMapID();
        //map.setMapBackground(mapID);

        placeCaptureButton = (Button) buttons.getView().findViewById(R.id.btn_multiPurpose);


        rm = JSONRouterManager.getInstance(getApplicationContext());
        new RouterLoaderTask().execute();
        Log.i(TAG, "onCreate: Loaded routers from file");

    }

    private class RouterLoaderTask extends AsyncTask<Void, Void, Void> {
        public static final String TAG = "RouterLoaderTask";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            placeCaptureButton.setEnabled(false);

        }

        @Override
        protected Void doInBackground(Void... voids) {
            rm.loadIfNotAlready();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            placeCaptureButton.setEnabled(true);
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
        //Lock blue dot
        map.setBlueDotLocked();

        //Popup for MAC entry
        AlertDialog.Builder macAlertDialog = new AlertDialog.Builder(this);
        macAlertDialog.setTitle("Enter MAC address");

        //Set the content of the popup
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        macAlertDialog.setView(input);

        //Set popup buttons
        macAlertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO save MAC entered locally
                rm.addRouter(map.getCurrentX(), map.getCurrentY(),
                        input.getText().toString());
                map.addPersistentDot(map.getCurrentX(), map.getCurrentY());
                Log.d(TAG, "onClick: " + "Added " + input.getText().toString() +
                        " @ " + map.getCurrentX() + ", " + map.getCurrentY());
                Toast.makeText(RouterPlacementActivity.this, "Added "
                        + input.getText().toString() +  " @ "
                        + map.getCurrentX() + ", " + map.getCurrentY(), Toast.LENGTH_SHORT).show();
            }
        });
        macAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                Log.d(TAG, "onClick: Add MAC cancelled by user");
            }
        });
        macAlertDialog.show();


        map.setBlueDotUnlocked();
        
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
