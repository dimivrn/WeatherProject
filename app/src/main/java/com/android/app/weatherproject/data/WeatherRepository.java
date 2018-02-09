package com.android.app.weatherproject.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.android.app.weatherproject.BuildConfig;
import com.android.app.weatherproject.data.network.WeatherService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.android.app.weatherproject.data.network.WeatherService.BASE_WEATHER_URL;

public class WeatherRepository {

    private static WeatherRepository mWeatherRepository;
    private static final Object LOCK = new Object();

    private WeatherService mWeatherService;

    private WeatherRepository() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_WEATHER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mWeatherService = retrofit.create(WeatherService.class);
    }

    public synchronized static WeatherRepository getInstance() {

        if (mWeatherRepository == null) {
            synchronized (LOCK) {
                mWeatherRepository = new WeatherRepository();
            }
        }
        return mWeatherRepository;
    }

    public LiveData<WeatherResponse> getWeatherDataResponse(String latitude, String longitude) {

        final MutableLiveData<WeatherResponse> weatherResponseLive = new MutableLiveData<>();

        mWeatherService.getWeatherData(BuildConfig.DARK_SKY_API_KEY, latitude, longitude).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                weatherResponseLive.setValue(response.body());
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                weatherResponseLive.setValue(null);
            }
        });
        return weatherResponseLive;
    }

}
