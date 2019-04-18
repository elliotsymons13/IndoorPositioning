package com.example.elliotsymons.positioningtestbed;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.elliotsymons.positioningtestbed.WiFiRouterManagement.JSONRouterManager;
import com.example.elliotsymons.positioningtestbed.WiFiRouterManagement.RouterManager;
import com.example.elliotsymons.positioningtestbed.WiFiRouterManagement.RouterPlacementButtonsFragment;
import com.example.elliotsymons.positioningtestbed.WiFiRouterManagement.RouterPoint;

import java.util.Objects;
import java.util.Set;

import static com.example.elliotsymons.positioningtestbed.MapViewFragment.GENERIC_DOT;
import static com.example.elliotsymons.positioningtestbed.MapViewFragment.startX;
import static com.example.elliotsymons.positioningtestbed.MapViewFragment.startY;

/**
 * Activity allowing users to place routers on a map and enter their details.
 */
public class RouterPlacementActivity extends AppCompatActivity implements
        RouterPlacementButtonsFragment.DatasetStatusListener, TextWatcher {
    private static final String TAG = "RouterPlacementActivity";
    Preferences prefs;
    UtilityMethods utils;

    private MapViewFragment map;
    private RouterPlacementButtonsFragment buttons;
    Button placeCaptureButton;
    MapView mapView;

    private RouterManager rm;

    EditText etMAC;
    EditText etPower;
    private AlertDialog routerAlertDialog;
    private Button acceptBtn;

    /**
     * Remove all routers from both the UI and the file system.
     */
    @Override
    public void clearDataset() {
        JSONRouterManager.getInstance(getApplicationContext()).deleteAllRouters();
        mapView.removeAllPeristentDots();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router_placement);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Router placement");
        utils = new UtilityMethods(getApplicationContext());

        map = (MapViewFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_mapViewRouter);
        mapView = map.getMapView();
        mapView.addNavDot(GENERIC_DOT, startX, startY, R.color.colorGenericDot);
        mapView.setNavDotRadius(GENERIC_DOT, 15);
        buttons = (RouterPlacementButtonsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_placementButtonsRouter);
        prefs = Preferences.getInstance(getApplicationContext());
        placeCaptureButton = buttons.getView().findViewById(R.id.btn_multiPurpose);

        // Load routers from file
        rm = JSONRouterManager.getInstance(getApplicationContext());
        new RouterLoaderTask().execute();
        Log.i(TAG, "onCreate: Loaded routers from file");

        drawExistingRouters();
    }

    /**
     * Draw all the existing routers on the map.
     */
    public void drawExistingRouters() {
        Set<RouterPoint> existingRouters = rm.getAllRouters();
        for (RouterPoint point : existingRouters) {
            mapView.addPersistentDot(point.x, point.y);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        /* Not used*/
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        /* Not used*/
    }

    @Override
    public void afterTextChanged(Editable editable) {
        boolean macValid = false;
        boolean powerValid = false;

        if (routerAlertDialog != null) {

            // Check power input
            String powerValue = etPower.getText().toString();
            if (powerValue.equals("")) {
                etPower.setError("Enter a power value");
            } else if (!powerValue.matches("^[0-9]+(.[0-9]+)?$")) { // and positive number
                etPower.setError("Positive number needed");
            } else {
                powerValid = true;
            }

            // Check MAC address input
            String macValue = etMAC.getText().toString();
            if (macValue.equals("")) {
                etMAC.setError("Enter a MAC");
            } else if (!macValue.matches("^[a-fA-F0-9:]{17}|[a-fA-F0-9]{12}$")){
                etMAC.setError("Invalid MAC address format");
            } else {
                macValid = true;
            }
        }

        // Set dialog accept button accordingly:
        if (powerValid && macValid) {
            acceptBtn.setEnabled(true);
        } else {
            acceptBtn.setEnabled(false);
        }
    }

    /**
     * Task to asynchronously load all of the routers from the file
     */
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

    /**
     * Handle a press of any of the directional buttons.
     * @param v Button view
     */
    public void directionClick(View v) {
        int increment = 1;
        switch (v.getId()) {
            case R.id.btn_up:
                mapView.setDotY(GENERIC_DOT, mapView.getDotY(GENERIC_DOT) - increment);
                break;
            case R.id.btn_right:
                mapView.setDotX(GENERIC_DOT, mapView.getDotX(GENERIC_DOT) + increment);
                break;
            case R.id.btn_down:
                mapView.setDotY(GENERIC_DOT, mapView.getDotY(GENERIC_DOT) + increment);
                break;
            case R.id.btn_left:
                mapView.setDotX(GENERIC_DOT, mapView.getDotX(GENERIC_DOT) - increment);
                break;
            default:
                Log.w(TAG, "Invalid direction received");
        }
    }

    /**
     * Method called when the user presses 'place router'.
     *
     * Allows the user ot enter the detaisl for a new router (in a dialog popup).
     * Saves this new router to file, while also adding to the UI.
     * @param v
     */
    public void placeRouter(View v) {
        Log.d(TAG, "placeRouter: called");
        //Lock blue dot
        mapView.lockNavDot(GENERIC_DOT);

        //Popup for router entry
        AlertDialog.Builder routerAlertDialogBuilder = new AlertDialog.Builder(this);
        routerAlertDialogBuilder.setTitle("Enter MAC address");

        //Set the content of the popup
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_addrouter, null);
        routerAlertDialogBuilder.setView(dialogView);
        etMAC = dialogView.findViewById(R.id.et_mac);
        etPower = dialogView.findViewById(R.id.et_power);

        //Set popup buttons
        routerAlertDialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                utils.closeKeyboard();
                boolean success;
                try {
                    success = rm.addRouter(mapView.getDotX(GENERIC_DOT),
                            mapView.getDotY(GENERIC_DOT), etMAC.getText().toString(),
                            Double.parseDouble(etPower.getText().toString()));
                } catch (NumberFormatException e) {
                    Log.d(TAG, "onClick: No power entered for router");
                    success = false;
                }

                if (success) {
                    mapView.addPersistentDot(mapView.getDotX(GENERIC_DOT), mapView.getDotY(GENERIC_DOT));
                    Log.d(TAG, "onClick: " + "Added " + etMAC.getText().toString() +
                            " @ " + mapView.getDotX(GENERIC_DOT) + ", " + mapView.getDotY(GENERIC_DOT)
                    + ", TxPower = " + etPower.getText().toString());
                    Toast.makeText(RouterPlacementActivity.this, "Added "
                            + etMAC.getText().toString() +  " @ "
                            + mapView.getDotX(GENERIC_DOT) + ", " + mapView.getDotY(GENERIC_DOT)
                            + ", TxPower = " + etPower.getText().toString()
                            , Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RouterPlacementActivity.this,
                            "Did not add duplicate MAC", Toast.LENGTH_SHORT).show();
                }

            }
        });
        routerAlertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                utils.closeKeyboard();
                Log.d(TAG, "onClick: Add MAC cancelled by user");
            }
        });
        routerAlertDialog = routerAlertDialogBuilder.show();
        acceptBtn = routerAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        acceptBtn.setEnabled(false);
        etMAC.addTextChangedListener(this);
        etPower.addTextChangedListener(this);
        utils.showKeyboard();
        mapView.unlockNavDot(GENERIC_DOT);
    }

    /**
     * Tidy up and return to the main activity (called when the user presses 'save/exit').
     * @param view Button view.
     */
    public void finishCapturing(View view) {
        Intent intent = new Intent(this, WiFiHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        new RouterSaverTask().execute(rm);
        Log.i(TAG, "onDestroy: Saving routers to file");
        utils.closeKeyboard();
    }

    /**
     * Task for saving routers to file asynchronously.
     */
    private static class RouterSaverTask extends AsyncTask<RouterManager, Void, Void> {
        public static final String TAG = "RouterSaverTask";

        @Override
        protected Void doInBackground(RouterManager... rm) {
            rm[0].save();
            return null;
        }
    }

}
