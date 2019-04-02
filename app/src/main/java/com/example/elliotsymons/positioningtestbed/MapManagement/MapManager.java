package com.example.elliotsymons.positioningtestbed.MapManagement;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.example.elliotsymons.positioningtestbed.Preferences;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.JSONFingerprintManager;
import com.example.elliotsymons.positioningtestbed.WiFiRouterManagement.JSONRouterManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import java.util.List;

public class MapManager {
    private static final String TAG = "MapManager";
    private static MapManager instance; //singleton
    private Context applicationContext;


    private static final String mapsFilename = "maps.dat";
    private List<MapData> maps;

    public MapData getMapData(int index) {
        return maps.get(index);
    }

    public void addMap(MapData newMap) {
        maps.add(newMap);
    }

    public void deleteMap(int position) {
        maps.remove(position);
    }

    private int selected;
    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        if (this.selected != selected) {
            this.selected = selected;
            //map selection changed, so new manager for new file
            Log.d(TAG, "setSelected: Destroying instance of JSONRouterManager, JSONFingerprintManager to force file refresh");
            JSONRouterManager.getInstance(applicationContext).destroyInstance();
            JSONFingerprintManager.getInstance(applicationContext).destroyInstance();
            Log.d(TAG, "setSelected: Loading new files");
            JSONRouterManager.getInstance(applicationContext).loadIfNotAlready();
            JSONFingerprintManager.getInstance(applicationContext).loadIfNotAlready();
        }
    }


    /*
     * Singleton support -->
     * */
    private MapManager(Context context) {
        this.applicationContext = context;
        maps = new ArrayList<>();
    }
    public static MapManager getInstance(Context context) {
        if (instance == null)
            instance = new MapManager(context);
        return instance;
    }
    /*
     * <--
     * */


    public void saveMaps() {
        //save maps to external file using fos
        try {
            FileOutputStream fos = applicationContext.openFileOutput(mapsFilename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(maps);
            oos.close();
        } catch (IOException io) {
            Log.e(TAG, "saveMaps: IO exception when saving maps to file, (re)placing file", io);
            File fout = new File(applicationContext.getFilesDir(), mapsFilename);
        }

    }

    public List<MapData> loadMaps() {
        // load and return maps from external file using fis
        FileInputStream fis;
        try {
            fis = applicationContext.openFileInput(mapsFilename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            maps = (ArrayList<MapData>) ois.readObject();
            ois.close();
            return maps;
        } catch (IOException io) {
            Log.e(TAG, "loadMaps: Error loading maps from file", io);
        } catch (ClassNotFoundException cnf) {
            Log.e(TAG, "loadMaps: Could not find map data in map file", cnf);
        }
        return null;
    }

    public Bitmap decodeImageFromURIString(String uri) {
        if (uri == null) return null;

        final Uri URI = Uri.parse(uri);
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(applicationContext.getContentResolver(),
                    URI);
            return bitmap;
        } catch (FileNotFoundException fnf) {
            Log.e(TAG, "decodeImageFromURIString: File not found from map URI", fnf);
        } catch (IOException io) {
            Log.e(TAG, "decodeImageFromURIString: IO exception when loading map from URI", io);
        }
        Toast.makeText(applicationContext, "Could not find map image", Toast.LENGTH_LONG).show();
        return null;
    }
}
