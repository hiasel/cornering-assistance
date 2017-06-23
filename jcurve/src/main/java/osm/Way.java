package osm;

import com.peertopark.java.geocalc.EarthCalc;
import com.peertopark.java.geocalc.Point;
import detection.Curve;
import detection.DetectionParams;
import detection.Triangle;
import detection.TwistType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by matthias on 30.05.17.
 */
public class Way extends Entity{
    private String name;
    private String highway;
    private String maxspeed;
    private JSONArray nodeIds;
    private ArrayList<Node> nodes;
    private ArrayList<Curve> curves;

    public Way(long id) {
        super(id);
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getHighway() {
        return highway;
    }

    private void setHighway(String highway) {
        this.highway = highway;
    }

    public String getMaxspeed() {
        return maxspeed;
    }

    private void setMaxspeed(String maxspeed) {
        this.maxspeed = maxspeed;
    }

    private void setNodeIds(JSONArray nodeIds) {
        this.nodeIds = nodeIds;
    }

    public JSONArray getNodeIds() {
        return nodeIds;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public ArrayList<Curve> getCurves() {
        return curves;
    }

    @Override
    public void parseEntity(JSONObject json) {
        setNodeIds((JSONArray) json.get("nodes"));
        JSONObject tags = (JSONObject) json.get("tags");
        if (tags != null) {
            setName((String) tags.get("name"));
            setHighway((String) tags.get("highway"));
            setMaxspeed((String) tags.get("maxspeed"));
        }
    }

    @Override
    public String toString() {
        String string =  getName() + "\n" + getHighway() + "\n";
        for (Node node: nodes) {
            string += node.toString() + "\n";
        }
        return string;
    }

    /**
     * Detect all Curves of this Way
     */
    public void detectCurves() {
        ArrayList<Node> nodes = getNodes();
        curves = new ArrayList<>();

        // 1. Stage: Detect curves using a moving triangle (curves can grow at each iteration)
        for (int index=0; index < (nodes.size() - 2); index++) {
            Triangle triangle = new Triangle(nodes.get(index), nodes.get(index+1), nodes.get(index+2));

            Curve currentCurve = new Curve(triangle.getS1().getStart(), triangle.getS2().getEnd(), triangle.getAllPoints(), triangle.getTwistType());
            Curve lastCurve = getPreviousCurve();
            if (lastCurve == null) {
                // first curve or straight
                curves.add(currentCurve);
            } else {
                if (lastCurve.getType() == triangle.getTwistType()) {
                    // extend curve
                    lastCurve.addPoint(triangle.getS2().getEnd());
                    lastCurve.setEnd(triangle.getS2().getEnd());
                } else {
                    // new curve
                    curves.add(currentCurve);
                }
            }
        }

        // 2. Stage: Remove Straights
        curves.removeIf(curve -> curve.getType().equals(TwistType.STRAIGHT));

        // 3. Stage: Merge connecting curves
        ListIterator<Curve> iter = curves.listIterator();
        while(iter.hasNext()){
            Curve currentCurve = iter.next();
            if (iter.hasNext()) {
                Curve nextCurve = curves.get(iter.nextIndex());
                if (nextCurve.getType().equals(currentCurve.getType())) {
                    if (EarthCalc.getDistance(currentCurve.getEnd(), nextCurve.getStart()) < DetectionParams.MERGE_THRESHOLD) {
                        nextCurve.setStart(currentCurve.getStart());
                        nextCurve.prependPoints(currentCurve.getPoints());
                        iter.remove();
                    }
                }
            }
        }

        // 4. Stage: fix gaps (end/start points often are far away from other curve points)
        for (Curve curve: curves) {
            List<Point> points = curve.getPoints();
            if (points.size() > 3 ) {
                if (EarthCalc.getDistance(curve.getEnd(), points.get(points.size()-2)) > DetectionParams.GAP_THRESHOLD) {
                    curve.setEnd(points.get(points.size()-2));
                    curve.removePoint(points.size()-1);
                }
                if (EarthCalc.getDistance(curve.getStart(), points.get(1)) > DetectionParams.GAP_THRESHOLD) {
                    curve.setStart(points.get(1));
                    curve.removePoint(0);
                }
            }
        }


        // 5. Stage: Calculate properties of curves
        iter = curves.listIterator();
        while(iter.hasNext()){
            Curve curve = iter.next();
            curve.removeDuplicatePoints();
            if (curve.getPoints().size() < 3) {
                iter.remove();
                continue;
            }
            curve.calculateCenterPoint();
            curve.calculateLength();
            curve.calculateCircumCircleRadius();
            curve.calculateStartBearing();
            curve.calculateEndBearing();

            if (curve.getRadius() == null || curve.getRadius() > DetectionParams.RADIUS_THRESHOLD) {
                iter.remove();
                continue;
            }

            if (curve.getLength() < DetectionParams.LENGTH_THRESHOLD) {
                iter.remove();
            }
        }


    }

    /**
     * Get the previous Curve
     * @return
     */
    private Curve getPreviousCurve() {
        if (curves.size() > 0) {
            return (curves.get(curves.size()-1));
        } else {
            return(null);
        }
    }


}
