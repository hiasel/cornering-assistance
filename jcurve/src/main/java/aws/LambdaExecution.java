package aws;

import cache.CurveCache;
import cache.Params;
import cache.Rules;
import ch.hsr.geohash.GeoHash;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.util.Base64;
import detection.Curve;
import detection.DetectionParams;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import osm.OSMParser;
import osm.Way;
import util.BoundingBox;
import util.OverpassUtil;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * The class containing the "handleRequest" method that will be executed by AWS Lambda
 */
public class LambdaExecution implements RequestHandler<RequestClass, String> {

    // This variable will hold your decrypted key. Decryption happens
    // on first invocation when the container is initialized and never again
    // for subsequent invocations
    private static String DECRYPTED_KEY = decryptKey();

    @Override
    public String handleRequest(RequestClass request, Context context) {
        double latitude = request.latitude;
        double longitude = request.longitude;

        // Initialize Connection to MongoDB Cluster
        CurveCache cache = new CurveCache(false, DECRYPTED_KEY);
        Params params = cache.getParams();
        DetectionParams.RADIUS_THRESHOLD = params.getRadiusThreshold();
        DetectionParams.GEOHASH_PRECISION = params.getGeoHashPrecision();
        DetectionParams.LENGTH_THRESHOLD = params.getLengthThreshold();
        Rules rules = cache.getRules();

        // Check if given location is contained in cache (MongoDB)
        GeoHash geoHash = GeoHash.withCharacterPrecision(latitude, longitude, DetectionParams.GEOHASH_PRECISION);
        BoundingBox bb = BoundingBox.fromGeoHashBoundingBox(geoHash.getBoundingBox());
        JSONArray cachedCurves = cache.findCurves(bb);
        if (cachedCurves.size() > 0 ) {
            // return cached curves to user
            JSONObject response = new JSONObject();
            response.put("curves", cachedCurves);
            response.put("bounding-box", bb.toJSON());
            response.put("rules", rules.toJSON());
            return response.toJSONString();
        } else {
            // Cache is empty: Call OSM
            JSONObject overpassResponse = OverpassUtil.getWaysInBoundingBox(bb);
            OSMParser osmParser = new OSMParser();
            osmParser.parseOverpassResponse(overpassResponse);
            osmParser.detectCurves();
            // Store detected curves in Cache
            for (Way way : osmParser.getWays()) {
                for (Curve curve : way.getCurves()) {
                    cache.insertCurve(curve);
                }
            }

            // Return reponse to user
            JSONObject response = osmParser.toJSONResponse();
            response.put("bounding-box", bb.toJSON());
            response.put("rules", rules.toJSON());
            return response.toJSONString();
        }
    }

    private static String decryptKey() {
        byte[] encryptedKey = Base64.decode(System.getenv("db_pw"));

        AWSKMS client = AWSKMSClientBuilder.defaultClient();

        DecryptRequest request = new DecryptRequest()
                .withCiphertextBlob(ByteBuffer.wrap(encryptedKey));

        ByteBuffer plainTextKey = client.decrypt(request).getPlaintext();
        return new String(plainTextKey.array(), Charset.forName("UTF-8"));
    }
}
