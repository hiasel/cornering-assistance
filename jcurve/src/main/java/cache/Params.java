package cache;

/**
 * Entity class for Parameters that influence the curve detection
 */
public class Params {
    private int radiusThreshold;
    private int geoHashPrecision;
    private int lengthThreshold;

    public Params() {
    }

    public Params(int radiusThreshold, int geoHashPrecision, int lengthThreshold) {
        this.radiusThreshold = radiusThreshold;
        this.geoHashPrecision = geoHashPrecision;
        this.lengthThreshold = lengthThreshold;
    }

    public int getRadiusThreshold() {
        return radiusThreshold;
    }

    public void setRadiusThreshold(int radiusThreshold) {
        this.radiusThreshold = radiusThreshold;
    }

    public int getGeoHashPrecision() {
        return geoHashPrecision;
    }

    public void setGeoHashPrecision(int geoHashPrecision) {
        this.geoHashPrecision = geoHashPrecision;
    }

    public int getLengthThreshold() {
        return lengthThreshold;
    }

    public void setLengthThreshold(int lengthThreshold) {
        this.lengthThreshold = lengthThreshold;
    }
}
