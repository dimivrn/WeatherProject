package com.android.app.weatherproject;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetWeatherData extends AsyncTask {

    // Tag for logging reasons
    private static final String LOG_TAG = GetWeatherData.class.getSimpleName();

    @Override
    protected Object doInBackground(Object[] objects) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the response from the server
        String weatherJsonString = null;

        try {

            // Set up the url
            URL url = new URL("https://api.forecast.io/forecast/61ad0962a6f490073559a7b83f40ed2c/39.6383,22.4159?lang=el&units=si&exclude=flags,hourly");

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
