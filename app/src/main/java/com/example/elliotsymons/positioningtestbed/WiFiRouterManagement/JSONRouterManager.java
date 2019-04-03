package com.example.elliotsymons.positioningtestbed.WiFiRouterManagement;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.elliotsymons.positioningtestbed.MapManagement.MapManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Manages the storage of router information entered by the user, using a JSON file backing for
 * persistence.
 */
public class JSONRouterManager implements RouterManager {
    private static final String TAG = "JSONRouterManager";
    private static JSONRouterManager instance; //singleton
    private Context applicationContext;

    private final String routerDirectoryPath = "/routers";
    private String filename;
    private boolean loaded = false;

    //JSON file storage
    private JSONObject jsonRoot;
    private JSONArray routers;

    //Local 'live' storage
    private Set<RouterPoint> points;
    private int maxID;

    /*
     * Singleton support -->
     * */
    private JSONRouterManager(Context context) {
        this.applicationContext = context;
        MapManager mapManager = MapManager.getInstance(context);
        try {
            filename = mapManager.getMapData(mapManager.getSelected()).getName() + ".json";
        } catch (IndexOutOfBoundsException e) {
            filename = "default.json";
        }

        Log.d(TAG, "JSONRouterManager: Filename = " + filename);
        points = new HashSet<>();
    }
    public static JSONRouterManager getInstance(Context context) {
        if (instance == null)
            instance = new JSONRouterManager(context);
        return instance;
    }
    /*
     * <--
     * */

    public void destroyInstance() {
        save();
        instance = null;
    }


    @Override
    public void loadIfNotAlready() {
        if (!loaded) {
            load();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void load() {
        String jsonString = "";
        loaded = true;

        //Import file
        try {
            File folder = new File(applicationContext.getFilesDir() + routerDirectoryPath);
            File fin = new File(folder.getAbsolutePath(), filename);
            if (!folder.exists()) {
                folder.mkdir();
                Log.i(TAG, "load: Created folder as did not exist");
            }
            if (!fin.exists()) {
                fin.createNewFile();
                Log.i(TAG, "load: Created file as did not exist");
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
            Log.e(TAG, "Unable to read from wifi routers file");
            e.printStackTrace();
        }

        int ID = 0;
        try {
            jsonRoot = new JSONObject(jsonString);
            JSONObject routerData = jsonRoot.getJSONObject("router-data");
            routers = routerData.getJSONArray("points");


            for (int i = 0; i < routers.length(); i++) {
                JSONObject point = routers.getJSONObject(i);
                ID = point.getInt("ID");
                int X = point.getInt("X");
                int Y = point.getInt("Y");
                String mac = point.getString("MAC");
                double power = point.getDouble("TxPower");

                points.add(new RouterPoint(ID, X, Y, mac, power));
            }
        } catch (JSONException j) {
            Log.e(TAG, "Error loading routers from JSON");
            j.printStackTrace();
        }
        maxID = ID; //record highest current ID
    }

    private void initialise() {
        try {
            String json = "{\"router-data\":{\"points\":[]}}";
            File folder = new File(applicationContext.getFilesDir() + routerDirectoryPath);
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void deleteAllRouters() {
        //Delete file
        File folder = new File(applicationContext.getFilesDir() + routerDirectoryPath);
        File fin = new File(folder.getAbsolutePath(), filename);
        fin.delete();

        //Delete local live data
        points = new HashSet<>();
        load(); //overwrites local JSON data also
        Toast.makeText(applicationContext, "Routers deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean addRouter(int X, int Y, String mac, double power) {
        RouterPoint router = new RouterPoint(++maxID, X, Y, mac, power);

        // Multiple routers can share the same coordinates (as a single AP may have multiple MACs).
        // MAC addressed should be unique, however.
        for (RouterPoint point : points) {
            if (point.getMac().equalsIgnoreCase(mac)) {
                Log.i(TAG, "Did not add point with existing MAC. ");
                return false;
            }
        }
        Log.i(TAG, "Point did not yet exist, added. ");
        points.add(router);

        try {
            JSONObject newRouter = new JSONObject();
            newRouter.put("ID", router.getID());
            newRouter.put("X", router.x);
            newRouter.put("Y", router.y);
            newRouter.put("MAC", router.getMac());
            newRouter.put("TxPower", router.getTxPower());
            routers.put(newRouter);
        } catch (JSONException j) {
            Log.e(TAG, "Error adding router to JSON database. ");
            j.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Set<RouterPoint> getAllRouters() {
        return points;
    }

    @Override
    public void save() {
        if (loaded) {
            try {
                File folder = new File(applicationContext.getFilesDir() + routerDirectoryPath);
                File fout = new File(folder.getAbsolutePath(), filename);

                FileOutputStream fouts = new FileOutputStream(fout);
                fouts.write(jsonRoot.toString(4).getBytes()); //4 specifies the size of indent
                fouts.close();
            } catch (IOException e) {
                Log.w(TAG, "Unable to write to file when saving wifi routers");
                e.printStackTrace();
            } catch (JSONException j) {
                Log.w(TAG, "Unable to convert JSON to string when saving wifi routers");
                j.printStackTrace();
            }
        } else {
            Log.d(TAG, "save: Not saving, as not yet loaded from file");
        }

    }
}
