package detection;

/**
 * Detection Parameters
 */
public class DetectionParams {
    /**
     * Minimum angle between two segments of a triangle to be considered a curve (otherwise its a straight)
     */
    public static double ANGLE_THRESHOLD = 2.0;
    /**
     * Maximum Radius to consider for detection
     */
    public static double RADIUS_THRESHOLD = 100;
    /**
     * Sometimes the curve detection detects two distinct curves of same type (e.g. both right or left) that are very close to each other.
     * If the distance between two curves of same type exceeds this threshold, two curves are merged together
     */
    public static double MERGE_THRESHOLD = 50;
    /**
     * Sometimes start/end points of a curve are far away from the rest of the curve.
     * If the distance between start/end point and the second/next_to_last point of the curve exceeds this threshold, the start/end point is moved
     */
    public static double GAP_THRESHOLD = 30;

    /**
     * Specifies the character precision of the GeoHash calculated from the driver's position
     * The larger the precision (maximum = 12 characters) the smaller the bounding box of the geohash.
     */
    public static int GEOHASH_PRECISION = 12;

    /**
     * Minimum Length a curve needs to have
     */
    public static int LENGTH_THRESHOLD = 100;


}
