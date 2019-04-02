package com.example.elliotsymons.positioningtestbed;

import com.example.elliotsymons.positioningtestbed.WiFiRouterManagement.RouterPoint;

class TrilaterationPoint {
    RouterPoint routerPoint;
    int RSSI;
    double distance;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public TrilaterationPoint(RouterPoint routerPoint, int rssi) {
        this.routerPoint = routerPoint;
        this.RSSI = rssi;
    }

    public RouterPoint getRouterPoint() {
        return routerPoint;
    }

    public void setRouterPoint(RouterPoint routerPoint) {
        this.routerPoint = routerPoint;
    }

    public int getRSSI() {
        return RSSI;
    }

    public void setRSSI(int RSSI) {
        this.RSSI = RSSI;
    }
}
