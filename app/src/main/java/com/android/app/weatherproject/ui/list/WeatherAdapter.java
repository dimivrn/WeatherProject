package com.android.app.weatherproject.ui.list;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.android.app.weatherproject.R;
import com.android.app.weatherproject.data.WeatherDay;
import com.android.app.weatherproject.databinding.ListItemForecastBinding;

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

        ListItemForecastBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.list_item_forecast,
                        parent, false);

        return new WeatherHolder(binding);

    }

    @Override
    public void onBindViewHolder(WeatherHolder holder, int position) {

        holder.mBinding.setWeatherDay(mWeatherList.get(position));
        holder.mBinding.executePendingBindings();
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

    class WeatherHolder extends RecyclerView.ViewHolder {

        ListItemForecastBinding mBinding;

        WeatherHolder(ListItemForecastBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

//        private void bindNormalData(WeatherDay weatherForecast) {
//            String weatherIcon = weatherForecast.getIcon();
//
//            listIcon.setImageResource(UtilsMethods.getListIcon(weatherIcon));
//            long nextDate = weatherForecast.getTime();
//            dateTextView.setText(UtilsMethods.getDate(nextDate * 1000));
//            String nextSummary = weatherForecast.getSummary();
//            summaryTextView.setText(nextSummary);
//            double minTemp = weatherForecast.getTemperatureMin();
//            lowTempTextView.setText(String.valueOf(UtilsMethods.formatTemperature(mContext, minTemp)));
//            double maxTemp = weatherForecast.getTemperatureMax();
//            highTempTextView.setText(String.valueOf(UtilsMethods.formatTemperature(mContext, maxTemp)));
//        }
}
