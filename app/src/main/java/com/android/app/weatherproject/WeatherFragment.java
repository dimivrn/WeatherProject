package com.android.app.weatherproject;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class WeatherFragment extends Fragment {

    // Root of the layout of fragment
    private View mLayout;

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
        String lat = String.valueOf(lastKnownLocation.getLatitude());
        String lon = String.valueOf(lastKnownLocation.getLongitude());
        Log.i(LOG_TAG, "The latitute is" + lat);
        Log.i(LOG_TAG, "The longitute is" + lon);

        ListView weatherList = (ListView) fragmentView.findViewById(R.id.listView_weather);

        mWeatherAdapter = new WeatherAdapter(getActivity(), new ArrayList<Weather>());

        GetWeatherData getWeatherData = new GetWeatherData(getActivity(), mWeatherAdapter);
        getWeatherData.execute(lat, lon);

        weatherList.setAdapter(mWeatherAdapter);

        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
            Log.v(LOG_TAG, "EXTRACT COORDINATES IS CALLED");
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
}
