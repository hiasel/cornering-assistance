package com.k.matthias.curveassistant.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by matthias on 12.06.17.
 */

public class CurveDbHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "CurveDB.db";

    public CurveDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CurveDbContract.CurveDbEntry.TABLE_NAME + " (" +
                    CurveDbContract.CurveDbEntry._ID + " INTEGER PRIMARY KEY," +
                    CurveDbContract.CurveDbEntry.COLUMN_CENTER_LAT + " REAL," +
                    CurveDbContract.CurveDbEntry.COLUMN_CENTER_LON + " REAL," +
                    CurveDbContract.CurveDbEntry.COLUMN_START_LAT + " REAL," +
                    CurveDbContract.CurveDbEntry.COLUMN_START_LON + " REAL," +
                    CurveDbContract.CurveDbEntry.COLUMN_END_LAT + " REAL," +
                    CurveDbContract.CurveDbEntry.COLUMN_END_LON + " REAL," +
                    CurveDbContract.CurveDbEntry.COLUMN_LENGTH + " REAL," +
                    CurveDbContract.CurveDbEntry.COLUMN_RADIUS + " REAL," +
                    CurveDbContract.CurveDbEntry.COLUMN_START_BEARING + " REAL," +
                    CurveDbContract.CurveDbEntry.COLUMN_END_BEARING + " REAL)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CurveDbContract.CurveDbEntry.TABLE_NAME;
}
