package com.android.app.weatherproject.fetchWeather;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.app.weatherproject.R;
import com.android.app.weatherproject.data.Weather;
import com.android.app.weatherproject.utils.UtilsMethods;

import java.util.List;


public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherHolder> {

    private static final int ITEM_TYPE_HEADER = 0;
    private static final int ITEM_TYPE_NORMAL = 1;

    private List<Weather> mWeatherList;
    private Context mContext;

    WeatherAdapter(Context context, List<Weather> weatherList) {
        mContext = context;
        mWeatherList = weatherList;
    }

    @Override
    public WeatherHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case ITEM_TYPE_HEADER:
                return new WeatherHeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_current_item, parent, false));
            case ITEM_TYPE_NORMAL:
                return new WeatherNormalHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_forecast, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(WeatherHolder holder, int position) {

        Weather weatherData = mWeatherList.get(position);

        switch (holder.getItemViewType()) {
            case ITEM_TYPE_HEADER:
                WeatherHeaderHolder weatherHeaderHolder = (WeatherHeaderHolder) holder;
                weatherHeaderHolder.bindHeaderData(weatherData);
                break;
            case ITEM_TYPE_NORMAL:
                WeatherNormalHolder weatherNormalHolder = (WeatherNormalHolder) holder;
                weatherNormalHolder.bindNormalData(weatherData);
        }
    }

    @Override
    public int getItemCount() {
        return mWeatherList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE_HEADER;
        } else {
            return ITEM_TYPE_NORMAL;
        }
    }

    void updateWeatherData(List<Weather> updatedWeatherData) {
        mWeatherList = updatedWeatherData;
        notifyDataSetChanged();
    }

    void clearWeatherData() {
        mWeatherList.clear();
        notifyDataSetChanged();
    }

    class WeatherHolder extends RecyclerView.ViewHolder {

        WeatherHolder(View itemView) {
            super(itemView);
        }
    }

    class WeatherHeaderHolder extends WeatherHolder {

        private TextView currentDateTextView, currentSummaryTextView, currentTemperatureTextView;
        private TextView currentLocationTextView;
        private ImageView currentIcon;

        WeatherHeaderHolder(View itemView) {
            super(itemView);

            currentIcon = itemView.findViewById(R.id.current_summary_icon);
            currentDateTextView = itemView.findViewById(R.id.current_date_textView);
            currentSummaryTextView = itemView.findViewById(R.id.current_summary_textView);
            currentTemperatureTextView = itemView.findViewById(R.id.current_temperature_textView);
            currentLocationTextView = itemView.findViewById(R.id.location_textView);
        }

        private void bindHeaderData(Weather weatherForecast) {
            String currentIconString = weatherForecast.getCurrentIcon();

            String currentLocation = weatherForecast.getLocation();
            currentLocationTextView.setText(currentLocation);

            LinearLayout linearLayoutBackground = itemView.findViewById(R.id.linear_current_background);
            linearLayoutBackground.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

            currentIcon.setImageResource(UtilsMethods.getCurrentIcon(currentIconString));

            String currentText = weatherForecast.getCurrentSummary();
            currentSummaryTextView.setText(currentText);

            long currentDate = weatherForecast.getCurrentDate();
            currentDateTextView.setText(UtilsMethods.getDate(currentDate));

            double currentTemp = weatherForecast.getCurrentTemperature();
            currentTemperatureTextView.setText(String.valueOf(UtilsMethods.formatTemperature(mContext, currentTemp)));
        }
    }

    class WeatherNormalHolder extends WeatherHolder {

        private ImageView listIcon;
        private TextView dateTextView, summaryTextView, highTempTextView, lowTempTextView;

        WeatherNormalHolder(View itemView) {
            super(itemView);

            listIcon = itemView.findViewById(R.id.list_item_icon);
            dateTextView = itemView.findViewById(R.id.list_date_textView);
            summaryTextView = itemView.findViewById(R.id.list_summary_textView);
            highTempTextView = itemView.findViewById(R.id.item_high_temp);
            lowTempTextView = itemView.findViewById(R.id.item_low_temp);
        }

        private void bindNormalData(Weather weatherForecast) {
            String weatherIcon = weatherForecast.getIcon();
            // Set the image
            listIcon.setImageResource(UtilsMethods.getListIcon(weatherIcon));

            // Read the date for the Weather object
            long nextDate = weatherForecast.getDate();
            dateTextView.setText(UtilsMethods.getDate(nextDate));

            // Get the summary of the day's weather
            String nextSummary = weatherForecast.getSummary();
            summaryTextView.setText(nextSummary);

            // Get the minimum temperature for the day
            double minTemp = weatherForecast.getMintemperature();
            lowTempTextView.setText(String.valueOf(UtilsMethods.formatTemperature(mContext, minTemp)));

            // Get the maximum temperature for the day
            double maxTemp = weatherForecast.getMaxTemperature();
            highTempTextView.setText(String.valueOf(UtilsMethods.formatTemperature(mContext, maxTemp)));
        }
    }
}
