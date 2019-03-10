package com.example.elliotsymons.positioningtestbed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.Capture;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.FingerprintManager;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.FingerprintPoint;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.JSONFingerprintManager;
import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.Point;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WiFiLocatingActivity extends AppCompatActivity implements MapViewFragment.LocationPassListener, LocationButtonFragment.LocationControllerFragmentInteractionListener {
    private static final String TAG = "WiFiLocatingActivity";
    MapViewFragment map;
    LocationButtonFragment controls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_locating);
        getSupportActionBar().setTitle("WiFi location");

        map = (MapViewFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_mapViewLocate);
        controls = (LocationButtonFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_locationControls);
        map.setBlueDotLocked(); //the user is not able to place the dot in this activity, it should be located for them

    }

    @Override
    public void passLocation(int x, int y) {
        //TODO

    }

    @Override
    public void updateLocation(View view) {
        new WiFiFingerprintLocatorTask().execute();
    }

    private class WiFiFingerprintLocatorTask extends AsyncTask<Void, Integer, Point> {
        private static final String TAG = "WiFiFingerprintLocatorT";
        FingerprintManager fm;
        WifiManager wifiManager;
        boolean resultReceived = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.btn_locate).setEnabled(false);
            //TODO progress bar in UI
        }

        @Override
        protected void onPostExecute(Point location) {
            super.onPostExecute(location);
            //TODO handle finishing, run on UI thread
            int x = location.getX();
            int y = location.getY();

            //TODO update map
            Toast.makeText(WiFiLocatingActivity.this, "Updating map", Toast.LENGTH_SHORT).show();
            map.setCurrentX(x);
            map.setCurrentY(y);
            findViewById(R.id.btn_locate).setEnabled(true);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //TODO update on the UI thread progress bar
            switch (values[0]) {
                case 1:
                    Toast.makeText(WiFiLocatingActivity.this, "Scanning...",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(WiFiLocatingActivity.this, "Scanned",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(WiFiLocatingActivity.this, "Cross-comparison complete",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(WiFiLocatingActivity.this,
                            "Could not correlate", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        protected Point doInBackground(Void... voids) {
            Point location = new Point();
            fm = JSONFingerprintManager.getInstance(getApplicationContext());
            fm.loadIfNotAlready();

            //Get capture of current location
            publishProgress(1);
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
            publishProgress(2);
            unregisterReceiver(wifiScanReceiver);

            Log.d(TAG, "doInBackground: Scan completed");
            //extract needed values
            Set<Capture> queryPointCaptures = new HashSet<>();
            for (ScanResult result : scanResults) {
                queryPointCaptures.add(new Capture(result.BSSID, Math.abs(result.level)));
            }


            //Get fingerprint points
            Set<FingerprintPoint> fingerprintPoints = fm.getAllFingerprints();


            //Positioning algorithm
            Set<PossiblePoint> possiblePoints = new HashSet<>();
            //TODO progress updates (based on number of points, percentage of points checked)
            int correlationCount = 0;
            for (FingerprintPoint p : fingerprintPoints) {
                int distanceSquared = 0;
                boolean correlated = false;
                Set<Capture> fingerprintCaptures = p.getCaptures();
                for (Capture fingerprintCapture : fingerprintCaptures) {
                    for (Capture queryCapture: queryPointCaptures) {
                        if (fingerprintCapture.getMAC().equals(queryCapture.getMAC())) {
                            distanceSquared += Math.pow(
                                    (queryCapture.getRSSI() -
                                            fingerprintCapture.getRSSI()), 2);
                            if (!correlated) {
                                correlationCount++;
                                correlated = true;
                            }
                        }
                    }
                }
                possiblePoints.add(new PossiblePoint(Math.sqrt(distanceSquared), p));
            }
            publishProgress(3);

            //calculate nearest point
            PossiblePoint currentMinimum = null;
            for (PossiblePoint pp : possiblePoints) {
                if (currentMinimum == null || pp.getDistance() < currentMinimum.getDistance()) {
                    currentMinimum = pp;
                }
            }

            if (correlationCount < 1)  {
                publishProgress(4);
                return null;
            }

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
