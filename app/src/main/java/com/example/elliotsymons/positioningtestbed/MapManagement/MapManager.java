package com.example.elliotsymons.positioningtestbed.MapManagement;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
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
    private boolean shouldRemoveSelected = false;


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

    public void setShouldRemoveSelected() {
        shouldRemoveSelected = true;
    }

    public boolean shouldSelectedBeRemoved() {
        return shouldRemoveSelected;
    }
}
