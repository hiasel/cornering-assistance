package com.k.matthias.curveassistant.aws;

/**
 * Created by matthias on 31.05.17.
 */

public class RequestClass {
    double latitude;
    double longitude;

    public RequestClass() {
    }

    public RequestClass(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
