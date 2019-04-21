package com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement;

import android.content.Context;
import android.preference.Preference;
import android.util.Log;
import android.widget.Toast;

import com.example.elliotsymons.positioningtestbed.MapManagement.MapManager;
import com.example.elliotsymons.positioningtestbed.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.util.*;

/**
 * Singleton class providing storage for fingerprint data, with JSON file backing.
 */
public class JSONFingerprintManager implements FingerprintManager {
    private static final String TAG = "JSONFingerprintManager";
    private static JSONFingerprintManager instance; //singleton
    private Context applicationContext;

    private final String fingerprintDirectoryPath = "/fingerprints";
    private String filename;
    private boolean loaded = false;

    //JSON file storage
    private JSONObject jsonRoot;
    private JSONArray fingerprints;

    //Local 'live' storage
    private Set<FingerprintPoint> points;
    private int maxID;

    /**
     * Remove all fingerprints from the file system, and from the 'live' data in the application session.
     */
    @Override
    public void deleteAllFingerprints() {
        //Delete file
        File folder = new File(applicationContext.getFilesDir() + fingerprintDirectoryPath);
        File fin = new File(folder.getAbsolutePath(), filename);
        fin.delete();

        //Delete local live data
        points = new HashSet<>();
        load(); //overwrites local JSON data also
        Toast.makeText(applicationContext, "Fingerprints deleted", Toast.LENGTH_SHORT).show();
    }

    public void deleteFile(String filename) {
        File toDelete = new File(applicationContext.getFilesDir() + fingerprintDirectoryPath, filename+".json");
        toDelete.delete();
    }

    /*
     * Singleton support -->
     * */
    private JSONFingerprintManager(Context context) {
        this.applicationContext = context;
        MapManager mapManager = MapManager.getInstance(context);
        try {
            filename = mapManager.getMapData(mapManager.getSelected()).getName() + ".json";
        } catch (ArrayIndexOutOfBoundsException e) {
            filename = "default.json";
        }

        Log.d(TAG, "JSONFingerprintManager: Filename = " + filename);
        points = new HashSet<>();
    }
    public static JSONFingerprintManager getInstance(Context context) {
        if (instance == null)
            instance = new JSONFingerprintManager(context);
        return instance;
    }
    /*
     * <--
     * */

    /**
     * Delete the current instance of the singleton (forces a constructor call on the next use).
     */
    @Override
    public void destroyInstance() {
        save();
        instance = null;
    }

    // Setup the fingerprint file
    private void initialise() {
        try {
            String json = "{\"fingerprint-data\":{\"points\":[]}}";
            File folder = new File(applicationContext.getFilesDir() + fingerprintDirectoryPath);
            File fout = new File(folder.getAbsolutePath(), filename);

            FileOutputStream fouts = new FileOutputStream(fout);
            fouts.write(json.getBytes());
            fouts.close();
        } catch (FileNotFoundException fnf) {
            fnf.printStackTrace();
        } catch(IOException io) {
            io.printStackTrace();
        }
    }

    /**
     * Load fingerprints from file, if they have nto already been loaded in this session.
     */
    @Override
    public void loadIfNotAlready() {
        if (!loaded) {
            load();
        }
    }

    // Load fingerprints from file
    // Method handles cases such as no file, incorrect formatting, etc. internally.
    private void load() {
        String jsonString = "";
        loaded = true;

        //Import file
        try {
            File folder = new File(applicationContext.getFilesDir() + fingerprintDirectoryPath);
            File fin = new File(folder.getAbsolutePath(), filename);
            if (!folder.exists()) {
                folder.mkdir();
            }
            if (!fin.exists()) {
                fin.createNewFile();
            }
            Scanner fins = new Scanner(fin);
            if (fins.hasNext()) {
                jsonString = fins.useDelimiter("\\A").next(); // read whole file into string
            } else {
                initialise();
                fin = new File(folder.getAbsolutePath(), filename); //refresh file
                fins = new Scanner(fin);
                jsonString = fins.useDelimiter("\\A").next(); // read whole file into string
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable to write to read from wifi fingerprints file");
            e.printStackTrace();
        }

        int ID = 0;
        try {
            jsonRoot = new JSONObject(jsonString);
            JSONObject fingerprintData = jsonRoot.getJSONObject("fingerprint-data");
            fingerprints = fingerprintData.getJSONArray("points");


            for (int i = 0; i < fingerprints.length(); i++) {
                JSONObject point = fingerprints.getJSONObject(i);
                ID = point.getInt("ID");
                int X = point.getInt("X");
                int Y = point.getInt("Y");

                Set<Capture> captures = new HashSet<Capture>();
                JSONArray capturesArray = point.getJSONArray("captures");
                for (int j = 0; j < capturesArray.length(); j++) {
                    JSONObject capture = capturesArray.getJSONObject(j);
                    String mac = capture.getString("MAC");
                    int rssi = capture.getInt("RSSI");
                    captures.add(new Capture(mac, rssi));
                }
                points.add(new FingerprintPoint(ID, X, Y, captures));
            }
        } catch (JSONException j) {
            Log.e(TAG, "Error loading fingerprints from JSON");
            j.printStackTrace();
        }
        maxID = ID; //record highest current ID
    }

    /**
     * Add a new fingerprint to the dataset.
     * @param X The X coordinate of the captured point.
     * @param Y The Y coordinate of the captured point.
     * @param captures The set of Captures made at this location.
     */
    public void addFingerprint(int X, int Y, Set<Capture> captures) {
        FingerprintPoint point = new FingerprintPoint(++maxID, X, Y, captures);

        // Check to see if a capture already exists at that location.
        for (FingerprintPoint pointTemp : points) {
            if (pointTemp.equals(point)) {
                Log.i(TAG, "Did not add point at existing location. ");
                return;
            }
        }
        Log.i(TAG, "Point did not yet exist, added. ");
        points.add(point);

        // Add the point to the file
        try {
            JSONArray newCaptures = new JSONArray();
            for (Capture cap : captures) {
                JSONObject newCap = new JSONObject();
                newCap.put("MAC", cap.getMAC());
                newCap.put("RSSI", cap.getRSSI());
                newCaptures.put(newCap);
            }

            JSONObject newFingerprint = new JSONObject();
            newFingerprint.put("ID", maxID);
            newFingerprint.put("X", X);
            newFingerprint.put("Y", Y);
            newFingerprint.put("captures", newCaptures);

            fingerprints.put(newFingerprint);
        } catch (JSONException j) {
            Log.e(TAG, "Error adding fingerprint to JSON database. ");
            j.printStackTrace();
        }
    }

    /**
     * @return The set of all fingerprints.
     */
    @Override
    public Set<FingerprintPoint> getAllFingerprints() {
        return points;
    }

    /**
     * Save the fingerprints to file.
     */
    @Override
    public void save() {
        if (loaded) {
            try {
                File folder = new File(applicationContext.getFilesDir() + fingerprintDirectoryPath);
                File fout = new File(folder.getAbsolutePath(), filename);

                FileOutputStream fouts = new FileOutputStream(fout);
                fouts.write(jsonRoot.toString(4).getBytes()); //4 specifies the size of indent
                fouts.close();
            } catch (IOException e) {
                Log.w(TAG, "Unable to write to file when saving wifi fingerprints");
                e.printStackTrace();
            } catch (JSONException j) {
                Log.w(TAG, "Unable to convert JSON to string when saving wifi fingerprints");
                j.printStackTrace();
            }
        } else {
        Log.d(TAG, "save: Not saving, as not yet loaded from file");
        }
    }
}