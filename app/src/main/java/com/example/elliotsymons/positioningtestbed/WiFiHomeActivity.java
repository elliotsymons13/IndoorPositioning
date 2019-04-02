package com.example.elliotsymons.positioningtestbed;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.elliotsymons.positioningtestbed.MapManagement.MapData;
import com.example.elliotsymons.positioningtestbed.MapManagement.MapManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class WiFiHomeActivity extends AppCompatActivity implements MapsRecyclerViewAdapter.ItemClickListener, TextWatcher {

    private static final String TAG = "WiFiHomeActivity";

    private static final int PERMISSIONS_RQ_FINE_LOCATION = 2;
    public static final int PERMISSIONS_RQ_WRITE_EXTERNAL = 3;

    public static final int PICK_IMAGE_REQUEST = 1;

    WifiManager wifiManager;
    Preferences prefs;
    UtilityMethods utils;
    MapsRecyclerViewAdapter mapListAdapter;
    private MapManager mapManager;
    private final float MAP_SCALING_THRESHOLD = 1.5f;

    String newMapName;
    AlertDialog mapNameAlertDialog;
    Button acceptBtn;
    EditText mapNameInput;
    boolean nameValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_home);
        utils = new UtilityMethods(getApplicationContext());

        Objects.requireNonNull(getSupportActionBar()).setTitle("WiFi positioning");

        //Set up wifi manager
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        enableWifi();

        //Set up preferences singleton
        prefs = Preferences.getInstance(getApplicationContext());
        mapManager = MapManager.getInstance(getApplicationContext());


        // ( Non-dangerous permissions are granted automatically and do not need checking.)
        // Location permission ('dangerous') needs checked.
        // For android versions higher than 6.0 (API 23).  Versions earlier than this not supported. :
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: Location permission not granted. Requesting grant. ");
            requestLocationPermission();
        }

        // External storage permission ('dangerous') needs checked:
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: External storage permission not granted. Requesting grant. ");
            requestStoragePermission();
        }


        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (manager != null && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d(TAG, "onCreate: Location not enabled. Requesting enable. ");
            requestLocationEnabled();
        }
        ArrayList<MapData> mapNames = (ArrayList<MapData>)
                MapManager.getInstance(getApplicationContext()).loadMaps();
        if (mapNames == null) {
            Log.d(TAG, "onCreate: Initialised empty map list");
            mapNames = new ArrayList<>();
        } else {
            Log.d(TAG, "onCreate: List of maps loaded from file");
        }


        RecyclerView mapRecyclerView = findViewById(R.id.rv_maps);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mapRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mapRecyclerView.getContext(),
                layoutManager.getOrientation());
        mapRecyclerView.addItemDecoration(dividerItemDecoration);

        mapListAdapter = new MapsRecyclerViewAdapter(this, mapNames);
        mapListAdapter.setClickListener(this);
        mapRecyclerView.setAdapter(mapListAdapter);

        if (mapListAdapter.getItemCount() == 0)
            prefs.setMapURI(null); //to ensure the user cannot proceed until maps are added
        else
            prefs.setMapURI(mapListAdapter.getItem(0).getMapURI());
    }

    @Override
    public void onResume() {
        super.onResume();
        mapListAdapter.setSelectedRow(mapManager.getSelected());
    }

    private void requestLocationPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location permission must be enabled for this app to function. Enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        grantLocationPermission();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        Toast.makeText(WiFiHomeActivity.this, "App may function incorrectly", Toast.LENGTH_LONG).show();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void grantLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_RQ_FINE_LOCATION);
    }

    private void requestStoragePermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Storage permission needed to add images. Enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        grantStoragePermission();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        Toast.makeText(WiFiHomeActivity.this, "Permission required, exiting", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void grantStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSIONS_RQ_WRITE_EXTERNAL);
    }

    private void requestLocationEnabled() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location setting must be enabled for this app to function. Enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        Toast.makeText(WiFiHomeActivity.this, "Permission required, exiting", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }); 
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_RQ_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted
                } else {
                    //permission was not granted
                    Toast.makeText(this, "Location permissions must be granted", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
    

    /**
     * Enable WiFi if not already enabled.
     */
    private void enableWifi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            Toast.makeText(this, "Enabled WiFi", Toast.LENGTH_SHORT).show();
        }
    }

    public void routerPlacementSelected(View view) {
        if (prefs.getMapURI() == null) {
            Toast.makeText(this, "Select map before proceeding", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent transitionToRouterPlacement = new Intent(getBaseContext(),
                RouterPlacementActivity.class);
        //transitionToRouterPlacement.putExtra("mapURI", mapURI);
        startActivity(transitionToRouterPlacement);
    }

    /**
     * Transition to fingerprinting menu activity.
     */
    public void fingerprintingSelected(View view) {
        if (prefs.getMapURI() == null) {
            Toast.makeText(this, "Select map before proceeding", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent transitionToFingerprinting = new Intent(getBaseContext(),
                FingerprintPlacementActivity.class);
        //transitionToFingerprinting.putExtra("mapURI", mapURI);
        startActivity(transitionToFingerprinting);
    }

    public void locatingSelected(View view) {
        if (prefs.getMapURI() == null) {
            Toast.makeText(this, "Select map before proceeding", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent transitionToLocating = new Intent(getBaseContext(),
                WiFiLocatingActivity.class);
        //transitionToLocating.putExtra("mapURI", mapURI);
        startActivity(transitionToLocating);
    }


    Bitmap newMapBitmap;
    boolean mapBitmapSelected;

    public void addMapBackground(View view) {
        Log.d(TAG, "addMapBackground: Adding new map");
        mapBitmapSelected = false;

        //Popup for map name entry
        AlertDialog.Builder mapNameAlertDialogBuilder = new AlertDialog.Builder(this);
        mapNameAlertDialogBuilder.setTitle("Add new map");

        //Set the content of the popup
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_addmap, null);
        mapNameAlertDialogBuilder.setView(dialogView);
        mapNameInput = dialogView.findViewById(R.id.et_mapName);


        //Set popup buttons
        mapNameAlertDialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                utils.closeKeyboard();
                newMapName = mapNameInput.getText().toString();
                Log.d(TAG, "onClick: Entered " + newMapName);
                if (mapBitmapSelected) {
                    //check ratio of image size to check it is reasonably square
                    int height = newMapBitmap.getHeight();
                    int width = newMapBitmap.getWidth();

                    if (height/width > MAP_SCALING_THRESHOLD || width/height > MAP_SCALING_THRESHOLD) {
                        Log.d(TAG, "onClick: Image of inappropriate size");
                        Toast.makeText(WiFiHomeActivity.this,
                                "Inappropriately proportioned image", Toast.LENGTH_SHORT).show();
                        Toast.makeText(WiFiHomeActivity.this,
                                "Image should be 3:2 or 'squarer'", Toast.LENGTH_LONG).show();
                    } else {
                        String newMapUri = getImageUri(getApplicationContext(), newMapBitmap).toString();
                        MapData newMap = new MapData(newMapName, newMapUri);
                        mapManager.addMap(newMap); //add to persistent list
                        mapListAdapter.notifyDataSetChanged(); //inform UI to check list
                    }

                } else {
                    Toast.makeText(WiFiHomeActivity.this,
                            "No image selected, so no map added", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mapNameAlertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                utils.closeKeyboard();
            }
        });
        mapNameAlertDialog = mapNameAlertDialogBuilder.show();
        acceptBtn = mapNameAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        acceptBtn.setEnabled(false);
        mapNameInput.addTextChangedListener(this);
        utils.showKeyboard();
    }

    private void setDialogButtonStatus() {
        if (nameValid && mapBitmapSelected) {
            acceptBtn.setEnabled(true);
        } else {
            acceptBtn.setEnabled(false);
        }
    }

    public Uri getImageUri(Context context, Bitmap inImage) {
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void selectNewMapFile(View view) {
        //Select map image resource
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                newMapBitmap = MediaStore.Images.Media.getBitmap(
                        getApplicationContext().getContentResolver(), uri);
                mapBitmapSelected = true;
                setDialogButtonStatus();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onItemClick(View view, int position) {
        mapListAdapter.setSelectedRow(position);
        prefs.setMapURI(mapListAdapter.getItem(position).getMapURI());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MapManager.getInstance(getApplicationContext()).saveMaps();
        utils.closeKeyboard();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        /* Not used */
        nameValid = false;
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        /* Not used */
    }

    @Override
    public void afterTextChanged(Editable editable) {

        // Check name input
        newMapName = mapNameInput.getText().toString();
        if (mapNameAlertDialog != null) {
            if (newMapName.equals("")) {
                // map name needs to be non-blank
                mapNameInput.setError("Enter a name");
            } else {
                // input is valid
                nameValid = true;
            }
        }
        setDialogButtonStatus();
    }
}
