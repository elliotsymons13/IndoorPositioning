package com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement;

import android.graphics.Point;

import java.util.*;


/**
 * A location on the map, specified for fingerprinting by the user.
 *
 * Stores the location of the point (x, y), and an identifying ID, along with the set of Captures
 * at that location.
 */
public class FingerprintPoint extends Point {
    private int ID;
    private Set<Capture> captures;

    FingerprintPoint(int ID, int X, int Y, Set<Capture> captures) {
        super(X, Y);
        this.ID = ID;
        this.captures = captures;
    }

    public Set<Capture> getCaptures() { return captures; }

    @Override
    public String toString() {
        String capStr = "\n";
        for (Capture cap : captures) {
            capStr += cap.toString();
        }
        return "FingerprintPoint{" +
                "ID = " + ID + ", " +
                "X, Y = (" + x + ", " + y + "), " +
                "captures = {\n" + capStr + "}";
    }

    /**
     * Points are considered equal if they have the same X and Y coordinates.
     * @param obj Object for comparison.
     * @return True if equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (this.getClass() != obj.getClass()) return false;

        //We know that obj is of this class
        FingerprintPoint fp = (FingerprintPoint) obj;
        if ((this.x == (fp.x)) &&
            (this.y == (fp.y)) ) {
            return true;
        }
        return false;
    }
}