package com.example.elliotsymons.positioningtestbed;

import android.graphics.Paint;

public class NavDot {
    private int x;
    private int y;
    private int r;



    private int ID;
    private boolean locked = false;
    private boolean visible = true;

    private Paint paint;

    public NavDot(int ID, int x, int y, Paint paint) {
        this.ID = ID;
        this.paint = paint;
        this.x = x;
        this.y = y;
        r = 15;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
