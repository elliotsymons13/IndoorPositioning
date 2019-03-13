package com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement;

import java.util.*;


public interface FingerprintManager {
    void save();
    void loadIfNotAlready();
    void deleteAllFingerprints();

    void addFingerprint(int X, int Y, Set<Capture> captures);
    FingerprintPoint getFingerprintByXY(int x, int y);
    boolean fingerprintXYexists(int X, int Y);

    Set<FingerprintPoint> getAllFingerprints();

    String fileToString();
}