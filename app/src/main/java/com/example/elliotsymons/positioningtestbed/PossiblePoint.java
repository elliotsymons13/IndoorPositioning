package com.example.elliotsymons.positioningtestbed;

import com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement.FingerprintPoint;

class PossiblePoint {
    double distance;
    FingerprintPoint fingerprintPoint;


    public PossiblePoint(double distance, FingerprintPoint p) {
        this.distance = distance;
        this.fingerprintPoint = p;
    }

    public double getDistance() {
        return distance;
    }

    public FingerprintPoint getFingerprintPoint() {
        return fingerprintPoint;
    }

}
