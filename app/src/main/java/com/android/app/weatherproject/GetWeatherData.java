package com.android.app.weatherproject;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetWeatherData extends AsyncTask<String, Void, Void> {

    // Tag for logging reasons
    private static final String LOG_TAG = GetWeatherData.class.getSimpleName();

    @Override
    protected Void doInBackground(String... objects) {

        // If there's no coordinates, there's nothing to do
        if (objects.length == 0) {
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
            final String LATLON = objects[0] + "," + objects[1];
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
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                // Check if stream was empty and return
                if (buffer.length() == 0) {
                    return null;
                }

                weatherJsonString = buffer.toString();
                Log.i(LOG_TAG, "The weather JSON data is here " + weatherJsonString);
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
        return null;
    }
}
