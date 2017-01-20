package com.android.app.weatherproject;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class WeatherAdapter extends ArrayAdapter<Weather> {

    // Declare constants for the two different view types
    // Integer representation of the view types
    private final int VIEW_TODAY = 0;
    private final int VIEW_NEXT_DAY = 1;
    private Context ctx;

    WeatherAdapter(Activity context, ArrayList<Weather> weatherForecasts) {
        super(context, 0, weatherForecasts);
        ctx = context;
    }

    /**
     * Use the ViewHolder pattern in order for caching the views for days list items
     */
    public static class ViewHolder {
        public final ImageView listIcon;
        public final TextView dateTextView;
        public final TextView summaryTextView;
        public final TextView highTempTextView;
        public final TextView lowTempTextView;

        public final ImageView currentIcon;
        public final TextView currentDateTextView;
        public final TextView currentSummaryTextView;
        public final TextView currentTemperatureTextView;
        public final TextView currentLocationTextView;

        public ViewHolder(View view) {
            listIcon = (ImageView) view.findViewById(R.id.list_item_icon);
            dateTextView = (TextView) view.findViewById(R.id.list_date_textView);
            summaryTextView = (TextView) view.findViewById(R.id.list_summary_textView);
            highTempTextView = (TextView) view.findViewById(R.id.item_high_temp);
            lowTempTextView = (TextView) view.findViewById(R.id.item_low_temp);

            currentIcon = (ImageView) view.findViewById(R.id.current_summary_icon);
            currentDateTextView = (TextView) view.findViewById(R.id.current_date_textView);
            currentSummaryTextView = (TextView) view.findViewById(R.id.current_summary_textView);
            currentTemperatureTextView = (TextView) view.findViewById(R.id.current_temperature_textView);
            currentLocationTextView = (TextView) view.findViewById(R.id.location_textView);
        }
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;

        // Two different layout types
        int viewType = getItemViewType(position);
        int viewId = -1;

        if (listItemView == null) {
            if (viewType == VIEW_TODAY) {
                viewId = R.layout.list_current_item;
            } else if (viewType == VIEW_NEXT_DAY) {
                viewId = R.layout.list_item_forecast;
            }

            listItemView = LayoutInflater.from(getContext()).inflate(viewId, parent, false);
            ViewHolder listItemHolder = new ViewHolder(listItemView);
            listItemView.setTag(listItemHolder);
        }

        Weather weatherForecast = getItem(position);

        ViewHolder viewHolder = (ViewHolder) listItemView.getTag();

        if (viewType == VIEW_TODAY) {

            String currentIcon = weatherForecast.getCurrentIcon();

            LinearLayout linearLayoutBackground = (LinearLayout) listItemView.findViewById(R.id.linear_current_background);
            linearLayoutBackground.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimary));

            viewHolder.currentIcon.setImageResource(UtilsMethods.getCurrentIcon(currentIcon));

            String currentText = weatherForecast.getCurrentSummary();
            viewHolder.currentSummaryTextView.setText(currentText);

            long currentDate = weatherForecast.getCurrentDate();
            viewHolder.currentDateTextView.setText(UtilsMethods.getDate(currentDate));

            double currentTemp = weatherForecast.getCurrentTemperature();
            viewHolder.currentTemperatureTextView.setText(String.valueOf(UtilsMethods.formatTemperature(getContext(), currentTemp)));
        } else {

            String weatherIcon = weatherForecast.getIcon();
            // Set the image
            viewHolder.listIcon.setImageResource(UtilsMethods.getListIcon(weatherIcon));

            // Read the date for the Weather object
            long nextDate = weatherForecast.getDate();
            viewHolder.dateTextView.setText(UtilsMethods.getDate(nextDate));

            // Get the summary of the day's weather
            String nextSummary = weatherForecast.getSummary();
            viewHolder.summaryTextView.setText(nextSummary);

            // Get the minimum temperature for the day
            double minTemp = weatherForecast.getMintemperature();
            viewHolder.lowTempTextView.setText(String.valueOf(UtilsMethods.formatTemperature(getContext(), minTemp)));

            // Get the maximum temperature for the day
            double maxTemp = weatherForecast.getMaxTemperature();
            viewHolder.highTempTextView.setText(String.valueOf(UtilsMethods.formatTemperature(getContext(), maxTemp)));

        }
        return listItemView;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TODAY : VIEW_NEXT_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

}
