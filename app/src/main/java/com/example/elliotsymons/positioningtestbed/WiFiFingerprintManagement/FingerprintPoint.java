package com.example.elliotsymons.positioningtestbed.WiFiFingerprintManagement;

import java.util.*;

public class FingerprintPoint {
    private int ID, X, Y;
    private Set<Capture> captures;

    public FingerprintPoint(int ID, int X, int Y, Set<Capture> captures) {
        this.ID = ID;
        this.X = X;
        this.Y = Y;
        this.captures = captures;
    }

    public int getX() {
        return X;
    }
    
    public void setX(int X) {
        this.X = X;
    }

    public int getY() {
        return Y;
    }

    public void setY(int Y) {
        this.Y = Y;
    }

    public int getID() { return ID; }

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