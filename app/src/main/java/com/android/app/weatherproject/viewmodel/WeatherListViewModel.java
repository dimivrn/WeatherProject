package com.android.app.weatherproject.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.android.app.weatherproject.data.WeatherRepository;
import com.android.app.weatherproject.data.model.Currently;
import com.android.app.weatherproject.data.model.WeatherDay;
import com.android.app.weatherproject.data.model.WeatherResponse;

import java.util.List;

public class WeatherListViewModel extends AndroidViewModel {

    private WeatherRepository mWeatherRepository;

    public WeatherListViewModel(Application application) {
        super(application);
        mWeatherRepository = WeatherRepository.getInstance(application);
    }

    public LiveData<WeatherResponse> getObservableWeatherResponse(String lat, String lon) {
        return mWeatherRepository.getWeatherDataResponse(lat, lon);
    }
}
