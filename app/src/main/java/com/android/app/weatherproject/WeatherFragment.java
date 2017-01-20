package com.android.app.weatherproject;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class WeatherFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Weather>> {

    // Constant value for the loader id
    private static final int WEATHER_LOADER_ID = 1;

    // Root of the layout of fragment
    private View mLayout;

    // Bundle for storing and passing the coordinates to Loader
    private Bundle mBundleCoordinates;

    // The coordinates of the user
    String lat, lon;

    // No internet TextView
    TextView mEmptyText;

    // The array adapter to be used to fetch the data in UI
    ArrayAdapter<Weather> mWeatherAdapter;

    // Tag for logging reasons
    private static final String LOG_TAG = WeatherFragment.class.getSimpleName();

    private LocationManager mLocationManager;

    // An app defined request constant. The callback method gets the result of the request
    final private int MY_REQUEST_ACCESS_COARSE_LOCATION = 100;

    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mLocationManager = (LocationManager) getActivity()
                .getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_weather, container, false);

        ListView weatherList = (ListView) fragmentView.findViewById(R.id.listView_weather);
        mEmptyText = (TextView) fragmentView.findViewById(R.id.empty_view);
        weatherList.setEmptyView(mEmptyText);

        // Check if the permission for location is granted by the user
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_REQUEST_ACCESS_COARSE_LOCATION);
        } else {
            Log.v(LOG_TAG, "PERMISSION LOCATION IS GRANTED");
            // Register the listener with the Location Manager to receive location updates
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }

        // Utilize a cached location until the listener receive a more accurate position
        Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        lat = String.valueOf(lastKnownLocation.getLatitude());
        lon = String.valueOf(lastKnownLocation.getLongitude());



        mBundleCoordinates = new Bundle();
        mBundleCoordinates.putString("Latitude", lat);
        mBundleCoordinates.putString("Longitude", lon);

        mWeatherAdapter = new WeatherAdapter(getActivity(), new ArrayList<Weather>());

        GetWeatherData getWeatherData = new GetWeatherData(getActivity(), mWeatherAdapter);

        weatherList.setAdapter(mWeatherAdapter);

        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Here initialize the loader
        ConnectivityManager connManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get reference to Loader Manager
            LoaderManager manager = getActivity().getSupportLoaderManager();

            // Initialize the Loader
            manager.initLoader(WEATHER_LOADER_ID, null, this);
        } else {
            // There is no Internet connection so
            View loadingIndicator = getActivity().findViewById(R.id.progress_bar);
            loadingIndicator.setVisibility(GONE);
            // Show the no internet connection
            mEmptyText.setText(R.string.no_internet_connection_string);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(LOG_TAG, "LOCATION permission is granted.");
                } else {
                    Log.i(LOG_TAG, "LOCATION permission was not granted.");
                    Snackbar.make(mLayout, R.string.location_permission_not_granted,
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Define a listener that responds to Location updates
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // Called when new location is found by the network location provider
            extractCoordinatesFromLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    public String[] extractCoordinatesFromLocation(Location userLocation) {

        String userLat = String.valueOf(userLocation.getLatitude());
        String userLon = String.valueOf(userLocation.getLongitude());

        return new  String[] {userLat, userLon};
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.removeUpdates(locationListener);
        }
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
        mWeatherAdapter.clear();

        if (data != null && !data.isEmpty()) {
            mWeatherAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Weather>> loader) {

            // On reset clear any existing data
            mWeatherAdapter.clear();
    }
}
