package com.android.app.weatherproject.fetchWeather;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.android.app.weatherproject.BuildConfig;
import com.android.app.weatherproject.data.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

final class GetWeatherData {


    static List<Weather> fetchWeatherDataOk(String lat, String lon, final String location) {

        String language = "el";
        String units = "si";
        String exclude = "flags,hourly";

        Response weatherResponse = null;

        URL weatherUrl = null;

        OkHttpClient client = new OkHttpClient();

        // Construct the URL for the query with specified users location
        final String BASE_WEATHER_URL = "https://api.forecast.io/forecast/";
        final String LANGUAGE = "lang";
        final String LATLON = lat + "," + lon;
        final String UNITS = "units";
        final String EXCLUDE = "exclude";

        Uri builtUri = Uri.parse(BASE_WEATHER_URL).buildUpon()
                .appendPath(BuildConfig.DARK_SKY_API_KEY)
                .appendPath(LATLON)
                .appendQueryParameter(LANGUAGE, language)
                .appendQueryParameter(UNITS, units)
                .appendQueryParameter(EXCLUDE, exclude)
                .build();

        try {
            weatherUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        final Request request = new Request.Builder()
                .url(weatherUrl)
                .build();

        try {
            weatherResponse = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return getDataFromJson(weatherResponse.body().string(), location);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Weather> getDataFromJson(String jsonForecast, String location) throws JSONException {

        // The names of the Json object that will be extracted from the response
        final String CURR_DAY = "currently";
        final String TIME = "time";
        final String SUMMARY = "summary";
        final String ICON = "icon";
        final String TEMPERATURE = "temperature";
        final String DAILY = "daily";
        final String DATA = "data";
        final String MIN_TEMP = "temperatureMin";
        final String MAX_TEMP = "temperatureMax";

        JSONObject weatherForecast = new JSONObject(jsonForecast);

        JSONObject currentWeather = weatherForecast.getJSONObject(CURR_DAY);
        long time;
        String todaySummary, todayIcon;

        time = currentWeather.getLong(TIME);
        time *= 1000L;

        todaySummary = currentWeather.getString(SUMMARY);
        todayIcon = currentWeather.getString(ICON);
        double todayTemperature = currentWeather.getDouble(TEMPERATURE);

        JSONObject dailyWeather = weatherForecast.getJSONObject(DAILY);
        JSONArray arrayDaily = dailyWeather.getJSONArray(DATA);

        List<Weather> forecastsObjects = new ArrayList<>();
        for (int i = 1; i < arrayDaily.length(); i++) {
            long timeDaily;
            String summaryDaily, iconDaily;
            double minTempDaily, maxTempDaily;

            JSONObject dayWeather = arrayDaily.getJSONObject(i);

            timeDaily = dayWeather.getLong(TIME);
            timeDaily *= 1000L;

            summaryDaily = dayWeather.getString(SUMMARY);
            iconDaily = dayWeather.getString(ICON);

            minTempDaily = dayWeather.getDouble(MIN_TEMP);
            maxTempDaily = dayWeather.getDouble(MAX_TEMP);

            Weather weatherObject = new Weather(timeDaily, summaryDaily, iconDaily, todayTemperature,
                    minTempDaily, maxTempDaily);
            forecastsObjects.add(weatherObject);
        }
        // Add the current weather object at first entry of ArrayList
        forecastsObjects.add(0, new Weather(time, todaySummary, todayIcon, todayTemperature, location));

        return forecastsObjects;
    }
}
