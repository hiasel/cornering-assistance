package com.k.matthias.curveassistant.detection;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.k.matthias.curveassistant.AssistantActivity;
import com.k.matthias.curveassistant.aws.AWSConnector;
import com.k.matthias.curveassistant.aws.RequestClass;
import com.k.matthias.curveassistant.aws.ResultReceiver;
import com.k.matthias.curveassistant.db.CurveRepo;
import com.k.matthias.curveassistant.db.entity.Curve;
import com.k.matthias.curveassistant.db.entity.Point;
import com.k.matthias.curveassistant.util.BearingUtil;
import com.k.matthias.curveassistant.util.BoundingBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Algorithm for detecting nearest curves from the drivers location
 * using data available from either AWS Lambda or the local cache
 */

public class NearestCurveDetection implements ResultReceiver {
    private static final String TAG = AssistantActivity.class.getSimpleName();

    private final CurveDetectedListener mListener;
    private Context mContext;
    private CurveRepo mCurveRepo;



    private Location mLastLocation = null;
    private Location mCurrentLocation = null;
    private double mCurrentDriverBearing = 0.0;
    private boolean mIsLambdaTaskRunning = false;
    private BoundingBox mCurrentBoundingBox = null;
    private Curve mApproachingCurve = null;
    private Curve mLastApprochingCurve = null; // improvement (sometimes right after curve has passed and bounding box was just left, the same curve could be triggered although it was passeD)
    private int mWarnBeforeCurveMeters = 150; // A default value, will be overwritten by Lambda calls
    private float mLastDistanceToCurveStart = mWarnBeforeCurveMeters;
    private int mDangerousUpperLimit = 50; // A default value, will be overwritten by Lambda calls
    private int mMediumUpperLimit = 75; // A default value, will be overwritten by Lambda calls
    private ArrayList<Curve> curveBlackList = new ArrayList<>(); // Blacklist of already driven curves
    private static final int BLACKLIST_SIZE = 10; // size of blacklist

    public NearestCurveDetection(Context context, CurveDetectedListener listener) {
        this.mContext = context;
        this.mListener = listener;
        initDetection();
    }

    private void initDetection() {
        this.mCurveRepo = new CurveRepo(mContext);
    }

    public void driverLocationUpdate(Location newLocation) {
        this.mCurrentLocation = newLocation;
        if (mLastLocation != null) {
            mCurrentDriverBearing = BearingUtil.getBearing(mLastLocation, mCurrentLocation);
            if (mApproachingCurve != null) {
                // A curve is approaching
                handleApproachingCurve();
            } else if (mCurrentBoundingBox != null && mCurrentBoundingBox.isPointWithinBoundigBox(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())) {
                // Driver is still within Bounding Box
                checkForUpcomingCurves();
            } else {
                // Driver out of Bounding Box: Requesting new Curve Infos
                requestNewCurveInformation();
            }
        } else {
            requestNewCurveInformation();
        }
        // in the very end: Update Last Location
        mLastLocation = mCurrentLocation;
    }

    private void handleApproachingCurve() {

        // Driver is approaching the curve (e.g. he is less than CURVE_APPROACHING_DISTANCE away from the curve's center)
        float distanceToCurveStart = mApproachingCurve.distanceToStart(mCurrentLocation);
        mListener.onCurveApproaching(Math.round(distanceToCurveStart));

        if ((distanceToCurveStart <= mLastDistanceToCurveStart)) {
            // driver is still before curve's starting point
            mListener.onEnteringCurve(distanceToCurveStart);
            mLastDistanceToCurveStart = distanceToCurveStart;

        } else {
            mListener.onPassedCurve();
            // add to list of recently driven curves ("blackList")
            if (curveBlackList.size() > BLACKLIST_SIZE) {
                curveBlackList.clear();
            }
            curveBlackList.add(mApproachingCurve);
            // driver passed curve's starting point: curve is removed
            mCurveRepo.removeCurve(mApproachingCurve.getId());
            mLastApprochingCurve = mApproachingCurve;
            mApproachingCurve = null;
            mLastDistanceToCurveStart = mWarnBeforeCurveMeters;
        }

    }

    private void checkForUpcomingCurves() {
        Curve nearest = mCurveRepo.getNearestCurve(new Point(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
        if (nearest != null && !curveBlackList.contains(nearest)) {
            if (!nearest.equals(mLastApprochingCurve)) { // discard curves that have just been left
                float distanceToNearest = nearest.distanceToCenter(mCurrentLocation);
                if (distanceToNearest < mWarnBeforeCurveMeters) {
                    nearest.relocateStartEndPoints(mCurrentLocation);
                    mListener.onApproachingCurveCandidateDetected(nearest);
                    // Check if candidate fits current driving direction (it might be another curve not in the direction of the driver)
                    if (nearest.hasMatchingStartBearing(mCurrentDriverBearing)) {
                        mApproachingCurve = nearest;
                        mListener.onApproachingCurveDetected(nearest);
                        if (mApproachingCurve.getRadius() <= mDangerousUpperLimit) {
                            mListener.onDangerousCurveApproaching();
                        } else if (mApproachingCurve.getRadius() <= mMediumUpperLimit) {
                            mListener.onMediumCurveApproaching();
                        } else {
                            mListener.onEasyCurveApproaching();
                        }
                    }
                }
            }
        }
    }

    private void requestNewCurveInformation() {
        if (!mIsLambdaTaskRunning) {
            Log.d(TAG, "Calling AWS");
            AWSConnector.callAWSLambda(mContext, new RequestClass(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), this);
            mIsLambdaTaskRunning = true;
        }
    }

    @Override
    public void onResultReceived(String result) {
        Log.d(TAG, "AWS result: " + result);
        // Result received from AWS Lambda Function call
        mIsLambdaTaskRunning = false;
        try {
            JSONObject json = new JSONObject(result);
            JSONObject bbJson = json.getJSONObject("bounding-box");
            mCurrentBoundingBox = BoundingBox.fromJSON(bbJson);
            mListener.onBoundingBoxUpdate(mCurrentBoundingBox);
            JSONArray curvesJson = json.getJSONArray("curves");
            mCurveRepo.removeOldCurves();
            mCurveRepo.insert(curvesJson);
            JSONObject rules = json.getJSONObject("rules");
            mWarnBeforeCurveMeters = rules.getInt("warnBeforeCurveMeters");
            mDangerousUpperLimit = rules.getInt("dangerousUpperLimit");
            mMediumUpperLimit = rules.getInt("mediumUpperLimit");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
