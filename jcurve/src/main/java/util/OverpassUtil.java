package util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Util class to request data from OpenStreetMaps using Overpass API
 */
public class OverpassUtil {

    private static final String OVERPASS_BASE_URL = "http://overpass-api.de/api/interpreter?data=";

    private static final String QUERY_OUT_FORMAT = "[out:json]";
    private static final String QUERY_WAY_FILTER = "way[~\"highway\"~\"^primary$|^secondary$|^tertiary$|^motorway$\"];";
    private static final String QUERY_NODE_FILTER = "foreach(\n" +
            "  out;\n" +
            "  node(w);\n" +
            "  out;\n" +
            ");";

    /**
     * Returns an Overpass Query that fetches all Nodes and Ways within a bounding box around the given Location in lat/lon
     * @param bb BoundingBox
     * @return
     */
    public static JSONObject getWaysInBoundingBox(BoundingBox bb) {
        String query = getQuery(bb);
        JSONObject response = new JSONObject();
        try {
            query = OVERPASS_BASE_URL + URLEncoder.encode(query, "UTF-8");
            JSONParser jsonParser = new JSONParser();
            response = (JSONObject) jsonParser.parse(readUrl(query));
        } catch (Exception e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("request-error", e.toString());
            response = new JSONObject(errorMap);
        }
        return response;
    }

    private static String getQuery(BoundingBox bb) {
        String bbOverpassString = "[bbox:"+ bb.getSouth() + "," + bb.getWest() + "," + bb.getNorth() + ", " + bb.getEast() + "];";
        return QUERY_OUT_FORMAT + bbOverpassString + QUERY_WAY_FILTER + QUERY_NODE_FILTER;
    }

    /**
     * Download File from URL (HTTP)
     * @param urlString
     * @return
     * @throws Exception
     */
    public static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    /**
     * Download File from URL (HTTPS)
     * @param urlString
     * @return
     * @throws Exception
     */
    public static String readHTTPSUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }
}
