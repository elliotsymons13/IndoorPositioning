package com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement;

import java.util.HashSet;
import java.util.Set;

public class FingerprintDataSingleton {
    private static FingerprintDataSingleton instance;

    private Set<FingerprintPoint> points;

    /*
    * Singleton support -->
    * */
    private FingerprintDataSingleton() {//Prevents instantiation otherwise
        points = new HashSet<>();
    }
    public static FingerprintDataSingleton getInstance() {
        if (instance == null)
            instance = new FingerprintDataSingleton();
        return instance;
    }
    /*
    * <--
    * */


    public Set<FingerprintPoint> getFingerprintPoints() {
        return points;
    }




}
