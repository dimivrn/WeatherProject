package com.android.app.weatherproject.ui.list;


import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.app.weatherproject.R;
import com.android.app.weatherproject.data.Currently;
import com.android.app.weatherproject.ui.FetchLocationIntentService;
import com.android.app.weatherproject.utils.UtilsMethodsBinding;
import com.android.app.weatherproject.viewmodel.WeatherListViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.ArrayList;

import static android.view.View.GONE;
import static com.android.app.weatherproject.ui.FetchLocationIntentService.Constants.LOCATION_DATA_EXTRA;
import static com.android.app.weatherproject.ui.FetchLocationIntentService.Constants.RECEIVER;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class WeatherFragment extends Fragment {

    private Location mLastLocation;

    private String mLocationString;
    public AddressResultReceiver mResultReceiver = new AddressResultReceiver(new Handler());
    private View mLayout;

    private TextView mCurrentDateTextView, mCurrentTempTextView, mCurrentSummaryTextView;
    private TextView mEmptyText;
    private WeatherAdapter mWeatherAdapter;
    private ImageView mCurrentImageWeather;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 199;
    final private int MY_REQUEST_ACCESS_FINE_LOCATION = 100;
    private FusedLocationProviderClient mFusedLocationClient;

    private WeatherListViewModel mWeatherListViewmodel;

    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_weather, container, false);

        RecyclerView weatherList = fragmentView.findViewById(R.id.listView_weather);
        mEmptyText = fragmentView.findViewById(R.id.empty_view);
        weatherList.setLayoutManager(new LinearLayoutManager(getActivity()));

        mWeatherAdapter = new WeatherAdapter(getActivity(), new ArrayList<>());

        weatherList.setAdapter(mWeatherAdapter);

        ConnectivityManager connManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            checkLocationPermission();
        } else {
            // There is no Internet connection so
            View loadingIndicator = fragmentView.findViewById(R.id.progress_bar);
            loadingIndicator.setVisibility(GONE);
            // Show the no internet connection
            mEmptyText.setText(R.string.no_internet_connection_string);
        }
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCurrentImageWeather = view.findViewById(R.id.current_weather_image);
        mCurrentDateTextView = view.findViewById(R.id.current_date_text_view);
        mCurrentTempTextView = view.findViewById(R.id.current_date_temp_text_view);
        mCurrentSummaryTextView = view.findViewById(R.id.current_weather_summary_text_view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFusedLocationClient = getFusedLocationProviderClient(getActivity());

        mWeatherListViewmodel = ViewModelProviders.of(this).get(WeatherListViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        ConnectivityManager connManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            getLastLocation();
        } else {
            // There is no Internet connection so
            View loadingIndicator = getActivity().findViewById(R.id.progress_bar);
            loadingIndicator.setVisibility(GONE);
            // Show the no internet connection
            mEmptyText.setText(R.string.no_internet_connection_string);
        }
    }

    private void getLastLocation() {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), location -> {
                        if (location != null) {
                            mLastLocation = location;
                            //startIntentService();
                            observeWeatherResponse(mWeatherListViewmodel);
                        }

                    });
        }
    }

    private void observeWeatherResponse(WeatherListViewModel weatherListViewModel) {
        weatherListViewModel.getObservableWeatherResponse(String.valueOf(mLastLocation.getLatitude()),
                String.valueOf(mLastLocation.getLongitude())).observe(this, weatherResponseList -> {
            if (weatherResponseList != null) {
                View loadingIndicator = getActivity().findViewById(R.id.progress_bar);
                loadingIndicator.setVisibility(View.GONE);

                setCurrentWeatherData(weatherResponseList.getCurrently());

                mWeatherAdapter.updateWeatherData(weatherResponseList.getDaily().getData());
            }
        });
    }

    private void setCurrentWeatherData(Currently currentWeather) {

        //mCurrentImageWeather.setImageResource(UtilsMethodsBinding.setCurrentWeatherIcon(currentWeather.getIcon()));
        //mCurrentDateTextView.setText(UtilsMethodsBinding.setWeatherTime(currentWeather.getTime() * 1000));
        mCurrentTempTextView.setText(String.valueOf(UtilsMethodsBinding.formatTemperature(getActivity(), currentWeather.getTemperature())));
        mCurrentSummaryTextView.setText(currentWeather.getSummary());
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
                    Snackbar.make(mLayout, R.string.location_permission_not_granted,
                            Snackbar.LENGTH_SHORT).show();
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

            // Show a toast message if an address was found.
            if (resultCode == FetchLocationIntentService.Constants.SUCCESS_RESULT) {
                mLocationString = resultData.getString(FetchLocationIntentService.Constants.RESULT_DATA_KEY);
            }
        }
    }
}
