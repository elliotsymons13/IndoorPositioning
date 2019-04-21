package com.example.elliotsymons.positioningtestbed.WiFiRouterManagement;

import java.util.Set;

/**
 * Interface specifying the requirements for a class managing the storage of routers.
 */
public interface RouterManager {
    void save();

    void loadIfNotAlready();

    void deleteAllRouters();

    //Boolean to indicate success
    boolean addRouter(int X, int Y, String mac, double power);

    Set<RouterPoint> getAllRouters();

    void destroyInstance();

}
