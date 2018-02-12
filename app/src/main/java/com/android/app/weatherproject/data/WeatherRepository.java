package com.android.app.weatherproject.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.android.app.weatherproject.BuildConfig;
import com.android.app.weatherproject.data.network.WeatherNetworkDataSource;
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
    private WeatherNetworkDataSource mWeatherNetworkDataSource;

    private WeatherService mWeatherService;

    private boolean mInitialized = false;

    private WeatherRepository(Context context) {

        mWeatherNetworkDataSource = WeatherNetworkDataSource.getInstance(context);

    }

    public synchronized static WeatherRepository getInstance(Context context) {

        if (mWeatherRepository == null) {
            synchronized (LOCK) {
                mWeatherRepository = new WeatherRepository(context);
            }
        }
        return mWeatherRepository;
    }

    public LiveData<WeatherResponse> getWeatherDataResponse(String latitude, String longitude) {
        initializeData(latitude, longitude);

        return mWeatherNetworkDataSource.getFetchedWeatherForecasts();
    }

    private synchronized void initializeData(String latitude, String longitude) {

        // Initialization once per app lifetime
        if (mInitialized) return;
        mInitialized = true;

        startFetchWeatherService(latitude, longitude);
    }

    private void startFetchWeatherService(String latitude, String longitude) {
        mWeatherNetworkDataSource.startFetchWeatherService(latitude, longitude);
    }
}
