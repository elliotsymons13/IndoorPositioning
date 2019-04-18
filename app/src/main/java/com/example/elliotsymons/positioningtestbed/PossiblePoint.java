package com.example.elliotsymons.positioningtestbed;

import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.FingerprintPoint;

class PossiblePoint {
    double distance;
    int matchingRouters;
    FingerprintPoint fingerprintPoint;


    public PossiblePoint(double distance, int matchingRouters, FingerprintPoint p) {
        this.distance = distance;
        this.matchingRouters = matchingRouters;
        this.fingerprintPoint = p;
    }

    public double getDistance() {
        return distance;
    }

    public FingerprintPoint getFingerprintPoint() {
        return fingerprintPoint;
    }

    public int getMatchingRouters() {return matchingRouters;}

}