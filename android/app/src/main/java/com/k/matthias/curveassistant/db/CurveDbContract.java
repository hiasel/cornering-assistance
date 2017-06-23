package com.k.matthias.curveassistant.db;

import android.provider.BaseColumns;

/**
 * Created by matthias on 12.06.17.
 */

public final class CurveDbContract {

    private CurveDbContract() {

    }

    /* Inner class that defines the table contents */
    public static class CurveDbEntry implements BaseColumns {
        public static final String TABLE_NAME = "curve";
        public static final String COLUMN_CENTER_LAT = "center_lat";
        public static final String COLUMN_CENTER_LON = "center_lon";
        public static final String COLUMN_START_LAT = "start_lat";
        public static final String COLUMN_START_LON = "start_lon";
        public static final String COLUMN_END_LAT = "end_lat";
        public static final String COLUMN_END_LON = "end_lon";
        public static final String COLUMN_LENGTH = "length";
        public static final String COLUMN_RADIUS = "radius";
        public static final String COLUMN_START_BEARING = "start_bearing";
        public static final String COLUMN_END_BEARING = "end_bearing";
    }


}


