package com.android.app.weatherproject.ui.list;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
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

    private List<WeatherDay> mWeatherList;
    private Context mContext;

    WeatherAdapter(Context context, List<WeatherDay> weatherList) {
        mContext = context;
        mWeatherList = weatherList;
    }

    @Override
    public WeatherHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new WeatherNormalHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_forecast, parent, false));

    }

    @Override
    public void onBindViewHolder(WeatherHolder holder, int position) {

        WeatherDay weatherData = mWeatherList.get(position);

        WeatherNormalHolder weatherNormalHolder = (WeatherNormalHolder) holder;
        weatherNormalHolder.bindNormalData(weatherData);

    }

    @Override
    public int getItemCount() {
        return mWeatherList.size();
    }


    void updateWeatherData(List<WeatherDay> updatedWeatherData) {
        if (mWeatherList == null) {
            mWeatherList = updatedWeatherData;
            notifyItemRangeInserted(0, updatedWeatherData.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mWeatherList.size();
                }

                @Override
                public int getNewListSize() {
                    return updatedWeatherData.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mWeatherList.get(oldItemPosition).getTime().equals(
                            updatedWeatherData.get(newItemPosition).getTime());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    WeatherDay weatherDay = updatedWeatherData.get(newItemPosition);
                    WeatherDay oldWeatherDay = updatedWeatherData.get(oldItemPosition);
                    return weatherDay.getTime().equals(oldWeatherDay.getTime()) &&
                            weatherDay.getSummary() == oldWeatherDay.getSummary();
                }
            });
            mWeatherList = updatedWeatherData;
            result.dispatchUpdatesTo(this);
        }
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

            listIcon.setImageResource(UtilsMethods.getListIcon(weatherIcon));
            long nextDate = weatherForecast.getTime();
            dateTextView.setText(UtilsMethods.getDate(nextDate * 1000));
            String nextSummary = weatherForecast.getSummary();
            summaryTextView.setText(nextSummary);
            double minTemp = weatherForecast.getTemperatureMin();
            lowTempTextView.setText(String.valueOf(UtilsMethods.formatTemperature(mContext, minTemp)));
            double maxTemp = weatherForecast.getTemperatureMax();
            highTempTextView.setText(String.valueOf(UtilsMethods.formatTemperature(mContext, maxTemp)));
        }
    }
}
