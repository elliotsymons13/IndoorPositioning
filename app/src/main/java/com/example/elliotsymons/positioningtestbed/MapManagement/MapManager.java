package com.example.elliotsymons.positioningtestbed.MapManagement;

import android.content.Context;
import android.util.Log;

import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.FingerprintPoint;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.JSONFingerprintManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MapManager {
    private static final String TAG = "MapManager";
    private static MapManager instance; //singleton
    Context applicationContext;


    public static final String mapsFilename = "maps.dat";
    private List<MapData> maps;


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


    public void saveMaps(List<MapData> maps) {
        //TODO save maps to external file using fos
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
        //TODO load and return maps from external file using fis
        //File fout = new File(applicationContext.getFilesDir(), mapsFilename);
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
}
