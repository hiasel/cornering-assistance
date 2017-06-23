package util;

import com.peertopark.java.geocalc.EarthCalc;
import com.peertopark.java.geocalc.Point;
import org.json.simple.JSONObject;

/**
 * Represents a bounding box of a location
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

    /**
     * Returns a bounding box from a center point
     * @param lat       Latitude of center point
     * @param lon       Longitude of center point
     * @param width     Width in meters of bounding box
     * @param height    Height in meters of bounding box
     * @return
     */
    public static BoundingBox fromCenterPoint(double lat, double lon, double width, double height) {
        double south = GeoUtil.movePoint(lat, lon, height/2, GeoUtil.BEARING_SOUTH)[0];
        double west = GeoUtil.movePoint(lat, lon, width/2, GeoUtil.BEARING_WEST)[1];
        double north = GeoUtil.movePoint(lat, lon, height/2, GeoUtil.BEARING_NORTH)[0];
        double east = GeoUtil.movePoint(lat, lon, width/2, GeoUtil.BEARING_EAST)[1];
        return new BoundingBox(south, west, north, east);
    }

    /**
     * Converts a BoundingBox created by the GeoHash Library to this BoundingBox
     * @param geoHashBoundingBox
     * @return
     */
    public static BoundingBox fromGeoHashBoundingBox(ch.hsr.geohash.BoundingBox geoHashBoundingBox) {
        return new BoundingBox(geoHashBoundingBox.getMinLat(), geoHashBoundingBox.getMinLon(), geoHashBoundingBox.getMaxLat(), geoHashBoundingBox.getMaxLon());
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("south", south);
        json.put("west", west);
        json.put("north", north);
        json.put("east", east);
        return json;
    }

    public BoundingBox fromJSON(JSONObject json) {
        BoundingBox bb = new BoundingBox(
                Double.parseDouble(json.get("south").toString()),
                Double.parseDouble(json.get("west").toString()),
                Double.parseDouble(json.get("north").toString()),
                Double.parseDouble(json.get("east").toString())
        );
        return bb;
    }

    /**
     * Checks weather or not a Point lies within the Bounding Box
     * @param lat
     * @param lon
     * @return
     */
    public boolean isPointWithinBoundigBox(double lat, double lon) {
        return (lat >= south && lat <= north && lon >= east && lon <= west);
    }

    public double getHeight() {
        Point top = Point.create(north, west);
        Point bottom = Point.create(south, west);
        return EarthCalc.getHarvesineDistance(top, bottom);
    }

    public double getWidth() {
        Point right = Point.create(north, east);
        Point left = Point.create(north, west);
        return EarthCalc.getHarvesineDistance(right, left);
    }
}
