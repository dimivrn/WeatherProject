package com.android.app.weatherproject.fetchWeather;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.app.weatherproject.R;
import com.android.app.weatherproject.data.Weather;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class WeatherFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Weather>> {

    // Constant value for the loader id
    private static final int WEATHER_LOADER_ID = 1;

    // The last known location returned
    private Location mLastLocation;

    // The location returned from IntentService to ResultReceiver
    private String mLocationString;

    // Result receiver to handle the response from FetchLocationIntentService
    public AddressResultReceiver mResultReceiver = new AddressResultReceiver(new Handler());

    // Root of the layout of fragment
    private View mLayout;

    // Bundle for storing and passing the coordinates to Loader
    private Bundle mBundleCoordinates;

    // The coordinates of the user
    String lat, lon;

    // No internet TextView
    TextView mEmptyText;

    LoaderManager manager;

    // The array adapter to be used to fetch the data in UI
    WeatherAdapter mWeatherAdapter;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 199;

    // Tag for logging reasons
    private static final String LOG_TAG = WeatherFragment.class.getSimpleName();

    final private int MY_REQUEST_ACCESS_FINE_LOCATION = 100;

    private FusedLocationProviderClient mFusedLocationClient;

    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_weather, container, false);

        RecyclerView weatherList = fragmentView.findViewById(R.id.listView_weather);
        mEmptyText = fragmentView.findViewById(R.id.empty_view);
        //weatherList.setEmptyView(mEmptyText);
        weatherList.setLayoutManager(new LinearLayoutManager(getActivity()));

        mWeatherAdapter = new WeatherAdapter(getActivity(), new ArrayList<Weather>());

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFusedLocationClient = getFusedLocationProviderClient(getActivity());
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
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mLastLocation = location;
                                startLoader();
                            }

                        }
                    });
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
                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        if (mLastLocation != null) {
                                            mLastLocation = location;
                                            startLoader();
                                        }
                                    }
                                });
                    }
                } else {
                    Snackbar.make(mLayout, R.string.location_permission_not_granted,
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void startLoader() {
        // Start the service from here
        if (mLastLocation != null) {
            startIntentService();

            // Get the coordinates from last known location and pass the in Loader
            lat = String.valueOf(mLastLocation.getLatitude());
            lon = String.valueOf(mLastLocation.getLongitude());
        }

        mBundleCoordinates = new Bundle();
        mBundleCoordinates.putString("Latitude", lat);
        mBundleCoordinates.putString("Longitude", lon);
        mBundleCoordinates.putString("Location", mLocationString);

        manager = getActivity().getSupportLoaderManager();

        // Initialize the Loader
        manager.initLoader(WEATHER_LOADER_ID, null, this);
        manager.restartLoader(WEATHER_LOADER_ID, null, this);
    }

//    // Instantiate a new Location Request and set the intervals
//    protected void createLocationRequest() {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(20000);
//        mLocationRequest.setFastestInterval(5000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//    }

//    // Start listening on location updates called in onConnected
//    protected void startLocationUpdates() {
//        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
//                Manifest.permission.ACCESS_FINE_LOCATION);
//        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
//            FusedLocationApi.requestLocationUpdates(
//                    mGoogleApiClient, mLocationRequest, this);
//        }
//    }
//
//    // Stop listening for updates called in the onPause() method of lifecycle
//    protected void stopLocationUpdates() {
//        LocationServices.FusedLocationApi.removeLocationUpdates(
//                mGoogleApiClient, this);
//    }


    protected void startIntentService() {
        Intent intent = new Intent(getActivity(), FetchLocationIntentService.class);
        intent.putExtra(FetchLocationIntentService.Constants.RECEIVER, mResultReceiver);
        intent.putExtra(FetchLocationIntentService.Constants.LOCATION_DATA_EXTRA, mLastLocation);
        getActivity().startService(intent);
    }

    @Override
    public Loader<List<Weather>> onCreateLoader(int id, Bundle args) {
        // Create a new loader and pass the bundle with coordinates
        return new WeatherLoader(getActivity(), mBundleCoordinates);
    }

    @Override
    public void onLoadFinished(Loader<List<Weather>> loader, List<Weather> data) {
        View loadingIndicator = getActivity().findViewById(R.id.progress_bar);
        loadingIndicator.setVisibility(View.GONE);

        // Clear the adapter of previous data
        mWeatherAdapter.clearWeatherData();

        if (data != null && !data.isEmpty()) {
            mWeatherAdapter.updateWeatherData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Weather>> loader) {
        // On reset clear any existing data
        mWeatherAdapter.clearWeatherData();
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
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
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
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public class AddressResultReceiver extends ResultReceiver {

        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            mLocationString = resultData.getString(FetchLocationIntentService.Constants.RESULT_DATA_KEY);
            Log.i(LOG_TAG, mLocationString);

            // Show a toast message if an address was found.
            if (resultCode == FetchLocationIntentService.Constants.SUCCESS_RESULT) {
                Log.i(LOG_TAG, mLocationString);
            }
        }
    }
}
