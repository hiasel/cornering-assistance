package cache;

import org.json.simple.JSONObject;

/**
 * Entity class for Rules that influence assistance in the app
 */
public class Rules {

    private int dangerousUpperLimit;
    private int mediumUpperLimit;
    private int warnBeforeCurveMeters;

    public Rules() {
    }

    public Rules(int dangerousUpperLimit, int mediumUpperLimit, int warnBeforeCurveMeters) {
        this.dangerousUpperLimit = dangerousUpperLimit;
        this.mediumUpperLimit = mediumUpperLimit;
        this.warnBeforeCurveMeters = warnBeforeCurveMeters;
    }

    public int getDangerousUpperLimit() {
        return dangerousUpperLimit;
    }

    public void setDangerousUpperLimit(int dangerousUpperLimit) {
        this.dangerousUpperLimit = dangerousUpperLimit;
    }

    public int getMediumUpperLimit() {
        return mediumUpperLimit;
    }

    public void setMediumUpperLimit(int mediumUpperLimit) {
        this.mediumUpperLimit = mediumUpperLimit;
    }

    public int getWarnBeforeCurveMeters() {
        return warnBeforeCurveMeters;
    }

    public void setWarnBeforeCurveMeters(int warnBeforeCurveMeters) {
        this.warnBeforeCurveMeters = warnBeforeCurveMeters;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("dangerousUpperLimit", this.dangerousUpperLimit);
        json.put("mediumUpperLimit", this.mediumUpperLimit);
        json.put("warnBeforeCurveMeters", this.warnBeforeCurveMeters);
        return json;
    }
}
