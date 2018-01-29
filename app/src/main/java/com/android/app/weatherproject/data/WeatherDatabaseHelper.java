package com.android.app.weatherproject.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dimitris on 30/1/2018.
 *
 * Database helper class responsible for creating the first time the database and
 * updating after
 */

public class WeatherDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "weather.db";

    private static final int DATABASE_VERSION = 1;

    public WeatherDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherDataContract.WeatherDataEntry.TABLE_NAME + " (" +

                WeatherDataContract.WeatherDataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                WeatherDataContract.WeatherDataEntry.COLUMN_CURRENT_LOCATION + " TEXT," +
                WeatherDataContract.WeatherDataEntry.COLUMN_DATE + " INTEGER NOT NULL," +

                WeatherDataContract.WeatherDataEntry.COLUMN_MIN_TEMP + " REAL NOT NULL," +
                WeatherDataContract.WeatherDataEntry.COLUMN_MAX_TEMP + " REAL NOT NULL," +

                WeatherDataContract.WeatherDataEntry.COLUMN_SUMMARY + " TEXT NOT NULL," +
                WeatherDataContract.WeatherDataEntry.COLUMN_CURRENT_TEMP + " REAL," +

                " UNIQUE (" + WeatherDataContract.WeatherDataEntry.COLUMN_DATE + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherDataContract.WeatherDataEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
