package com.example.elliotsymons.positioningtestbed.MapManagement;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;

public class Map  {
    Bitmap mapBitmap;
    MapData mapData;

    public Map(MapData mapData, Bitmap mapBitmap) {
        this.mapBitmap = mapBitmap;
        this.mapData = mapData;
    }

    public Bitmap getMapBitmap() {
        return mapBitmap;
    }

    public void setMapBitmap(Bitmap mapBitmap) {
        this.mapBitmap = mapBitmap;
    }
    public MapData getMapData() {
        return mapData;
    }

    public void setMapData(MapData mapData) {
        this.mapData = mapData;
    }

}
