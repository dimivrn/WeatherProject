package com.android.app.weatherproject.fetchWeather;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.app.weatherproject.R;
import com.android.app.weatherproject.data.Weather;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static com.google.android.gms.location.LocationServices.FusedLocationApi;

public class WeatherFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Weather>>, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    // Constant value for the loader id
    private static final int WEATHER_LOADER_ID = 1;

    // The last known location returned
    private Location mLastLocation;

    // The location returned from IntentService to ResultReceiver
    private String mLocationString;

    // Location request object
    private LocationRequest mLocationRequest;

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

    // Instance of Google API Client
    private GoogleApiClient mGoogleApiClient;

    // The array adapter to be used to fetch the data in UI
    ArrayAdapter<Weather> mWeatherAdapter;

    // Tag for logging reasons
    private static final String LOG_TAG = WeatherFragment.class.getSimpleName();

    // An app defined request constant. The callback method gets the result of the request
    final private int MY_REQUEST_ACCESS_FINE_LOCATION = 100;

    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.v(LOG_TAG, "ON ATTACH");

        // Create instance of GoogleAPIClient
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_weather, container, false);

        ListView weatherList = fragmentView.findViewById(R.id.listView_weather);
        mEmptyText = fragmentView.findViewById(R.id.empty_view);
        weatherList.setEmptyView(mEmptyText);

        mWeatherAdapter = new WeatherAdapter(getActivity(), new ArrayList<Weather>());

        weatherList.setAdapter(mWeatherAdapter);

        ConnectivityManager connManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // Connect
            mGoogleApiClient.connect();
        } else {
            // There is no Internet connection so
            View loadingIndicator = fragmentView.findViewById(R.id.progress_bar);
            loadingIndicator.setVisibility(GONE);
            // Show the no internet connection
            mEmptyText.setText(R.string.no_internet_connection_string);
        }

        // Create the location request object
        createLocationRequest();

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION);
        ConnectivityManager connManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                if (mGoogleApiClient != null) {
                    mGoogleApiClient.connect();
                }
            }
        } else {
            // There is no Internet connection so
            View loadingIndicator = getActivity().findViewById(R.id.progress_bar);
            loadingIndicator.setVisibility(GONE);
            // Show the no internet connection
            mEmptyText.setText(R.string.no_internet_connection_string);
        }

        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startLocationUpdates();
        }
    }


    /**
     * Call backs provided by GoogleAPIClient
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLoader();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_ACCESS_FINE_LOCATION: {
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

    /**
     *
     */
    public void startLoader() {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }

        // Start the service from here
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startIntentService();
        }

        // Get the coordinates from last known location and pass the in Loader
        lat = String.valueOf(mLastLocation.getLatitude());
        lon = String.valueOf(mLastLocation.getLongitude());

        mBundleCoordinates = new Bundle();
        mBundleCoordinates.putString("Latitude", lat);
        mBundleCoordinates.putString("Longitude", lon);
        mBundleCoordinates.putString("Location", mLocationString);

        Log.v(LOG_TAG, "START LOADER METHOD THE LOCATION IS " + mLocationString);


        manager = getActivity().getSupportLoaderManager();

        // Initialize the Loader
        manager.initLoader(WEATHER_LOADER_ID, null, this);
        manager.restartLoader(WEATHER_LOADER_ID, null, this);
    }

    // Instantiate a new Location Request and set the intervals
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    // Start listening on location updates called in onConnected
    protected void startLocationUpdates() {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    // Stop listening for updates called in the onPause() method of lifecycle
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }


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


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        startLoader();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    // Lifecycle method here disconnect the Client
    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    class AddressResultReceiver extends ResultReceiver {

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
