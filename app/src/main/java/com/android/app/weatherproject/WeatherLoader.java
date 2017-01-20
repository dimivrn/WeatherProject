package com.android.app.weatherproject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

public class WeatherLoader extends AsyncTaskLoader<List<Weather>> {

    private String mlat, mlon;

    private static final String LOG_TAG = WeatherLoader.class.getSimpleName();

    WeatherLoader(Context context, Bundle coordinates) {
        super(context);
        if (coordinates != null) {
            mlat = coordinates.getString("Latitude");
            mlon = coordinates.getString("Longitude");
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

        List<Weather> weatherList = GetWeatherData.fetchWeatherData(mlat, mlon);

        return weatherList;
    }
}
