package com.android.app.weatherproject.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.android.app.weatherproject.data.model.WeatherDay;

import java.util.Date;
import java.util.List;

@Dao
public interface WeatherDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(List<WeatherDay> weatherList);

    @Query("SELECT * FROM weather_daily WHERE time= :date")
    LiveData<WeatherDay> getWeatherByDate(Date date);

    @Query("SELECT * FROM weather_daily")
    LiveData<List<WeatherDay>> getWeatherData();
}
