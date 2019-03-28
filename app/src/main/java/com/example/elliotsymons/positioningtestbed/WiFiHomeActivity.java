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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.elliotsymons.positioningtestbed.MapManagement.Map;
import com.example.elliotsymons.positioningtestbed.MapManagement.MapData;
import com.example.elliotsymons.positioningtestbed.MapManagement.MapManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class WiFiHomeActivity extends AppCompatActivity implements MapsRecyclerViewAdapter.ItemClickListener {

    private static final String TAG = "WiFiHomeActivity";

    private static final int PERMISSIONS_RQ_FINE_LOCATION = 2;

    public static final int PICK_IMAGE_REQUEST = 1;

    WifiManager wifiManager;
    Preferences prefs;
    MapsRecyclerViewAdapter mapListAdapter;

    private int mapID = R.drawable.msb_floor_plan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifihome);

        getSupportActionBar().setTitle("WiFi positioning");

        //Set up wifi manager
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        enableWifi();

        //Set up preferences singleton
        prefs = Preferences.getInstance(getApplicationContext());
        prefs.setMapID(mapID);

        // ( Non-dangerous permissions are granted automatically and do not need checking.)


        //For android versions higher than 6.0 (API 23).  Versions earlier than this not supported. :
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: Location permission not granted. Requesting grant. ");
            requestLocationPermission();
        }


        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d(TAG, "onCreate: Location not enabled. Requesting enable. ");
            requestLocationEnabled();
        }
        ArrayList<MapData> mapNames = (ArrayList<MapData>)
                MapManager.getInstance(getApplicationContext()).loadMaps();
       if (mapNames == null) {
           Log.d(TAG, "onCreate: Initialised empty map list");
           mapNames= new ArrayList<>();
       } else {
           Log.d(TAG, "onCreate: List of maps loaded from file");
        }
        
         

        RecyclerView mapRecyclerView = findViewById(R.id.rv_maps);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this  );
        mapRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mapRecyclerView.getContext(),
                layoutManager.getOrientation());
        mapRecyclerView.addItemDecoration(dividerItemDecoration);

        mapListAdapter = new MapsRecyclerViewAdapter(this, mapNames);
        mapListAdapter.setClickListener(this);
        mapRecyclerView.setAdapter(mapListAdapter);

    }

    private void grantLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_RQ_FINE_LOCATION);
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
                        Toast.makeText(WiFiHomeActivity.this, "App may function incorrectly", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show();
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

    /**
     * Transition to fingerprinting menu activity.
     * @param view
     */
    public void fingerprintingSelected(View view) {
        Intent transitionToFingerprinting = new Intent(getBaseContext(),
                PlacementFingerprintingActivity.class);
        //transitionToFingerprinting.putExtra("mapID", mapID);
        startActivity(transitionToFingerprinting);
    }

    public void locatingSelected(View view) {
        Intent transitionToLocating = new Intent(getBaseContext(),
                WiFiLocatingActivity.class);
        //transitionToLocating.putExtra("mapID", mapID);
        startActivity(transitionToLocating);
    }

    public void setMapBackground(View view) {
        switch(view.getId()) {
            case R.id.rb_dcs:
                Log.d(TAG, "onClick: dcs selected");
                mapID = R.drawable.floor_plan;
                break;
            case R.id.rb_home:
                Log.d(TAG, "onClick: home selected");
                mapID = R.drawable.house_floor_plan;
                break;
            case R.id.rb_msb:
                Log.d(TAG, "setMapBackground: msb selected");
                mapID = R.drawable.msb_floor_plan;
                break;
        }
        prefs.setMapID(mapID);
    }

    Bitmap newMapBitmap;
    boolean mapBitmapSelected;

    public void addMapBackground(View view) {
        Log.d(TAG, "addMapBackground: Adding new map");
        mapBitmapSelected = false;

        //Popup for map name entry
        AlertDialog.Builder mapNameAlertDialog = new AlertDialog.Builder(this);
        mapNameAlertDialog.setTitle("Enter map name");

        //Set the content of the popup
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_addmap, null);
        mapNameAlertDialog.setView(dialogView);
        final EditText input = dialogView.findViewById(R.id.et_mapName);

        //Set popup buttons
        mapNameAlertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO validate input
                String newMapName = input.getText().toString();
                Log.d(TAG, "onClick: Entered " + newMapName);
                if (mapBitmapSelected) {
                    String newMapUri = getImageUri(getApplicationContext(), newMapBitmap).toString();
                    mapListAdapter.addItem(new MapData(newMapName, newMapUri));
                    mapListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(WiFiHomeActivity.this,
                            "No image selected, so no map added", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mapNameAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        mapNameAlertDialog.show();
    }

    public Uri getImageUri(Context context, Bitmap inImage) {
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void selectNewMapFile(View view) {
        //TODO
        //Select map image resource
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        // TODO add to recycler view + persistent list of maps (file backed?)
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                newMapBitmap = MediaStore.Images.Media.getBitmap(
                        getApplicationContext().getContentResolver(), uri);
                mapBitmapSelected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void routerPlacementSelected(View view) {
        Intent transitionToRouterPlacement = new Intent(getBaseContext(),
                RouterPlacementActivity.class);
        //transitionToRouterPlacement.putExtra("mapID", mapID);
        startActivity(transitionToRouterPlacement);
    }


    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "Item clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MapManager.getInstance(getApplicationContext()).saveMaps(mapListAdapter.getList());
    }
}
