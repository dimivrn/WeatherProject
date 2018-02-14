package com.android.app.weatherproject.data.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;

import com.android.app.weatherproject.BuildConfig;
import com.android.app.weatherproject.data.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.android.app.weatherproject.data.network.WeatherService.BASE_WEATHER_URL;

public class WeatherNetworkDataSource {

    private static final Object LOCK = new Object();
    private static WeatherNetworkDataSource mInstance;
    private WeatherService mWeatherService;

    private Context mContext;

    private final MutableLiveData<WeatherResponse> mFetchedWeatherDays;

    private WeatherNetworkDataSource(Context context) {
        mContext= context;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_WEATHER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mWeatherService = retrofit.create(WeatherService.class);

        mFetchedWeatherDays = new MutableLiveData<>();
    }

    public static WeatherNetworkDataSource getInstance(Context context) {
        if (mInstance == null) {
            synchronized (LOCK) {
                mInstance = new WeatherNetworkDataSource(context);
            }
        }
        return mInstance;
    }

    public LiveData<WeatherResponse> getFetchedWeatherForecasts() {
        return mFetchedWeatherDays;
    }

    public void startFetchWeatherService(String latitude, String longitude) {
        Intent intent = new Intent(mContext, FetchWeatherService.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        mContext.startService(intent);
    }

    void getWeatherDataResponse(String latitude, String longitude) {

        mWeatherService.getWeatherData(BuildConfig.DARK_SKY_API_KEY, latitude, longitude).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                mFetchedWeatherDays.postValue(response.body());
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                mFetchedWeatherDays.postValue(null);
            }
        });
    }

}
