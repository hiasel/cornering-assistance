package osm;

import detection.Curve;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import util.OverpassUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by matthias on 30.05.17.
 */
public class OSMParser {
    private HashMap<Long, Node> nodes = new HashMap<>();
    private ArrayList<Way> ways = new ArrayList<>();

    public OSMParser() {

    }

    public ArrayList<Way> getWays() {
        return ways;
    }

    public void parseOverpassResponse(JSONObject response) {
        JSONArray elements = (JSONArray) response.get("elements");
        for (int i = 0; i < elements.size(); i++) {
            JSONObject element = (JSONObject) elements.get(i);
            long id = Long.parseLong(element.get("id").toString());
            String type = element.get("type").toString();
            if (type.equals("way")) {
                Way way = new Way(id);
                way.parseEntity(element);
                this.ways.add(way);
            } else if (type.equals("node")) {
                Node node = new Node(id);
                node.parseEntity(element);
                this.nodes.put(node.getId(), node);
            }
        }

        for (Way way : this.ways) {
            ArrayList<Node> nodes = new ArrayList<>();
            JSONArray nodeIds = way.getNodeIds();
            for (int i = 0; i < nodeIds.size(); i++) {
                nodes.add(this.nodes.get(Long.parseLong(nodeIds.get(i).toString())));
            }
            way.setNodes(nodes);
        }
    }

    public void detectCurves() {
        for (Way way : ways) {
            way.detectCurves();
        }
    }

    @SuppressWarnings("unchecked")
    public JSONObject toJSONResponse() {
        JSONObject json = new JSONObject();

        JSONArray curveArray = new JSONArray();

        for (Way way : ways) {

            for (Curve curve : way.getCurves()) {
                JSONObject element = new JSONObject();
                JSONObject startLatLon = new JSONObject();
                startLatLon.put("lat", curve.getStart().getLatitude());
                startLatLon.put("lon", curve.getStart().getLongitude());
                if (curve.getStartBearing() != null) {
                    startLatLon.put("bearing", curve.getStartBearing());
                }
                element.put("start", startLatLon);

                JSONObject endLatLon = new JSONObject();
                endLatLon.put("lat", curve.getEnd().getLatitude());
                endLatLon.put("lon", curve.getEnd().getLongitude());
                if (curve.getEndBearing() != null) {
                    endLatLon.put("bearing", curve.getEndBearing());
                }
                element.put("end", endLatLon);

                JSONObject centerPointLatLon = new JSONObject();
                centerPointLatLon.put("lat", curve.getCenterPoint().getLatitude());
                centerPointLatLon.put("lon", curve.getCenterPoint().getLongitude());
                element.put("centerPoint", centerPointLatLon);

                element.put("type", curve.getType().toString());
                element.put("radius", Math.floor(curve.getRadius()));
                element.put("length", Math.floor(curve.getLength()));

                curveArray.add(element);
            }
        }
        json.put("curves", curveArray);
        return json;
    }

    public JSONObject nodesToJSON() {
        JSONObject json = new JSONObject();

        JSONArray wayArray = new JSONArray();

        for (Way way : ways) {
            JSONObject wayJSON = new JSONObject();
            wayJSON.put("ID", way.getId());
            wayJSON.put("Name", way.getName());
            JSONArray nodeArray = new JSONArray();
            for (Node node : way.getNodes()) {
                JSONObject nodeJSON = new JSONObject();
                nodeJSON.put("lat", node.getLatitude());
                nodeJSON.put("lon", node.getLongitude());
                nodeArray.add(nodeJSON);
            }
            wayJSON.put("nodes", nodeArray);
            wayArray.add(wayJSON);
        }
        json.put("ways", wayArray);
        return json;
    }
}
