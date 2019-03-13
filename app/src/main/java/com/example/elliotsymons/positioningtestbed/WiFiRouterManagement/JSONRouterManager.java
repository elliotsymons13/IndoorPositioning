package com.example.elliotsymons.positioningtestbed.WiFiRouterManagement;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.Capture;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.FingerprintPoint;

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

public class JSONRouterManager implements RouterManager {
    private static final String TAG = "JSONRouterManager";
    private static JSONRouterManager instance; //singleton
    private Context applicationContext;

    private final String routerDirectoryPath = "/routers";
    private String filename = "defaultRoutersFile.json";
    private boolean loaded = false;

    //JSON file storage
    private JSONObject jsonRoot;
    private JSONObject routerData;
    private JSONArray routers;

    //Local 'live' storage
    private Set<RouterPoint> points;
    private int maxID;

    /*
     * Singleton support -->
     * */
    private JSONRouterManager(Context context) {
        this.applicationContext = context;
        points = new HashSet<RouterPoint>();
    }
    public static JSONRouterManager getInstance(Context context) {
        if (instance == null)
            instance = new JSONRouterManager(context);
        return instance;
    }
    /*
     * <--
     * */


    @Override
    public void loadIfNotAlready(String filename) {
        if (!loaded) {
            loadFile(filename);
            loaded = true;
        }
    }

    private void load() {
        String jsonString = "";

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
            routerData = jsonRoot.getJSONObject("router-data");
            routers = routerData.getJSONArray("points");


            for (int i = 0; i < routers.length(); i++) {
                JSONObject point = routers.getJSONObject(i);
                ID = point.getInt("ID");
                int X = point.getInt("X");
                int Y = point.getInt("Y");
                String mac = point.getString("MAC");

                points.add(new RouterPoint(ID, X, Y, mac));
            }
        } catch (JSONException j) {
            Log.e(TAG, "Error loading routers from JSON");
            j.printStackTrace();
        }
        maxID = ID; //record highest current ID
    }

    @Override
    public void loadFile(String filename) {
        this.filename = filename;
        Log.d(TAG, "loadFile: Going to load file with NAME = " + filename);
        this.load();
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
    public void addRouter(int X, int Y, String mac) {
        RouterPoint router = new RouterPoint(++maxID, X, Y, mac);

        //TODO check for existing - actually don't want this,
        // as different networks in same router have different MACs, EG in DCS, but would have same X, Y

        /*for (RouterPoint pointTemp : points) {
            if (pointTemp.equals(point)) {
                Log.i(TAG, "Did not add point at existing location. ");
                return;
            }
        }
        Log.i(TAG, "Point did not yet exist, added. ");*/
        points.add(router);

        try {
            JSONObject newRouter = new JSONObject();
            newRouter.put("ID", router.getID());
            newRouter.put("X", router.getX());
            newRouter.put("Y", router.getY());
            newRouter.put("MAC", router.getMac());
            routers.put(newRouter);
        } catch (JSONException j) {
            Log.e(TAG, "Error adding router to JSON database. ");
            j.printStackTrace();
        }

    }

    @Override
    public Set<RouterPoint> getAllRouters() {
        return points;
    }

    @Override
    public void save() {
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
    }
}
