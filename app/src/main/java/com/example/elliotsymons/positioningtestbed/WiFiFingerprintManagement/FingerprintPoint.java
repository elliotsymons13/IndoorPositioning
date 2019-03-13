package com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement;

import java.util.*;

public class FingerprintPoint extends Point {
    private int ID, X, Y;
    private Set<Capture> captures;

    public FingerprintPoint(int ID, int X, int Y, Set<Capture> captures) {
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
                "X, Y = (" + X + ", " + Y + "), " +
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
        if ((this.X == (fp.getX())) &&
            (this.Y == (fp.getY())) ) {
            return true;
        }
        return false;
    }
}