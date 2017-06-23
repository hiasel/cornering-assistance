package com.k.matthias.curveassistant.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Bounding Box around a driver's location
 */

public class BoundingBox {
    private double south;
    private double west;
    private double north;
    private double east;

    public BoundingBox(double south, double west, double north, double east) {
        this.south = south;
        this.west = west;
        this.north = north;
        this.east = east;
    }

    public double getSouth() {
        return south;
    }

    public double getWest() {
        return west;
    }

    public double getNorth() {
        return north;
    }

    public double getEast() {
        return east;
    }


    public static BoundingBox fromJSON(JSONObject json) {
        BoundingBox bb = null;
        try {
            bb = new BoundingBox(
                    Double.parseDouble(json.get("south").toString()),
                    Double.parseDouble(json.get("west").toString()),
                    Double.parseDouble(json.get("north").toString()),
                    Double.parseDouble(json.get("east").toString())
            );
        } catch (JSONException e) {
            return null;
        }
        return bb;
    }

    /**
     * Checks weather or not a Point lies within the Bounding Box
     * @param lat
     * @param lon
     * @return
     */
    public boolean isPointWithinBoundigBox(double lat, double lon) {
        return (lat >= south && lat <= north && lon <= east && lon >= west);
    }
}