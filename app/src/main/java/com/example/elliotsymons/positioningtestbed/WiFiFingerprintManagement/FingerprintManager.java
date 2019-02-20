package com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement;

import java.util.*;


public interface FingerprintManager {
    void save();
    void load();

    void addFingerprint(int X, int Y, Set<Capture> captures);
    FingerprintPoint getFingerprintByXY(int x, int y);
    boolean fingerprintXYexists(int X, int Y);

    String fileToString();
}