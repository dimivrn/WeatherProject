package com.android.app.weatherproject.ui.list;

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
import com.android.app.weatherproject.data.WeatherDay;
import com.android.app.weatherproject.utils.UtilsMethods;

import java.util.List;


public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherHolder> {

    private static final int ITEM_TYPE_HEADER = 0;
    private static final int ITEM_TYPE_NORMAL = 1;

    private List<WeatherDay> mWeatherList;
    private Context mContext;

    public WeatherAdapter(Context context, List<WeatherDay> weatherList) {
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

        WeatherDay weatherData = mWeatherList.get(position);

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

    public void updateWeatherData(List<WeatherDay> updatedWeatherData) {
        mWeatherList = updatedWeatherData;
        notifyDataSetChanged();
    }

    public void clearWeatherData() {
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

        private void bindHeaderData(WeatherDay weatherForecast) {
            String currentIconString = weatherForecast.getIcon();

//            String currentLocation = weatherForecast.getLocation();
//            currentLocationTextView.setText(currentLocation);

            LinearLayout linearLayoutBackground = itemView.findViewById(R.id.linear_current_background);
            linearLayoutBackground.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

            currentIcon.setImageResource(UtilsMethods.getCurrentIcon(currentIconString));

            String currentText = weatherForecast.getSummary();
            currentSummaryTextView.setText(currentText);

            long currentDate = weatherForecast.getTime();
            currentDateTextView.setText(UtilsMethods.getDate(currentDate * 1000));

//            double currentTemp = weatherForecast.getT();
//            currentTemperatureTextView.setText(String.valueOf(UtilsMethods.formatTemperature(mContext, currentTemp)));
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

        private void bindNormalData(WeatherDay weatherForecast) {
            String weatherIcon = weatherForecast.getIcon();
            // Set the image
            listIcon.setImageResource(UtilsMethods.getListIcon(weatherIcon));

            // Read the date for the Weather object
            long nextDate = weatherForecast.getTime();
            dateTextView.setText(UtilsMethods.getDate(nextDate * 1000));

            // Get the summary of the day's weather
            String nextSummary = weatherForecast.getSummary();
            summaryTextView.setText(nextSummary);

            // Get the minimum temperature for the day
            double minTemp = weatherForecast.getTemperatureMin();
            lowTempTextView.setText(String.valueOf(UtilsMethods.formatTemperature(mContext, minTemp)));

            // Get the maximum temperature for the day
            double maxTemp = weatherForecast.getTemperatureMax();
            highTempTextView.setText(String.valueOf(UtilsMethods.formatTemperature(mContext, maxTemp)));
        }
    }
}
