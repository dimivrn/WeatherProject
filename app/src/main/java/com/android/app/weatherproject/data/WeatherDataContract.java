package com.android.app.weatherproject.data;

import android.provider.BaseColumns;

/**
 * Created by dimitris on 29/1/18
 *
 * Weather database contract class contains all the constants related to the database
 */

public class WeatherDataContract {

    public static final class WeatherDataEntry implements BaseColumns {

        public static final String TABLE_NAME = "weather";

        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_SUMMARY = "summary";
        public static final String COLUMN_CURRENT_LOCATION = "current_location";
        public static final String COLUMN_CURRENT_TEMP = "current_temperature";
        public static final String COLUMN_MIN_TEMP = "min_temperature";
        public static final String COLUMN_MAX_TEMP = "max_temperature";
    }
}
