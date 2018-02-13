package com.android.app.weatherproject.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.android.app.weatherproject.data.model.WeatherDay;

import java.util.Date;

@Dao
public interface WeatherDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(WeatherDay... Weather);

    @Query("SELECT * FROM weather_daily WHERE date= :date")
    LiveData<WeatherDay> getWeatherByDate(Date date);
}
