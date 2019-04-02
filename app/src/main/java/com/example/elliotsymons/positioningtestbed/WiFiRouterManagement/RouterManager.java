package com.example.elliotsymons.positioningtestbed.WiFiRouterManagement;

import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.Capture;

import java.util.Set;

public interface RouterManager {
    void save();
    void loadIfNotAlready();
    void deleteAllRouters();

    //Boolean to indicate success
    boolean addRouter(int X, int Y, String mac, double power);

    Set<RouterPoint> getAllRouters();

    void destroyInstance();

}
