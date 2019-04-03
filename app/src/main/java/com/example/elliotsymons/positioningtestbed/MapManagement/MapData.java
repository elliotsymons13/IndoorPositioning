package com.example.elliotsymons.positioningtestbed.MapManagement;

import java.io.Serializable;

/**
 * A map created by the user.
 *
 * Consists of a name (label), and a Uri pointing to an image on the device.
 */
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
}
