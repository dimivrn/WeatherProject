package com.android.app.weatherproject.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.android.app.weatherproject.data.WeatherRepository;
import com.android.app.weatherproject.data.WeatherResponse;

public class WeatherListViewModel extends ViewModel {

    private WeatherRepository mWeatherRepository;

    public WeatherListViewModel() {

        mWeatherRepository = WeatherRepository.getInstance();
    }

    public LiveData<WeatherResponse> getObservableWeatherResponse(String lat, String lon) {
        return mWeatherRepository.getWeatherDataResponse(lat, lon);
    }
}
