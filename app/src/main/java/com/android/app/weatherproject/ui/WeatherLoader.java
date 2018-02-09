package com.android.app.weatherproject.ui;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import org.json.JSONException;

import java.io.IOException;

public class WeatherLoader extends AsyncTaskLoader<ContentValues[]> {

    private String mlat, mlon, location;

    public WeatherLoader(Context context, Bundle coordinates) {
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
    public ContentValues[] loadInBackground() {
        if (mlat == null && mlon == null) {
            return null;
        }

        try {
            return GetWeatherData.fetchWeatherDataOk(mlat, mlon, location);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
