package com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement;

/**
 * A single 'record' parsed from a scan result - consists of:
 * a mac address of the router
 * and the associated RSSI measured.
 */
public class Capture {
    private String MAC;
    private int RSSI;

    /**
     * @param mac MAC address.
     * @param rssi Received Signal Strength indication.
     */
    public Capture (String mac, int rssi) {
        this.MAC = mac;
        this.RSSI = rssi;
    }

    public String getMAC() {
        return MAC;
    }

    public int getRSSI() {
        return RSSI;
    }

    /**
     * @return String representation of this capture.
     */
    @Override
    public String toString() {
        return "Capture{" + 
                "MAC = " + MAC + ", " + 
                "RSSI = " + RSSI + "}\n";
    }



}