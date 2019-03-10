package com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.util.*;

public class JSONFingerprintManager implements FingerprintManager {
    private static final String TAG = "JSONFingerprintManager";
    private static JSONFingerprintManager instance; //singleton
    private Context applicationContext;

    private final String fingerprintDirectoryPath = "/WiFiFingerprintData";
    private final String filename = "data.json";
    private boolean loaded = false;

    //JSON file storage
    private JSONObject jsonRoot;
    private JSONObject fingerprintData;
    private JSONArray fingerprints;

    //Local 'live' storage
    private Set<FingerprintPoint> points;
    private int maxID;



    /*
     * Singleton support -->
     * */
    private JSONFingerprintManager(Context context) {
        this.applicationContext = context;
        points = new HashSet<FingerprintPoint>();
    }
    public static JSONFingerprintManager getInstance(Context context) {
        if (instance == null)
            instance = new JSONFingerprintManager(context);
        return instance;
    }
    /*
     * <--
     * */

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

    @Override
    public void loadIfNotAlready() {
        if (!loaded) {
            load();
            loaded = true;
        }
    }

    private void load() {
        String jsonString = "";

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
            fingerprintData = jsonRoot.getJSONObject("fingerprint-data");
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
            Log.e(TAG, "Error loading fingeprints from JSON");
            j.printStackTrace();
        }
        maxID = ID; //record highest current ID
    }

    public void addFingerprint(int X, int Y, Set<Capture> captures) {
        FingerprintPoint point = new FingerprintPoint(++maxID, X, Y, captures);

        for (FingerprintPoint pointTemp : points) {
            if (pointTemp.equals(point)) {
                Log.i(TAG, "Did not add point at existing location. ");
                return;
            }
        }
        Log.i(TAG, "Point did not yet exist, added. ");
        points.add(point);

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

    public FingerprintPoint getFingerprintByXY(int x, int y) {
        for (FingerprintPoint point : points) {
            if (point.getX() == x && point.getY() == y) {
                return point;
            }
        }
        return null; //if point not found
    }

    public boolean fingerprintXYexists(int X, int Y) {
        return (getFingerprintByXY(X, Y) != null);
    }

    @Override
    public Set<FingerprintPoint> getAllFingerprints() {
        return points;
    }

    public void save() {
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
    }

    public String fileToString() {
        return jsonRoot.toString();
    }
}