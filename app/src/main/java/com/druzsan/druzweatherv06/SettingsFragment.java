package com.druzsan.druzweatherv06;

import android.app.Fragment;
import android.content.ContentValues;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.druzsan.druzweatherv06.utils.DBHelper;

/**
 * Created by druzsan on 23.12.2015.
 */
public class SettingsFragment extends Fragment {

    private String currentWeatherUnits;
    private int currentForecastCount;
    private MainActivity mainActivity;

    public SettingsFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        mainActivity = (MainActivity) getActivity();
        final DBHelper dbHelper = new DBHelper(getActivity().getApplicationContext());
        currentWeatherUnits = dbHelper.getWeatherUnits();
        currentForecastCount = dbHelper.getForecastCount();

        Typeface typefaceLight = Typeface.createFromAsset(getActivity().getAssets()
                , "fonts/Roboto-Light.ttf");
        Typeface typefaceRegular = Typeface.createFromAsset(mainActivity.getAssets()
                , "fonts/Roboto-Regular.ttf");

        TextView unitsText = (TextView) rootView.findViewById(R.id.settings_units_text);
        unitsText.setTypeface(typefaceRegular);

        RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.rg_settings);
        RadioButton rbCelcius = (RadioButton) rootView.findViewById(R.id.rb_settings_celcius);
        rbCelcius.setTypeface(typefaceLight);
        RadioButton rbFahrenheit = (RadioButton) rootView.findViewById(R.id.rb_settings_fahrenheit);
        rbFahrenheit.setTypeface(typefaceLight);
        RadioButton rbKelvin = (RadioButton) rootView.findViewById(R.id.rb_settings_kelvin);
        rbKelvin.setTypeface(typefaceLight);

        mainActivity.setTitle("Settings");
        mainActivity.setFabVisibility(true);

        switch (currentWeatherUnits) {
            case "metric": {
                rbCelcius.setChecked(true);
                break;
            }
            case "fahrenheit": {
                rbFahrenheit.setChecked(true);
                break;
            }
            case "kelvin": {
                rbKelvin.setChecked(true);
                break;
            }
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_settings_celcius: {
                        currentWeatherUnits = "metric";
                        break;
                    }
                    case R.id.rb_settings_fahrenheit: {
                        currentWeatherUnits = "fahrenheit";
                        break;
                    }
                    case R.id.rb_settings_kelvin: {
                        currentWeatherUnits = "kelvin";
                        break;
                    }
                }
                ContentValues cv = new ContentValues();
                cv.put("weather_units", currentWeatherUnits);
                cv.put("forecast_count", currentForecastCount);
                dbHelper.getWritableDatabase().delete(DBHelper.TABLE_SETTINGS_NAME, null, null);
                dbHelper.getWritableDatabase().insert(DBHelper.TABLE_SETTINGS_NAME, null, cv);
                android.util.Log.d("Settings", "units = " + dbHelper.getWeatherUnits());
                mainActivity.refreshDB(mainActivity.getApplicationContext());
            }
        });

        return rootView;
    }
}
