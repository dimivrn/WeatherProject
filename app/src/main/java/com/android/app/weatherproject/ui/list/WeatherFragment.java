package com.android.app.weatherproject.ui.list;


import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.app.weatherproject.R;
import com.android.app.weatherproject.data.Currently;
import com.android.app.weatherproject.data.WeatherDay;
import com.android.app.weatherproject.databinding.FragmentWeatherBinding;
import com.android.app.weatherproject.ui.FetchLocationIntentService;
import com.android.app.weatherproject.utils.UtilsMethodsBinding;
import com.android.app.weatherproject.viewmodel.WeatherListViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.resolveSize;
import static com.android.app.weatherproject.ui.FetchLocationIntentService.Constants.LOCATION_DATA_EXTRA;
import static com.android.app.weatherproject.ui.FetchLocationIntentService.Constants.RECEIVER;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class WeatherFragment extends Fragment {

    private static final String LOG_TAG = WeatherFragment.class.getSimpleName();
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 199;
    final private int MY_REQUEST_ACCESS_FINE_LOCATION = 100;

    private Location mLastLocation;
    public AddressResultReceiver mResultReceiver = new AddressResultReceiver(new Handler());

    private WeatherAdapter mWeatherAdapter;

    private FusedLocationProviderClient mFusedLocationClient;

    private WeatherListViewModel mWeatherListViewModel;

    private FragmentWeatherBinding mBinding;

    public WeatherFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_weather, container, false);

        mBinding.listViewWeather.setLayoutManager(new LinearLayoutManager(getActivity()));
        mWeatherAdapter = new WeatherAdapter(weatherClickListener);
        mBinding.listViewWeather.setAdapter(mWeatherAdapter);

        if (checkNetWorkConnection()) {
            checkLocationPermission();
        } else {
            mBinding.progressBar.setVisibility(GONE);
            mBinding.emptyView.setText(R.string.no_internet_connection_string);
        }
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //noinspection ConstantConditions
        mFusedLocationClient = getFusedLocationProviderClient(getActivity());

        mWeatherListViewModel = ViewModelProviders.of(this).get(WeatherListViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkNetWorkConnection()) {
            getLastLocation();
        } else {
            mBinding.progressBar.setVisibility(GONE);
            mBinding.emptyView.setText(R.string.no_internet_connection_string);
        }
    }

    private final WeatherAdapter.WeatherClickListener weatherClickListener = weatherDay ->
            Log.i(LOG_TAG, "CLICK LISTENER ENABLED");

    /**
     * Check if network connection is available
     *
     * @return true or false depending the situation
     */
    @SuppressWarnings("ConstantConditions")
    private boolean checkNetWorkConnection() {
        ConnectivityManager connManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    private void getLastLocation() {
        @SuppressWarnings("ConstantConditions")
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), location -> {
                        if (location != null) {
                            mLastLocation = location;
                            //startIntentService();
                            observeWeatherResponse(mWeatherListViewModel);
                        }

                    });
        }
    }

    private void observeWeatherResponse(WeatherListViewModel weatherListViewModel) {
        weatherListViewModel.getObservableWeatherResponse(String.valueOf(mLastLocation.getLatitude()),
                String.valueOf(mLastLocation.getLongitude())).observe(this, weatherResponseList -> {
            if (weatherResponseList != null) {
                mBinding.progressBar.setVisibility(GONE);
                setCurrentWeatherData(weatherResponseList.getCurrently());
                mWeatherAdapter.updateWeatherData(weatherResponseList.getDaily().getData());
            }
        });
    }

    private void setCurrentWeatherData(Currently currentWeather) {
        UtilsMethodsBinding.setCurrentWeatherIcon(
                mBinding.currentDayLayout.currentWeatherImage, currentWeather.getIcon());
        UtilsMethodsBinding.setWeatherTime(
                mBinding.currentDayLayout.currentDateTextView, currentWeather.getTime());

        mBinding.currentDayLayout.currentDateTempTextView.setText(
                UtilsMethodsBinding.formatTemperature(getActivity(), currentWeather.getTemperature()));
        mBinding.currentDayLayout.currentWeatherSummaryTextView.setText(currentWeather.getSummary());
    }

    protected void startIntentService() {
        Intent intent = new Intent(getActivity(), FetchLocationIntentService.class);
        intent.putExtra(RECEIVER, mResultReceiver);
        intent.putExtra(LOCATION_DATA_EXTRA, mLastLocation);
        getActivity().startService(intent);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // The location permission was granted by the user
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {
                        getLastLocation();
                    }
                } else {
                    Log.i(LOG_TAG, getString(R.string.location_permission_not_granted));
                }
            }
        }
    }

    public class AddressResultReceiver extends ResultReceiver {

        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String mLocationString;
            // Show a toast message if an address was found.
            if (resultCode == FetchLocationIntentService.Constants.SUCCESS_RESULT) {
                mLocationString = resultData.getString(FetchLocationIntentService.Constants.RESULT_DATA_KEY);
            }
        }
    }
}
