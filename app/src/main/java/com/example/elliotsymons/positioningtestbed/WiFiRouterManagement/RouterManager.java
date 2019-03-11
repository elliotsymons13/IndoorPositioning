package com.example.elliotsymons.positioningtestbed.WiFiRouterManagement;

import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.Capture;

import java.util.Set;

public interface RouterManager {
    void save();
    void loadIfNotAlready();
    void deleteAllRouters();

    void addRouter(RouterPoint router);

    Set<RouterPoint> getAllRouters();

}
