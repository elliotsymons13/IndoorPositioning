package com.example.elliotsymons.positioningtestbed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.elliotsymons.positioningtestbed.MapManagement.MapManager;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.Capture;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.FingerprintManager;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.FingerprintPoint;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.JSONFingerprintManager;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.Point;
import com.example.elliotsymons.positioningtestbed.WiFiRouterManagement.JSONRouterManager;
import com.example.elliotsymons.positioningtestbed.WiFiRouterManagement.RouterManager;
import com.example.elliotsymons.positioningtestbed.WiFiRouterManagement.RouterPoint;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.elliotsymons.positioningtestbed.MapViewFragment.FINGERPRINT_DOT;
import static com.example.elliotsymons.positioningtestbed.MapViewFragment.TRILAT_DOT;
import static com.example.elliotsymons.positioningtestbed.MapViewFragment.startX;
import static com.example.elliotsymons.positioningtestbed.MapViewFragment.startY;

public class WiFiLocatingActivity extends AppCompatActivity implements
        MapViewFragment.LocationPassListener, LocationControlsFragment.LocationControllerFragmentInteractionListener {
    private static final String TAG = "WiFiLocatingActivity";
    Preferences prefs;
    MapManager mapManager;

    MapViewFragment map;
    LocationControlsFragment controls;
    String mapURI;
    ProgressBar progressBarFingerprinting, progressBarTrilaterating;

    private double TxPwr = 100; //Default is 70mW for 'normal' routers, up to 400mW for others - <100 for uni? //TODO set/calibrate
    private double pathLossExponent = 6;

    public void setTxPwr(double txPwr) {
        TxPwr = txPwr;
        Log.d(TAG, "setTxPwr: set to " + txPwr);
    }

    public void setPathLossExponent(double pathLossExponent) {
        this.pathLossExponent = pathLossExponent;
        Log.d(TAG, "setPathLossExponent: set to " + pathLossExponent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_locating);
        getSupportActionBar().setTitle("WiFi mapBitmap");

        map = (MapViewFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_mapViewLocate);
        prefs = Preferences.getInstance(getApplicationContext());
        mapManager = MapManager.getInstance(getApplicationContext());
        controls = (LocationControlsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_locationControls);
        progressBarFingerprinting = findViewById(R.id.progressBar_locateProgressFingerprinting);
        progressBarFingerprinting.setVisibility(View.INVISIBLE);
        progressBarTrilaterating = findViewById(R.id.progressBar_locateProgressTrilateration);
        progressBarTrilaterating.setVisibility(View.INVISIBLE);


        map.addNavDot(TRILAT_DOT, startX, startY, R.color.colorTilatDot);
        map.lockNavDot(TRILAT_DOT); //the user is not able to place the dot in this activity, it should be located for them
        map.hideNavDot(TRILAT_DOT);
        map.addNavDot(FINGERPRINT_DOT, startX, startY, R.color.colorRSSIDot);
        map.lockNavDot(FINGERPRINT_DOT); //the user is not able to place the dot in this activity, it should be located for them
        map.hideNavDot(FINGERPRINT_DOT);
    }

    /*@Override
    public void onResume() {
        super.onResume();
        // load specified map with URI
        mapURI = prefs.getMapURI();
        //Bitmap newBackground = mapManager.decodeImageFromURIString(mapURI);
        //map.setMapBackground(newBackground);
        //TODO not needed as works without?
    }*/

    @Override
    public void passLocation(int x, int y) {
        //TODO

    }

    @Override
    public void updateLocation(View view) {
        new WiFiFingerprintLocatorTask().execute();
        new WiFiTrilaterationLocatorTask().execute();
    }


    private class WiFiTrilaterationLocatorTask extends AsyncTask<Void, Integer, Point> {
        private static final String TAG = "WiFiTrilaterationLocato";
        RouterManager rm;
        WifiManager wifiManager;
        boolean resultReceived = false;

        String dummyResult = "";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarTrilaterating.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Point location) {
            super.onPostExecute(location);
            progressBarTrilaterating.setVisibility(View.INVISIBLE);
            if (location == null) {
                Toast.makeText(WiFiLocatingActivity.this,
                        "Insufficient points in range for trilateration", Toast.LENGTH_LONG).show();
                findViewById(R.id.btn_locate).setEnabled(true);
                return;
            }
            int x = location.getX();
            int y = location.getY();
            Log.d(TAG, "onPostExecute: Updating map: x,y = " + x + ", " + y);
            // update map
            map.setCurrentX(MapViewFragment.TRILAT_DOT, x);
            map.setCurrentY(MapViewFragment.TRILAT_DOT, y);
            map.showNavDot(MapViewFragment.TRILAT_DOT);

            findViewById(R.id.btn_locate).setEnabled(true);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBarTrilaterating.setProgress(values[0]);
        }

        @Override
        protected Point doInBackground(Void... voids) {
            Point location = new Point();
            rm = JSONRouterManager.getInstance(getApplicationContext());
            String routersFilename = prefs.getRoutersFilename();
            rm.loadFile(routersFilename);

            //Get captures at current mapBitmap
            publishProgress(5);
            wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            enableWifi();
            registerReceiver(wifiScanReceiver,
                    new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            Log.d(TAG, "doInBackground: Requesting scan");
            wifiManager.startScan();
            while (!resultReceived) {
                SystemClock.sleep(100);
            }

            //CREDIT: https://stackoverflow.com/questions/17285337/how-can-i-sort-the-a-list-of-getscanresults-based-on-signal-strength-in-ascend
            // -->>
            Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult lhs, ScanResult rhs) {
                    //return (lhs.level <rhs.level ? -1 : (lhs.level==rhs.level ? 0 : 1));
                    return WifiManager.compareSignalLevel(lhs.level, rhs.level);
                }
            };
            List<ScanResult> scanResults = wifiManager.getScanResults();
            Collections.sort(scanResults, comparator);
            // <<--

            publishProgress(10);
            unregisterReceiver(wifiScanReceiver);
            Log.d(TAG, "doInBackground: Got scan results");

            Set<RouterPoint> storedRouters = rm.getAllRouters();


            //Find all routers that are present in our scan and the set of stored routers
            List<TrilaterationPoint> trilaterationPoints = new ArrayList<>();
            for (ScanResult result : scanResults) {
                String mac = result.BSSID;
                int rssi = result.level;

                for (RouterPoint routerPoint : storedRouters) {
                    if (mac.equalsIgnoreCase(routerPoint.getMac())) { //i.e. if the same router
                        trilaterationPoints.add(new TrilaterationPoint(routerPoint, rssi));
                    }
                }
            }
            publishProgress(30);

            //Find the N of these that are closest to our position (based on RSSI)
            int N = 3;
            int length = trilaterationPoints.size();
            if (length < N) {
                N = length;
            }
            Log.d(TAG, "doInBackground: N = " + N);
            if (N < 3) {
                Log.d(TAG, "doInBackground: Trilateration impossible, insufficient points");
                return null;
            }
            List<TrilaterationPoint> NtrilaterationPoints = trilaterationPoints.subList(0, N);



            //Calculate the distances to these N routers, using the path-loss model
            //(parameters are set globally, and configurable via the seek bars)
            Log.d(TAG, "doInBackground: Tx = " + TxPwr + ", PathLossExponent = " + pathLossExponent);
            for (TrilaterationPoint point : NtrilaterationPoints) {
                point.setDistance(
                        Math.pow(10,  ((TxPwr - point.getRSSI()) / (10 * pathLossExponent))  )
                );
                Log.d(TAG, "doInBackground: Distance to router at " + 
                        point.getRouterPoint().getX() + ", " + 
                        point.getRouterPoint().getY() + " is " +
                        point.getDistance() + " @ RSSI " + point.getRSSI());
            }
            Log.d(TAG, "doInBackground: All data ready for trilateration");
            publishProgress(40);

            //Perform the trilateration algorithm, given the distances calculated above, and the known locations of the routers.


            //DUMMY FIXME
            //CREDIT: https://github.com/lemmingapex/Trilateration
            // -->> (adapted)
            double[][] positions = new double[N][2];
            double[] distances = new double[N];
            int i = 0;
            for (TrilaterationPoint p : NtrilaterationPoints) {
                positions[i][0] = p.getRouterPoint().getX();
                positions[i][1] = p.getRouterPoint().getY();
                distances[i] = p.getDistance();

                i++;
            }
//            double[][] positions = new double[][] { { 5.0, -6.0 }, { 13.0, -15.0 }, { 21.0, -3.0 }, { 12.4, -21.2 } };
//            double[] distances = new double[] { 8.06, 13.97, 23.32, 15.31 };

            NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
            LeastSquaresOptimizer.Optimum optimum = solver.solve();

            // the answer
            double[] centroid = optimum.getPoint().toArray();


            double position_x = centroid[0];
            double position_y = centroid[1];


            // <<--
            dummyResult = "" + position_x + ", " + position_y;

            Log.i(TAG, "doInBackground: Dummy result: " + dummyResult);



            publishProgress(100);
            Log.d(TAG, "doInBackground: Finished");
            return new Point((int) position_x, (int) position_y);
        }

        private void enableWifi() {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
        }

        private final BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    boolean success = intent.getBooleanExtra(
                            WifiManager.EXTRA_RESULTS_UPDATED, false);
                    if (success) {
                        onScanSuccess();
                    } else {
                        onScanFailure();
                    }
                }
            }
        };

        private void onScanSuccess() {
            Log.i(TAG, "onScanSuccess: Scan result received");
            List<ScanResult> scanResults = wifiManager.getScanResults();
            //postToastMessage("Scan completed (" + scanResults.size() + " captures)");
            resultReceived = true;

            //Process results:
            String text = "";
            for (ScanResult result : scanResults) {
                text += "SSID: " + result.SSID + "\n";
                text += "MAC : " + result.BSSID + "\n";
                text += "RSSI: " + result.level + "\n";
            }
        }

        private void onScanFailure() {
            //postToastMessage("SCAN FAILED");
        }
    }


    /**
     * Task to asynchronously calculate the users mapBitmap, by snapping to the nearest fingerprint point.
     *
     * 1 - setup progress bar
     * 2 - get a wifi scan of the current environment and parse RSSI, MAC to set of captures
     * 3 - compare this set with the set at every fingerprint point,
     * recording the proximity in terms of Euclidean distance
     * 4 - Select the closest fingerprint points (min(distance)) and snap map mapBitmap to the fingerprint coordinates
     */
    private class WiFiFingerprintLocatorTask extends AsyncTask<Void, Integer, Point> {
        private static final String TAG = "WiFiFingerprintLocatorT";
        FingerprintManager fm;
        WifiManager wifiManager;
        boolean resultReceived = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.btn_locate).setEnabled(false);
            progressBarFingerprinting.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Point location) {
            super.onPostExecute(location);
            if (location == null) {
                Toast.makeText(WiFiLocatingActivity.this, "Out of range / unmapped area", Toast.LENGTH_SHORT).show();
                Toast.makeText(WiFiLocatingActivity.this, "Cannot locate", Toast.LENGTH_SHORT).show();
                progressBarFingerprinting.setVisibility(View.INVISIBLE);
                progressBarFingerprinting.setProgress(0);
                findViewById(R.id.btn_locate).setEnabled(true);
                return;
            }
            int x = location.getX();
            int y = location.getY();

            // update map
            map.setCurrentX(MapViewFragment.FINGERPRINT_DOT, x);
            map.setCurrentY(MapViewFragment.FINGERPRINT_DOT, y);
            map.showNavDot(MapViewFragment.FINGERPRINT_DOT);
            progressBarFingerprinting.setVisibility(View.INVISIBLE);
            findViewById(R.id.btn_locate).setEnabled(true);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBarFingerprinting.setProgress(values[0]);
        }

        @Override
        protected Point doInBackground(Void... voids) {
            Point location = new Point();
            fm = JSONFingerprintManager.getInstance(getApplicationContext());
            fm.loadIfNotAlready();

            //Get capture of current mapBitmap
            publishProgress(5);
            wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            enableWifi();
            registerReceiver(wifiScanReceiver,
                    new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            Log.d(TAG, "doInBackground: Requesting scan");
            wifiManager.startScan();
            while (!resultReceived) {
                SystemClock.sleep(100);
            }
            List<ScanResult> scanResults = wifiManager.getScanResults();
            publishProgress(10);
            unregisterReceiver(wifiScanReceiver);

            Log.d(TAG, "doInBackground: Scan completed");
            //extract needed values
            Set<Capture> queryPointCaptures = new HashSet<>();
            for (ScanResult result : scanResults) {
                queryPointCaptures.add(new Capture(result.BSSID, Math.abs(result.level)));
            }


            //Get fingerprint points
            Set<FingerprintPoint> fingerprintPoints = fm.getAllFingerprints();


            // ---POSITIONING ALGORITHM---

            Set<PossiblePoint> possiblePoints = new HashSet<>();
            int numberOfPoints = fingerprintPoints.size();
            int pointsInCommon = 0;
            int index = 0;
            for (FingerprintPoint p : fingerprintPoints) { //for each stored point...
                int distanceSquared = 0;
                index++;
                publishProgress((10 + index/numberOfPoints * 80)); //update progress bar in UI
                boolean atLeast1Match = false;
                int correlationsForThisPoint = 0;
                Set<Capture> fingerprintCaptures = p.getCaptures();
                for (Capture fingerprintCapture : fingerprintCaptures) { //...consider each capture point...
                    for (Capture queryCapture: queryPointCaptures) {
                        if (fingerprintCapture.getMAC().equalsIgnoreCase(queryCapture.getMAC())) { //..and if MACs match
                            /// include in distance consideration:
                            distanceSquared += Math.pow(
                                    (queryCapture.getRSSI() -
                                            fingerprintCapture.getRSSI()), 2);
                            correlationsForThisPoint++;
                            if (!atLeast1Match) {
                                pointsInCommon++;
                                atLeast1Match = true;
                            }
                        }
                    }
                }
                Log.d(TAG, "doInBackground: possible fingerprintPoint: distance = "
                        + Math.sqrt(distanceSquared) + ", correlations = " + correlationsForThisPoint
                + " at point X,Y = " + p.getX() + "," + p.getY());
                possiblePoints.add(new PossiblePoint(Math.sqrt(distanceSquared), correlationsForThisPoint, p));
            }
            publishProgress(90);

            //calculate nearest point
            PossiblePoint currentMinimum = null;
            for (PossiblePoint pp : possiblePoints) {
                if (currentMinimum == null) {
                    currentMinimum = pp;
                    continue;
                }
                if (pp.getDistance() < currentMinimum.getDistance()) {
                    if (pp.getMatchingRouters() != currentMinimum.getMatchingRouters())
                        Log.d(TAG, "doInBackground: not same router count");
                    currentMinimum = pp;
                }
            }

            if (pointsInCommon < 1)  {
                publishProgress(40);
                return null;
            }
            publishProgress(100);
            return new Point(currentMinimum.getFingerprintPoint().getX(),
                    currentMinimum.getFingerprintPoint().getY());
        }

        /**
         * Enable WiFi if not already enabled.
         */
        private void enableWifi() {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
        }

        private final BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    boolean success = intent.getBooleanExtra(
                            WifiManager.EXTRA_RESULTS_UPDATED, false);
                    if (success) {
                        onScanSuccess();
                    } else {
                        onScanFailure();
                    }
                }
            }
        };

        private void onScanSuccess() {
            Log.i(TAG, "onScanSuccess: Scan result received");
            List<ScanResult> scanResults = wifiManager.getScanResults();
            //postToastMessage("Scan completed (" + scanResults.size() + " captures)");
            resultReceived = true;

            //Process results:
            String text = "";
            for (ScanResult result : scanResults) {
                text += "SSID: " + result.SSID + "\n";
                text += "MAC : " + result.BSSID + "\n";
                text += "RSSI: " + result.level + "\n";
            }
        }

        private void onScanFailure() {
            //postToastMessage("SCAN FAILED");
        }
    }


}
