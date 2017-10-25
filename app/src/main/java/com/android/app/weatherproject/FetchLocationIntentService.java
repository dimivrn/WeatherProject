package com.android.app.weatherproject;


import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchLocationIntentService extends IntentService {

    private static final String LOG_TAG = FetchLocationIntentService.class.getSimpleName();

    public ResultReceiver mReceiver;

    public FetchLocationIntentService() {
        super("background");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String error = "";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(
                Constants.LOCATION_DATA_EXTRA);
        // Get the Result Receiver
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Service not available");
        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, "Invalid coordinates");
        }

        if (addresses == null || addresses.size() == 0) {
            Log.e(LOG_TAG, "No address found");
        } else {
            // Get only the city name from addresses
            Address address = addresses.get(0);
            String cityName = address.getLocality();
            ArrayList<String> addressFragments = new ArrayList<String>();

            addressFragments.add(0, cityName);

            // Fetch the address city name using getAddressLine,
            // join them, and send them to the thread
            Log.i(LOG_TAG, getString(R.string.success_message));
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragments));
        }
    }

    // Sending the location back to the ResultReceiver of fragment
    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }

    // Define Constant class to contain the needed values
    public final class Constants {
        static final int SUCCESS_RESULT = 0;
        static final String PACKAGE_NAME =
                "com.android.app.weatherproject";
        static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
        static final String RESULT_DATA_KEY = PACKAGE_NAME +
                ".RESULT_DATA_KEY";
        static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
                ".LOCATION_DATA_EXTRA";
    }
}
