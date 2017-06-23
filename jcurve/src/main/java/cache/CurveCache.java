package cache;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import detection.Curve;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import util.BoundingBox;

import java.util.Arrays;

/**
 * Connection to MongoDB instance
 */
public class CurveCache {
    private final JSONParser parser;
    private MongoDatabase database;
    private MongoCollection<Document> curveCollection;
    private MongoCollection<Document> rulesCollection;
    private MongoCollection<Document> paramsCollection;
    private String CONNECTION_URI = "mongodb://%s:%s@%s";

    public CurveCache(boolean local, String pw) {
        parser = new JSONParser();

        if (local) {
            initLocalConnection();
        } else {
            initConnection(pw);
        }
        initCollections();
    }

    /**
     * Initializes a connection to a remote MongoDB cluster (Atlas DB)
     * @param pw
     */
    private void initConnection(String pw) {
        CONNECTION_URI = String.format(CONNECTION_URI, System.getenv("db_user"), pw, System.getenv("db_uri"));
        MongoClientURI uri = new MongoClientURI(
                CONNECTION_URI);
        MongoClient mongoClient = new MongoClient(uri);
        this.database = mongoClient.getDatabase("curvedb");
    }

    /**
     * Initializes a connection to a local MongoDB database (for testing purposes)
     */
    private void initLocalConnection() {
        MongoClient mongoClient = new MongoClient("localhost");
        database = mongoClient.getDatabase("curvedb");
    }


    private void initCollections() {
        curveCollection = database.getCollection("curves");
        rulesCollection = database.getCollection("rules");
        paramsCollection = database.getCollection("params");

    }

    public Rules getRules() {
        Document doc = rulesCollection.find().first();
        return new Rules(doc.getInteger("dangerousUpperLimit"), doc.getInteger("mediumUpperLimit"), doc.getInteger("warnBeforeCurveMeters"));
    }

    public Params getParams() {
        Document doc = paramsCollection.find().first();
        return new Params(doc.getInteger("radiusThreshold"),
                doc.getInteger("geoHashPrecision"),
                doc.getInteger("lengthThreshold")
                );
    }

    public JSONArray findCurves(BoundingBox bb) {
        JSONArray curveArray = new JSONArray();
        curveCollection.find(Filters.geoWithinBox("centerPoint", bb.getSouth(), bb.getWest(), bb.getNorth(), bb.getEast())).forEach(new Block<Document>() {
            @Override
            public void apply(Document document) {
                JSONObject element = documentToJSON(document);
                if (element != null) {
                    curveArray.add(element);
                }
            }
        });
        return curveArray;

    }

    private JSONObject documentToJSON(Document document) {
        try {
            JSONObject cacheJSON = (JSONObject) parser.parse(document.toJson());

            JSONObject element = new JSONObject();

            JSONObject latlon = new JSONObject();
            JSONObject cachePoint = (JSONObject) cacheJSON.get("startPoint");
            JSONArray coordinatesArray = (JSONArray) cachePoint.get("coordinates");
            latlon.put("lat", coordinatesArray.get(0));
            latlon.put("lon", coordinatesArray.get(1));
            latlon.put("bearing", cachePoint.get("bearing"));
            element.put("start", latlon);

            latlon = new JSONObject();
            cachePoint = (JSONObject) cacheJSON.get("endPoint");
            coordinatesArray = (JSONArray) cachePoint.get("coordinates");
            latlon.put("lat", coordinatesArray.get(0));
            latlon.put("lon", coordinatesArray.get(1));
            latlon.put("bearing", cachePoint.get("bearing"));
            element.put("end", latlon);

            latlon = new JSONObject();
            cachePoint = (JSONObject) cacheJSON.get("centerPoint");
            coordinatesArray = (JSONArray) cachePoint.get("coordinates");
            latlon.put("lat", coordinatesArray.get(0));
            latlon.put("lon", coordinatesArray.get(1));
            element.put("centerPoint", latlon);


            element.put("type", cacheJSON.get("type"));
            element.put("radius", Math.floor(Double.parseDouble(cacheJSON.get("radius").toString())));
            element.put("length", Math.floor(Double.parseDouble(cacheJSON.get("length").toString())));

            return element;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void insertCurve(Curve curve) {
        Document doc = new Document(
                "radius", curve.getRadius())
                .append("length", curve.getLength())
                .append("type", curve.getType().name())
                .append("centerPoint", new Document("type", "Point").append("coordinates", Arrays.asList(curve.getCenterPoint().getLatitude(), curve.getCenterPoint().getLongitude())))
                .append("startPoint", new Document("type", "Point").append("bearing", curve.getStartBearing()).append("coordinates", Arrays.asList(curve.getStart().getLatitude(), curve.getStart().getLongitude())))
                .append("endPoint", new Document("type", "Point").append("bearing", curve.getEndBearing()).append("coordinates", Arrays.asList(curve.getEnd().getLatitude(), curve.getEnd().getLongitude())));
        curveCollection.insertOne(doc);

    }
}
