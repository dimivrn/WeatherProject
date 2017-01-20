package com.android.app.weatherproject;

/**
 *  An {@link Weather} object contains all the related information for a single forecast
 */

class Weather {


    private String mCurrentSummary, mCurrentIcon, mLocation;
    private double mCurrentTemperature;
    private long mCurrentDate;

    private String mSummary, mIcon;
    private double  mMinTemperature, mMaxTemperature;
    private long mDate;

     Weather(long currentDate, String currentSummary, String currentIcon, double currentTemp, String currentLocation) {
        mCurrentDate = currentDate;
        mCurrentSummary = currentSummary;
        mCurrentIcon = currentIcon;
        mCurrentTemperature = currentTemp;
        mLocation = currentLocation;
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


     String getCurrentSummary() {
        return mCurrentSummary;
    }

     String getCurrentIcon() {
        return mCurrentIcon;
    }

     double getCurrentTemperature() {
        return mCurrentTemperature;
    }

     long getCurrentDate() {
        return mCurrentDate;
    }


     String getSummary() {
        return mSummary;
    }

     String getIcon() {
        return mIcon;
    }

     double getMintemperature() {
        return mMinTemperature;
    }

     double getMaxTemperature() {
        return mMaxTemperature;
    }

     long getDate() {
        return mDate;
    }

     String getLocation() {
        return mLocation;
    }
}
