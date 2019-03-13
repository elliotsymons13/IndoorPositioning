package com.example.elliotsymons.positioningtestbed.WiFiRouterManagement;

import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.Point;

public class RouterPoint extends Point {
    private int ID;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public int getX() {
        return X;
    }

    @Override
    public void setX(int x) {
        X = x;
    }

    @Override
    public int getY() {
        return Y;
    }

    @Override
    public void setY(int y) {
        Y = y;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    private int X;
    private int Y;
    private String mac;

    public RouterPoint(int ID, int X, int Y, String mac) {
        this.ID = ID;
        this.X = X;
        this.Y = Y;
        this.mac = mac;
    }


}