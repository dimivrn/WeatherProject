package com.android.app.weatherproject.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.android.app.weatherproject.data.model.WeatherDay;

@Database(entities = {WeatherDay.class}, version = 3)
@TypeConverters(DateConverter.class)
public abstract class WeatherDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "weather";

    private static final Object LOCK = new Object();
    private static volatile WeatherDatabase mDatabaseInstance;

    public abstract WeatherDAO weatherDAO();

    public static WeatherDatabase getInstance(Context context) {
        if (mDatabaseInstance == null) {
            synchronized (LOCK) {
                mDatabaseInstance = Room.databaseBuilder(context.getApplicationContext(),
                        WeatherDatabase.class, WeatherDatabase.DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return mDatabaseInstance;
    }
}
