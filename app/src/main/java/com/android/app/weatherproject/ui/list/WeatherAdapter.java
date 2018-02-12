package com.android.app.weatherproject.ui.list;

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

    private WeatherClickListener mOnWeatherClickListener;

    WeatherAdapter(WeatherClickListener weatherClickListener) {
        mOnWeatherClickListener = weatherClickListener;
    }

    @Override
    public WeatherHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ListItemForecastBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.list_item_forecast,
                        parent, false);

        binding.setClickCallback(mOnWeatherClickListener);

        return new WeatherHolder(binding);
    }

    @Override
    public void onBindViewHolder(WeatherHolder holder, int position) {

        holder.mBinding.setWeatherDay(mWeatherList.get(position));
        holder.mBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mWeatherList == null ? 0 : mWeatherList.size();
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

    public interface WeatherClickListener {
        void onClick(WeatherDay weatherDay);
    }
}
