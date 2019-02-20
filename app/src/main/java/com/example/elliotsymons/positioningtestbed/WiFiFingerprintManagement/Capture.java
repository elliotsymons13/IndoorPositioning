package com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement;

public class Capture {
    private String MAC;
    private int RSSI;

    public Capture (String mac, int rssi) {
        this.MAC = mac;
        this.RSSI = rssi;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAX(String MAC) {
        this.MAC = MAC;
    }

    public int getRSSI() {
        return RSSI;
    }

    public void setRSSI(int RSSI) {
        this.RSSI = RSSI;
    }

    @Override
    public String toString() {
        return "Capture{" + 
                "MAC = " + MAC + ", " + 
                "RSSI = " + RSSI + "}\n";
    }

}