package app.cache.entity;

import org.springframework.data.annotation.Id;

/**
 * Entity class for Parameters that influence assistance in the app
 */
public class Rules {
    @Id
    public String id;

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
}
