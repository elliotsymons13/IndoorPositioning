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
import com.example.elliotsymons.positioningtestbed.WiFiRouterManagement.JSONRouterManager;
import com.example.elliotsymons.positioningtestbed.WiFiRouterManagement.RouterManager;
import com.example.elliotsymons.positioningtestbed.WiFiRouterManagement.RouterPlacementButtonsFragment;
import com.example.elliotsymons.positioningtestbed.WiFiRouterManagement.RouterPoint;

import java.util.Set;

import static com.example.elliotsymons.positioningtestbed.MapViewFragment.GENERIC_DOT;
import static com.example.elliotsymons.positioningtestbed.MapViewFragment.startX;
import static com.example.elliotsymons.positioningtestbed.MapViewFragment.startY;

public class RouterPlacementActivity extends AppCompatActivity implements MapViewFragment.LocationPassListener, RouterPlacementButtonsFragment.DatasetStatusListener {
    private static final String TAG = "RouterPlacementActivity";

    private MapViewFragment map;
    private RouterPlacementButtonsFragment buttons;
    Button placeCaptureButton;
    MyMapView myMapView;
    Preferences prefs;

    private RouterManager rm;

    @Override
    public void passLocation(int x, int y) {
        //TODO?
    }

    @Override
    public void clearDataset() {
        //TODO
        myMapView.removeAllPeristentDots();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router_placement);
        getSupportActionBar().setTitle("Router placement");

        map = (MapViewFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_mapViewRouter);
        myMapView = map.getMyMapView();
        myMapView.addNavDot(GENERIC_DOT, startX, startY, R.color.colorGenericDot);
        myMapView.setNavDotRadius(GENERIC_DOT, 15);
        buttons = (RouterPlacementButtonsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_placementButtonsRouter);
        prefs = Preferences.getInstance(getApplicationContext());

        placeCaptureButton = (Button) buttons.getView().findViewById(R.id.btn_multiPurpose);


        rm = JSONRouterManager.getInstance(getApplicationContext());
        new RouterLoaderTask().execute();
        Log.i(TAG, "onCreate: Loaded routers from file");

        drawExistingRouters();
    }

    public void drawExistingRouters() {
        Set<RouterPoint> existingRouters = rm.getAllRouters();
        for (RouterPoint point : existingRouters) {
            map.addPersistentDot(point.getX(), point.getY());
        }
    }

    private class RouterLoaderTask extends AsyncTask<String, Void, Void> {
        public static final String TAG = "RouterLoaderTask";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            placeCaptureButton.setEnabled(false);

        }

        @Override
        protected Void doInBackground(String... strings) {
            rm.loadIfNotAlready();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            drawExistingRouters();
            placeCaptureButton.setEnabled(true);
            super.onPostExecute(aVoid);
        }
    }

    public void directionClick(View v) {
        int increment = 1;
        switch (v.getId()) {
            case R.id.btn_up:
                map.setCurrentY(GENERIC_DOT, map.getCurrentY(GENERIC_DOT) - increment);
                break;
            case R.id.btn_right:
                map.setCurrentX(GENERIC_DOT, map.getCurrentX(GENERIC_DOT) + increment);
                break;
            case R.id.btn_down:
                map.setCurrentY(GENERIC_DOT, map.getCurrentY(GENERIC_DOT) + increment);
                break;
            case R.id.btn_left:
                map.setCurrentX(GENERIC_DOT, map.getCurrentX(GENERIC_DOT) - increment);
                break;
            default:
                Log.w(TAG, "Invalid direction received");
        }
    }
    
    public void placeRouter(View v) {
        Log.d(TAG, "placeRouter: called");
        //Lock blue dot
        map.lockNavDot(GENERIC_DOT);

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
                rm.addRouter(map.getCurrentX(GENERIC_DOT), map.getCurrentY(GENERIC_DOT),
                        input.getText().toString());
                map.addPersistentDot(map.getCurrentX(GENERIC_DOT), map.getCurrentY(GENERIC_DOT));
                Log.d(TAG, "onClick: " + "Added " + input.getText().toString() +
                        " @ " + map.getCurrentX(GENERIC_DOT) + ", " + map.getCurrentY(GENERIC_DOT));
                Toast.makeText(RouterPlacementActivity.this, "Added "
                        + input.getText().toString() +  " @ "
                        + map.getCurrentX(GENERIC_DOT) + ", " + map.getCurrentY(GENERIC_DOT), Toast.LENGTH_SHORT).show();
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

        map.unlockNavDot(GENERIC_DOT);
        
    }


    public void finishCapturing(View view) {
        Intent intent = new Intent(this, WiFiHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void loadRouters() {
        //Popup for filename entry
        AlertDialog.Builder filenameAlertDialog = new AlertDialog.Builder(this);
        filenameAlertDialog.setTitle("Enter custom filename");

        //Set the content of the popup
        final EditText input = new EditText(getApplicationContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        filenameAlertDialog.setView(input);

        //Set popup buttons
        filenameAlertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String filename = input.getText().toString() + ".json";
                new RouterLoaderTask().execute(filename);
                Toast.makeText(getApplicationContext(), "Using file specified now", Toast.LENGTH_SHORT).show();
            }
        });
        filenameAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                Log.d(TAG, "onClick: Add custom filename cancelled by user");
            }
        });
        filenameAlertDialog.show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        prefs.savePrefs(getApplicationContext()); //FIXME call elsewhere also?
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
