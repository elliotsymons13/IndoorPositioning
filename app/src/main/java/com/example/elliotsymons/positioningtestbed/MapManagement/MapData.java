package com.example.elliotsymons.positioningtestbed.MapManagement;

import java.io.Serializable;

public class MapData implements Serializable {
    private String name;
    private String mapURI;

    public MapData(String name, String mapURI) {
        this.name = name;
        this.mapURI = mapURI;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMapURI() {
        return mapURI;
    }

    public void setMapURI(String mapURI) {
        this.mapURI = mapURI;
    }
}
