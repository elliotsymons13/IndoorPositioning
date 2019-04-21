package com.example.elliotsymons.positioningtestbed.WiFiRouterManagement;

import android.graphics.Point;

public class RouterPoint extends Point {
    private int ID;
    private String mac;
    private double txPower;

    public double getTxPower() {
        return txPower;
    }

    int getID() {
        return ID;
    }

    public String getMac() {
        return mac;
    }

    RouterPoint(int ID, int X, int Y, String mac, double txPower) {
        this.ID = ID;
        this.x = X;
        this.y = Y;
        this.mac = mac;
        this.txPower = txPower;
    }


}
