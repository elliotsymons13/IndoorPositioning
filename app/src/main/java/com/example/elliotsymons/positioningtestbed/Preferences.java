package com.example.elliotsymons.positioningtestbed;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Singleton class storing any user preferences, and managing their persistence.
 */
public class Preferences {
    private static final String TAG = "Preferences";
    private static volatile Preferences instance = null;
    private Context applicationContext;

    //Variables
    private String mapURI;
    private int activeLocationMethods;

    //Setters and getters
    String getMapURI() {
        return mapURI;
    }
    void setMapURI(String uri) {
        mapURI = uri;
    }

    /*
     * Singleton support -->
     * */
    private Preferences(Context context) {
        this.applicationContext = context;
        activeLocationMethods = 0;
        mapURI = null;
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

    int getActiveLocationMethods() {
        return activeLocationMethods;
    }

    void incrementActiveLocationMethods() {
        activeLocationMethods++;
    }

    void decrementActiveLocationMethods() {
        activeLocationMethods--;
    }


    //Taken from: https://stackoverflow.com/questions/13670862/setting-a-value-in-one-class-and-retrieving-from-another-class-in-java-android/13673178#13673178
    //Called automatically on first instantiation per session
    private void readPrefs(Context ctx) {
        try {
            SharedPreferences sp =
                    PreferenceManager.getDefaultSharedPreferences(ctx);
            mapURI = sp.getString("mapURI", mapURI);
        } catch (Exception e) {
            Log.e(TAG, "exception reading preferences: " + e, e);
            e.printStackTrace();
            Toast.makeText(ctx, "ERROR: Could not load preferences", Toast.LENGTH_SHORT).show();
        }
    }

    // Should be called manually from onPause (only required in activities where preferences are changed)
    void savePrefs(Context ctx) {
        try {
            SharedPreferences.Editor sp =
                    PreferenceManager.getDefaultSharedPreferences(ctx).edit();
            sp.putString("mapURI", mapURI);
            sp.commit();
        } catch (Exception e) {
            Log.e(TAG, "exception writing preferences: " + e, e);
            e.printStackTrace();
            Toast.makeText(ctx, "ERROR: Could not save preferences", Toast.LENGTH_SHORT).show();
        }
    }
}
