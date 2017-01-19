package com.android.app.weatherproject;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class WeatherAdapter extends ArrayAdapter<Weather> {

    WeatherAdapter(Activity context, ArrayList<Weather> weatherForecasts) {
        super(context, 0, weatherForecasts);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_forecast, parent, false);
        }

        Weather weatherForecast = getItem(position);

        TextView view = (TextView) listItemView.findViewById(R.id.forecast_textView);

        int forString = weatherForecast.getCurrentTemperature();
        view.setText(String.valueOf(forString));

        return listItemView;
    }
}
