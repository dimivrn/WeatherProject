package com.android.app.weatherproject.fetchWeather;

import android.net.Uri;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

final class GetWeatherData {

    static List<Weather> fetchWeatherData(String lat, String lon, String location) {

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
            }
        } catch (IOException e) {
            e.printStackTrace();
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
            return getDataFromJson(weatherJsonString, location);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // If there was an error parsing the Json response return null
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
