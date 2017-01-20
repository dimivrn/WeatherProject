package com.android.app.weatherproject;

/**
 *  An {@link Weather} object contains all the related information for a single forecast
 */

public class Weather {


    private String mCurrentSummary, mCurrentIcon;
    private double mCurrentTemperature;
    private long mCurrentDate;

    private String mSummary, mIcon;
    private double  mMinTemperature, mMaxTemperature;
    private long mDate;

     Weather(long currentDate, String currentSummary, String currentIcon, double currentTemp) {
        mCurrentDate = currentDate;
        mCurrentSummary = currentSummary;
        mCurrentIcon = currentIcon;
        mCurrentTemperature = currentTemp;
    }

     Weather(long date, String summary, String icon, double currentTemp,
             double minTemp, double maxTemp) {

        mDate = date;
        mSummary = summary;
        mIcon = icon;
        mCurrentTemperature = currentTemp;
        mMinTemperature = minTemp;
        mMaxTemperature = maxTemp;
    }


    public String getCurrentSummary() {
        return mCurrentSummary;
    }

    public String getCurrentIcon() {
        return mCurrentIcon;
    }

    public double getCurrentTemperature() {
        return mCurrentTemperature;
    }

    public long getCurrentDate() {
        return mCurrentDate;
    }


    public String getSummary() {
        return mSummary;
    }

    public String getIcon() {
        return mIcon;
    }

    public double getMintemperature() {
        return mMinTemperature;
    }

    public double getMaxTemperature() {
        return mMaxTemperature;
    }

    public long getDate() {
        return mDate;
    }

}
