package com.android.app.weatherproject.fetchWeather;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.android.app.weatherproject.data.Weather;
import com.android.app.weatherproject.fetchWeather.GetWeatherData;

import java.util.List;

public class WeatherLoader extends AsyncTaskLoader<List<Weather>> {

    private String mlat, mlon, location;

    WeatherLoader(Context context, Bundle coordinates) {
        super(context);
        if (coordinates != null) {
            mlat = coordinates.getString("Latitude");
            mlon = coordinates.getString("Longitude");
            location = coordinates.getString("Location");
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

        return GetWeatherData.fetchWeatherData(mlat, mlon, location);
    }
}
