package com.android.app.weatherproject.ui.list;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.app.weatherproject.R;
import com.android.app.weatherproject.ui.WeatherFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If the activity is not being restored from a previous state then create the new
        // fragment to be placed in the activity container otherwise there would be
        // overlapping fragments
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_container, new WeatherFragment())
                    .commit();
        }
    }
}

