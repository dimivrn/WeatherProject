package com.android.app.weatherproject;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

final class GetWeatherData {

    // Tag for logging reasons
    private static final String LOG_TAG = GetWeatherData.class.getSimpleName();

    private ArrayAdapter<Weather> mWeatherAdapter;
    private final Context mContext;

    public GetWeatherData(Context context, ArrayAdapter<Weather> weatherAdapter) {
        mContext = context;
        mWeatherAdapter = weatherAdapter;
    }

    public static List<Weather> fetchWeatherData(String lat, String lon) {

        // If there's no coordinates, there's nothing to do
        if (lat == null && lon == null) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the response from the server
        String weatherJsonString = null;

        String language = "el";
        String units = "si";
        String exclude = "flags,hourly";

        try {

            // Set up the url
            // URL url = new URL("https://api.forecast.io/forecast/61ad0962a6f490073559a7b83f40ed2c/39.6383,22.4159?lang=el&units=si&exclude=flags,hourly");

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

            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG, "The constructed URL is " + url);

            // Create the request and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Get the response from the server as an input stream
            // Check if the request was successful and if it was read the input stream
            if (urlConnection.getResponseCode() == 200) {
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder builder = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                // Check if stream was empty and return
                if (builder.length() == 0) {
                    return null;
                }

                weatherJsonString = builder.toString();
                Log.v(LOG_TAG, "The weather JSON data is here " + weatherJsonString);
            }
        } catch (IOException e) {

            Log.e(LOG_TAG, "There was an error", e);

            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            return getDataFromJson(weatherJsonString);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        // If there was an error parsing the Json response return null
        return null;
    }

    private static List<Weather> getDataFromJson(String jsonForecast) throws JSONException {

        final int ARRAY_LENGTH = 8;

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
        final String LAT = "latitude";
        final String LON = "longitude";

        JSONObject weatherForecast = new JSONObject(jsonForecast);

        double locationLatitude = weatherForecast.getDouble(LAT);
        double locationLongitude = weatherForecast.getDouble(LON);

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

        List<Weather> forecastsObjects= new ArrayList<Weather>();
//        forecastsObjects.add(0, new Weather(time, todaySummary, todayIcon, todayTemperature));
        String[] results = new String[ARRAY_LENGTH];
        for (int i = 1; i <arrayDaily.length(); i ++) {
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

            for (Weather obj : forecastsObjects) {
                Log.v(LOG_TAG, "Forecast Entry: " + obj);
                Log.v(LOG_TAG, "The size of results is " + results.length);
            }
        }
        forecastsObjects.add(0 , new Weather(time, todaySummary, todayIcon, todayTemperature));

        return forecastsObjects;
    }
}
