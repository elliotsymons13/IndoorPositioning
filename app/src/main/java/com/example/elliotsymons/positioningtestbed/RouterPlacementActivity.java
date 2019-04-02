package com.example.elliotsymons.positioningtestbed;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
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

public class RouterPlacementActivity extends AppCompatActivity implements
        RouterPlacementButtonsFragment.DatasetStatusListener, TextWatcher {
    private static final String TAG = "RouterPlacementActivity";

    private MapViewFragment map;
    private RouterPlacementButtonsFragment buttons;
    Button placeCaptureButton;
    MyMapView myMapView;
    Preferences prefs;
    UtilityMethods utils;

    private RouterManager rm;

    EditText etMAC;
    EditText etPower;
    private AlertDialog routerAlertDialog;
    private Button acceptBtn;

    @Override
    public void clearDataset() {
        JSONRouterManager.getInstance(getApplicationContext()).deleteAllRouters();
        myMapView.removeAllPeristentDots();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router_placement);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Router placement");
        utils = new UtilityMethods(getApplicationContext());

        map = (MapViewFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_mapViewRouter);
        myMapView = map.getMyMapView();
        myMapView.addNavDot(GENERIC_DOT, startX, startY, R.color.colorGenericDot);
        myMapView.setNavDotRadius(GENERIC_DOT, 15);
        buttons = (RouterPlacementButtonsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_placementButtonsRouter);
        prefs = Preferences.getInstance(getApplicationContext());

        placeCaptureButton = buttons.getView().findViewById(R.id.btn_multiPurpose);


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
            } else { //TODO further validation
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
                    success = rm.addRouter(map.getCurrentX(GENERIC_DOT),
                            map.getCurrentY(GENERIC_DOT), etMAC.getText().toString(),
                            Double.parseDouble(etPower.getText().toString()));
                } catch (NumberFormatException e) {
                    Log.d(TAG, "onClick: No power entered for router");
                    success = false;
                }

                if (success) {
                    map.addPersistentDot(map.getCurrentX(GENERIC_DOT), map.getCurrentY(GENERIC_DOT));
                    Log.d(TAG, "onClick: " + "Added " + etMAC.getText().toString() +
                            " @ " + map.getCurrentX(GENERIC_DOT) + ", " + map.getCurrentY(GENERIC_DOT)
                    + ", TxPower = " + etPower.getText().toString());
                    Toast.makeText(RouterPlacementActivity.this, "Added "
                            + etMAC.getText().toString() +  " @ "
                            + map.getCurrentX(GENERIC_DOT) + ", " + map.getCurrentY(GENERIC_DOT)
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

        map.unlockNavDot(GENERIC_DOT);
        
    }


    public void finishCapturing(View view) {
        Intent intent = new Intent(this, WiFiHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /*public void loadRouters() {
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
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        new RouterSaverTask().execute(rm);
        Log.i(TAG, "onDestroy: Saving routers to file");
        utils.closeKeyboard();
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
