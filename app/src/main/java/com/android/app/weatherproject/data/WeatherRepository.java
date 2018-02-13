package com.android.app.weatherproject.data;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.android.app.weatherproject.AppExecutors;
import com.android.app.weatherproject.data.database.WeatherDAO;
import com.android.app.weatherproject.data.database.WeatherDatabase;
import com.android.app.weatherproject.data.model.WeatherResponse;
import com.android.app.weatherproject.data.network.WeatherNetworkDataSource;

public class WeatherRepository {

    private static WeatherRepository mWeatherRepository;
    private static final Object LOCK = new Object();
    private WeatherNetworkDataSource mWeatherNetworkDataSource;
    private AppExecutors mAppExecutors;
    private WeatherDAO mWeatherDao;

    private boolean mInitialized = false;

    private WeatherRepository(Context context) {

        mWeatherNetworkDataSource = WeatherNetworkDataSource.getInstance(context);
        mAppExecutors = AppExecutors.getInstance();
        mWeatherDao = WeatherDatabase.getInstance(context).weatherDAO();

        LiveData<WeatherResponse> weatherData = mWeatherNetworkDataSource.getFetchedWeatherForecasts();
        weatherData.observeForever(weatherResponse -> mAppExecutors.diskIO().execute(() ->
                mWeatherDao.bulkInsert(weatherResponse.getDaily().getData())));
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
