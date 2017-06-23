package com.k.matthias.curveassistant.db.entity;

import android.location.Location;
import android.util.Log;

import com.k.matthias.curveassistant.util.BearingUtil;

/**
 * Created by matthias on 12.06.17.
 */

public class Curve {
    private long id;
    private Point start;
    private Point end;
    private Point center;
    private double radius;
    private double length;
    private double startBearing;
    private double endBearing;
    private boolean entered = false;

    public Curve() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        Curve compare = (Curve) obj;
        if (compare.getRadius() == this.getRadius() && compare.getLength() == this.getLength()) {
            return true;
        } else {
            return false;
        }
    }

    public Curve(Point start, Point end, Point center, double radius, double length) {
        this.start = start;
        this.end = end;
        this.center = center;
        this.radius = radius;
        this.length = length;
    }

    public Curve(long id, double startLat, double startLon, double endLat, double endLon, double centerLat, double centerLon, double length, double radius) {
        this.id = id;
        this.start = new Point(startLat, startLon);
        this.end = new Point(endLat, endLon);
        this.center = new Point(centerLat, centerLon);
        this.length = length;
        this.radius = radius;
    }

    public boolean isEntered() {
        return entered;
    }

    public void setEntered(boolean entered) {
        this.entered = entered;
    }

    /**
     * Checks whether a given bearing fits the curve's start bearing
     * @param bearing
     * @return
     */
    public boolean hasMatchingStartBearing(double bearing) {
        double bearingDiff = BearingUtil.calculateAngle(bearing, startBearing);
        Log.d("Curve", "bearing location: " + bearing);
        Log.d("Curve", "start bearing: " + startBearing);
        Log.d("Curve", "diff: " + bearingDiff);
        if (bearingDiff < 45) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks whether a given bearing fits the curve's end bearing
     * @param bearing
     * @return
     */
    public boolean hasMatchingEndBearing(double bearing) {
        double bearingDiff = BearingUtil.calculateAngle(bearing, endBearing);
        Log.d("Curve", "bearing location: " + bearing);
        Log.d("Curve", "end bearing: " + endBearing);
        Log.d("Curve", "diff: " + bearingDiff);

        if (bearingDiff < 45) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Relocates start and end point of the curve according to given Location.
     * In case the end point lies closer than the start point, the two are switched.
     * @param location
     */
    public void relocateStartEndPoints(Location location) {
        float distToStart = distanceToStart(location);
        float distToEnd = distanceToEnd(location);
        Log.d("Curve", "distToStart: " + distToStart);
        Log.d("Curve", "distToEnd: " + distToEnd);
        if (distToEnd < distToStart) {
            // switch start/end point
            Point copy_start = start;
            start = end;
            end = copy_start;
            // bearings also need to be switched by 180°
            double copy_startBearing = startBearing;
            startBearing = endBearing;
            endBearing = copy_startBearing;
            startBearing = (startBearing + 180)%360;
            endBearing = (endBearing + 180)%360;
            Log.d("Curve", "switchted start/end");

        }
    }

    public float distanceToStart(Location location) {
        float[] results = new float[1];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(), getStart().getLat(), getStart().getLon(), results);
        return results[0];
    }

    public float distanceToEnd(Location location) {
        float[] results = new float[1];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(), getEnd().getLat(), getEnd().getLon(), results);
        return results[0];
    }

    public float distanceToCenter(Location location) {
        float[] results = new float[1];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(), getCenter().getLat(), getCenter().getLon(), results);
        return results[0];
    }

    public double getStartBearing() {
        return startBearing;
    }

    public void setStartBearing(double startBearing) {
        this.startBearing = startBearing;
    }

    public double getEndBearing() {
        return endBearing;
    }

    public void setEndBearing(double endBearing) {
        this.endBearing = endBearing;
    }

    public Point getStart() {
        return start;
    }

    public void setStart(Point start) {
        this.start = start;
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
