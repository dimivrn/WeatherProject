package com.android.app.weatherproject.utils;

import android.content.Context;

import com.android.app.weatherproject.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilsMethods {

    /**
     *
     * @param context From context get access to the String resource id for Celcious formatting
     * @param temperature The temperature returned from the Weather items
     * @return The formatted temperature
     */
    public static String formatTemperature(Context context, double temperature) {

        return context.getString(R.string.format_temperature, temperature);
    }

//    public static String getFriendlyDayString(Context context, long date) {
//
//        Date today = Calendar.getInstance().getTime();
//
//
//    }

    public static String getDate(long timeInMilliseconds) {

        Date dateObject = new Date(timeInMilliseconds);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMMM");
        return dateFormatter.format(dateObject);
    }

    public static int getListIcon(String icon) {

        switch (icon){
            case  "clear-day":
                return R.drawable.ic_clear;
            case "clear-night":
                return R.drawable.ic_clear;
            case "rain":
                return R.drawable.ic_light_rain;
            case "snow":
                return R.drawable.ic_snow;
            case "sleet":
                return R.drawable.ic_light_rain;
            case "wind":
                return R.drawable.ic_cloudy;
            case "fog":
                return R.drawable.ic_fog;
            case "cloudy":
                return R.drawable.ic_cloudy;
            case "partly-cloudy-day":
                return R.drawable.ic_light_clouds;
            case "partly-cloudy-night":
                return R.drawable.ic_light_clouds;
            default:

                break;
        }
        return 0;
    }

    public static int getCurrentIcon(String icon) {

        switch (icon){
            case  "clear-day":
                return R.drawable.art_clear;
            case "clear-night":
                return R.drawable.ic_clear;
            case "rain":
                return R.drawable.art_light_rain;
            case "snow":
                return R.drawable.art_snow;
            case "sleet":
                return R.drawable.art_light_rain;
            case "wind":
                return R.drawable.art_clouds;
            case "fog":
                return R.drawable.art_fog;
            case "cloudy":
                return R.drawable.art_clouds;
            case "partly-cloudy-day":
                return R.drawable.art_light_clouds;
            case "partly-cloudy-night":
                return R.drawable.art_light_clouds;
            default:

                break;
        }
        return 0;
    }
}
