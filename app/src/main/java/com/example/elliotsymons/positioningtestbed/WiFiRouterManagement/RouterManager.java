package com.example.elliotsymons.positioningtestbed.WiFiRouterManagement;

import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.Capture;

import java.util.Set;

public interface RouterManager {
    void save();
    void loadIfNotAlready();
    void deleteAllRouters();

    void addRouter(int X, int Y, String mac);

    Set<RouterPoint> getAllRouters();

}
