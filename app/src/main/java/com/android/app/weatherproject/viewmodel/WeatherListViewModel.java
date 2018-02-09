package com.android.app.weatherproject.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.android.app.weatherproject.data.WeatherRepository;
import com.android.app.weatherproject.data.WeatherResponse;

public class WeatherListViewModel extends ViewModel {

    private WeatherRepository mWeatherRepository;

    private final LiveData<WeatherResponse> mWeatherResponse;

    public WeatherListViewModel() {

        mWeatherRepository = WeatherRepository.getInstance();

        mWeatherResponse = mWeatherRepository.getWeatherDataResponse();
    }

    public LiveData<WeatherResponse> getObservableWeatherResponse() {
        return mWeatherResponse;
    }
}
