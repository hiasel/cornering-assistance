package com.k.matthias.curveassistant.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.k.matthias.curveassistant.db.entity.Curve;
import com.k.matthias.curveassistant.db.entity.Point;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Local Curve Cache
 */

public class CurveRepo {

    private CurveDbHelper dbHelper;

    public CurveRepo(Context context) {
        dbHelper = new CurveDbHelper(context);
    }

    public void removeOldCurves() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from "+ CurveDbContract.CurveDbEntry.TABLE_NAME);
    }

    public void removeCurve(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(CurveDbContract.CurveDbEntry.TABLE_NAME, "_id = ? ", new String[] { Long.toString(id) });
    }


    public void insert(JSONArray curves) {
        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        ContentValues values = new ContentValues();
        try {
            for (int i = 0; i < curves.length(); i++) {
                JSONObject curve = (JSONObject) curves.get(i);

                JSONObject start = (JSONObject) curve.get("start");
                values.put(CurveDbContract.CurveDbEntry.COLUMN_START_LAT, start.getDouble("lat"));
                values.put(CurveDbContract.CurveDbEntry.COLUMN_START_LON, start.getDouble("lon"));
                double startBearing = start.optDouble("bearing");
                if (startBearing != Double.NaN) {
                    values.put(CurveDbContract.CurveDbEntry.COLUMN_START_BEARING, startBearing);
                }
                JSONObject end = (JSONObject) curve.get("end");
                values.put(CurveDbContract.CurveDbEntry.COLUMN_END_LAT, end.getDouble("lat"));
                values.put(CurveDbContract.CurveDbEntry.COLUMN_END_LON, end.getDouble("lon"));
                double endBearing = end.optDouble("bearing");
                if (endBearing != Double.NaN) {
                    values.put(CurveDbContract.CurveDbEntry.COLUMN_END_BEARING, endBearing);
                }
                JSONObject centerPoint = (JSONObject) curve.get("centerPoint");
                values.put(CurveDbContract.CurveDbEntry.COLUMN_CENTER_LAT, centerPoint.getDouble("lat"));
                values.put(CurveDbContract.CurveDbEntry.COLUMN_CENTER_LON, centerPoint.getDouble("lon"));


                double radius = Double.parseDouble(curve.get("radius").toString());
                double length = Double.parseDouble(curve.get("length").toString());
                values.put(CurveDbContract.CurveDbEntry.COLUMN_RADIUS, radius);
                values.put(CurveDbContract.CurveDbEntry.COLUMN_LENGTH, length);

                // Inserting Row
                long curveId = db.insert(CurveDbContract.CurveDbEntry.TABLE_NAME, null, values);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.close(); // Closing database connection
        }

    }

    public Curve getNearestCurve(Point location) {
        double fudge = Math.pow(Math.cos(Math.toRadians(location.getLat())), 2);

        SQLiteDatabase db = dbHelper.getReadableDatabase();


        String[] projection = {
                CurveDbContract.CurveDbEntry._ID,
                CurveDbContract.CurveDbEntry.COLUMN_START_LAT,
                CurveDbContract.CurveDbEntry.COLUMN_START_LON,
                CurveDbContract.CurveDbEntry.COLUMN_END_LAT,
                CurveDbContract.CurveDbEntry.COLUMN_END_LON,
                CurveDbContract.CurveDbEntry.COLUMN_CENTER_LAT,
                CurveDbContract.CurveDbEntry.COLUMN_CENTER_LON,
                CurveDbContract.CurveDbEntry.COLUMN_LENGTH,
                CurveDbContract.CurveDbEntry.COLUMN_RADIUS,
                CurveDbContract.CurveDbEntry.COLUMN_START_BEARING,
                CurveDbContract.CurveDbEntry.COLUMN_END_BEARING,
        };



        String sortOrder =
                "((%f - " + CurveDbContract.CurveDbEntry.COLUMN_CENTER_LAT + ") * (%f - " + CurveDbContract.CurveDbEntry.COLUMN_CENTER_LAT + ") + " +
                " (%f - " + CurveDbContract.CurveDbEntry.COLUMN_CENTER_LON + ") * (%f - " + CurveDbContract.CurveDbEntry.COLUMN_CENTER_LON + ") * %f)";

        sortOrder = String.format(Locale.ROOT, sortOrder, location.getLat(), location.getLat(), location.getLon(), location.getLon(), fudge);



        Cursor cursor = db.query(
                CurveDbContract.CurveDbEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        Curve curve = null;

        if (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(CurveDbContract.CurveDbEntry._ID));
            double startLat = cursor.getDouble(cursor.getColumnIndex(CurveDbContract.CurveDbEntry.COLUMN_START_LAT));
            double startLon = cursor.getDouble(cursor.getColumnIndex(CurveDbContract.CurveDbEntry.COLUMN_START_LON));
            double endLat = cursor.getDouble(cursor.getColumnIndex(CurveDbContract.CurveDbEntry.COLUMN_END_LAT));
            double endLon = cursor.getDouble(cursor.getColumnIndex(CurveDbContract.CurveDbEntry.COLUMN_END_LON));
            double centerLat = cursor.getDouble(cursor.getColumnIndex(CurveDbContract.CurveDbEntry.COLUMN_CENTER_LAT));
            double centerLon = cursor.getDouble(cursor.getColumnIndex(CurveDbContract.CurveDbEntry.COLUMN_CENTER_LON));
            double length = cursor.getDouble(cursor.getColumnIndex(CurveDbContract.CurveDbEntry.COLUMN_LENGTH));
            double radius = cursor.getDouble(cursor.getColumnIndex(CurveDbContract.CurveDbEntry.COLUMN_RADIUS));
            double startBearing = cursor.getDouble(cursor.getColumnIndex(CurveDbContract.CurveDbEntry.COLUMN_START_BEARING));
            double endBearing = cursor.getDouble(cursor.getColumnIndex(CurveDbContract.CurveDbEntry.COLUMN_END_BEARING));
            curve = new Curve(id, startLat, startLon, endLat, endLon, centerLat, centerLon, length, radius);
            curve.setStartBearing(startBearing);
            curve.setEndBearing(endBearing);
        }

        cursor.close();
        return curve;

    }






}
