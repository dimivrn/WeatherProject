package com.android.app.weatherproject.data.network;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;


public class FetchWeatherService extends IntentService {

    public FetchWeatherService() {
        super(FetchWeatherService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String latitude = intent.getStringExtra("latitude");
        String longitude = intent.getStringExtra("longitude");

        WeatherNetworkDataSource networkDataSource = WeatherNetworkDataSource.getInstance(this.getApplicationContext());
        networkDataSource.getWeatherDataResponse(latitude, longitude);
    }
}
