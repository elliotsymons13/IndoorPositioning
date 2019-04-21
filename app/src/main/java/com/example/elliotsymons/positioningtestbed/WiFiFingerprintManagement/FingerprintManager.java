package com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement;

import java.util.*;

/**
 * Interface specifying the requirements for a class managing the storage of fingerprint data.
 */
public interface FingerprintManager {
    void save();

    void loadIfNotAlready();

    void deleteAllFingerprints();

    void addFingerprint(int X, int Y, Set<Capture> captures);
    Set<FingerprintPoint> getAllFingerprints();

    void destroyInstance();
}