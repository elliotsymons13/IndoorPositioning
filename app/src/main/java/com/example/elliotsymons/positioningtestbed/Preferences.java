package com.example.elliotsymons.positioningtestbed;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class Preferences {
    private static final String TAG = "Preferences";
    private static volatile Preferences instance = null;
    private Context applicationContext;

    //Variables
    private int mapID = 0;
    private String dcsFPfilename = "dcsFingerprints.json";
    private String msbFPfilename = "msbFingerprints.json";
    private String homeFPfilename = "homeFingerprints.json";
    private String dcsRoutersFilename = "dcsRouters.json";
    private String msbRoutersFilename = "msbRouters.json";
    private String homeRoutersFilename = "homeRoutersFilename.json";

    public int getMapID() {
        return mapID;
    }
    public void setMapID(int id) {
        mapID = id;
    }

    /*
     * Singleton support -->
     * */
    private Preferences(Context context) {
        this.applicationContext = context;
    }

    public static Preferences getInstance(Context context) {
        if (instance == null) {
            synchronized(Preferences.class) {
                if (instance == null) {
                    instance = new Preferences(context);
                    instance.readPrefs(context);
                }
            }
        }
        return instance;
    }
    /*
     * <--
     * */

    //Taken from: https://stackoverflow.com/questions/13670862/setting-a-value-in-one-class-and-retrieving-from-another-class-in-java-android/13673178#13673178
    //Called automatically on first instantiation per session
    private void readPrefs(Context ctx) {
        try {
            SharedPreferences sp =
                    PreferenceManager.getDefaultSharedPreferences(ctx);
            //TODO
            mapID = sp.getInt("mapID", mapID);
        } catch (Exception e) {
            Log.e(TAG, "exception reading preferences: " + e, e);
            e.printStackTrace();
            Toast.makeText(ctx, "ERROR: Could not load preferences", Toast.LENGTH_SHORT).show();
        }
    }

    // Should be called manually from onPause
    public void savePrefs(Context ctx) {
        try {
            SharedPreferences.Editor sp =
                    PreferenceManager.getDefaultSharedPreferences(ctx).edit();
            //TODO
            sp.putInt("mapID", mapID);
            sp.commit();
        } catch (Exception e) {
            Log.e(TAG, "exception reading preferences: " + e, e);
            e.printStackTrace();
            Toast.makeText(ctx, "ERROR: Could not save preferences", Toast.LENGTH_SHORT).show();
        }
    }
}
