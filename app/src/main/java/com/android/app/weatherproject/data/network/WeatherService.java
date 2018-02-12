package com.android.app.weatherproject.data.network;

import com.android.app.weatherproject.data.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WeatherService {

    String BASE_WEATHER_URL = "https://api.forecast.io/forecast/";

    @GET("{api_key}/{lat},{lon}?units=si&exclude=flags,hourly,minutely")
    Call<WeatherResponse> getWeatherData(
            @Path("api_key") String DARK_SKY_API_KEY, @Path("lat") String latitude, @Path("lon") String longitude);

}
