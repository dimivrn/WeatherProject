package com.android.app.weatherproject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.List;

public class WeatherLoader extends AsyncTaskLoader<List<Weather>> {

    private String mlat, mlon, location;

    private static final String LOG_TAG = WeatherLoader.class.getSimpleName();

    WeatherLoader(Context context, Bundle coordinates) {
        super(context);
        if (coordinates != null) {
            mlat = coordinates.getString("Latitude");
            mlon = coordinates.getString("Longitude");
            location = coordinates.getString("Location");
            Log.v(LOG_TAG, "LOCATION " + location);
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Weather> loadInBackground() {
        if (mlat == null && mlon == null) {
            return null;
        }

        List<Weather> weatherList = GetWeatherData.fetchWeatherData(mlat, mlon, location);

        return weatherList;
    }
}
