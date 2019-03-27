package com.example.elliotsymons.positioningtestbed;

import android.graphics.Bitmap;
import android.net.Uri;

public class Map {
    private String name;
    Bitmap mapBitmap;
    private Uri mapURI;



    public Map(String name, Bitmap mapBitmap, Uri mapURI) {
        this.name = name;
        this.mapBitmap = mapBitmap;
        this.mapURI = mapURI;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getMapBitmap() {
        return mapBitmap;
    }

    public void setMapBitmap(Bitmap mapBitmap) {
        this.mapBitmap = mapBitmap;
    }

    public Uri getMapURI() {
        return mapURI;
    }

    public void setMapURI(Uri mapURI) {
        this.mapURI = mapURI;
    }
}
