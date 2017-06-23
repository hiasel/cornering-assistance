package com.k.matthias.curveassistant.detection;

import com.k.matthias.curveassistant.db.entity.Curve;
import com.k.matthias.curveassistant.util.BoundingBox;

/**
 * Created by matthias on 18.06.17.
 */

public interface CurveDetectedListener {
    void onApproachingCurveCandidateDetected(Curve curveCandidate);
    void onApproachingCurveDetected(Curve curve);
    void onEnteringCurve(float distanceLeft);
    void onPassedCurve();
    void onBoundingBoxUpdate(BoundingBox boundingBox);
    void onDangerousCurveApproaching();
    void onMediumCurveApproaching();
    void onEasyCurveApproaching();
    void onCurveApproaching(int distanceToCurve);
}
