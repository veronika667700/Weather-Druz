package com.druzsan.druzweatherv06;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.druzsan.druzweatherv06.utils.DBHelper;
import com.druzsan.druzweatherv06.model.Weather;

/**
 * Created by druzsan on 19.12.2015.
 */
public class CityFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {

    private MainActivity mainActivity;
    private Context context;
    private View rootView;
    private DBHelper dbHelper;
    private Typeface typefaceThin;
    private Typeface typefaceLight;
    private Typeface typefaceRegular;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String cityId;

    public CityFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null){
            cityId = bundle.getString("CITY_ID");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_city
                , container, false);

        mainActivity = (MainActivity) getActivity();
        context = mainActivity.getApplicationContext();
        dbHelper = new DBHelper(context);

        swipeRefreshLayout = (SwipeRefreshLayout)
                rootView.findViewById(R.id.city_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary);

        typefaceThin = Typeface.createFromAsset(mainActivity.getAssets()
                , "fonts/Roboto-Thin.ttf");
        typefaceLight = Typeface.createFromAsset(mainActivity.getAssets()
                , "fonts/Roboto-Light.ttf");
        typefaceRegular = Typeface.createFromAsset(mainActivity.getAssets()
                , "fonts/Roboto-Regular.ttf");

        mainActivity.setTitle("Overview");
        mainActivity.setFabVisibility(true);
        showWeather(rootView);

        return rootView;
    }

    @Override
    public void onRefresh() {
        mainActivity.refreshDB(context);
        showWeather(rootView);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showWeather(View rootView) {
        Weather cityWeather = dbHelper.getCityWeather(cityId);

        TextView name = (TextView) rootView.findViewById(R.id.city_name);
        name.setTypeface(typefaceLight);
        ImageView icon = (ImageView) rootView.findViewById(R.id.city_icon);
        TextView description = (TextView) rootView.findViewById(R.id.city_description);
        description.setTypeface(typefaceLight);
        TextView temp = (TextView) rootView.findViewById(R.id.city_temp);
        temp.setTypeface(typefaceThin);

        TextView tempRangeText = (TextView) rootView.findViewById(R.id.city_temperature_range_text);
        tempRangeText.setTypeface(typefaceRegular);
        TextView tempRange = (TextView) rootView.findViewById(R.id.city_temperature_range);
        tempRange.setTypeface(typefaceLight);

        TextView humidityText = (TextView) rootView.findViewById(R.id.city_humidity_text);
        humidityText.setTypeface(typefaceRegular);
        TextView humidity = (TextView) rootView.findViewById(R.id.city_humidity);
        humidity.setTypeface(typefaceLight);

        TextView windText = (TextView) rootView.findViewById(R.id.city_wind_text);
        windText.setTypeface(typefaceRegular);
        TextView wind = (TextView) rootView.findViewById(R.id.city_wind);
        wind.setTypeface(typefaceLight);

        TextView pressureText = (TextView) rootView.findViewById(R.id.city_pressure_text);
        pressureText.setTypeface(typefaceRegular);
        TextView pressure = (TextView) rootView.findViewById(R.id.city_pressure);
        pressure.setTypeface(typefaceLight);

        TextView cloudText = (TextView) rootView.findViewById(R.id.city_cloud_text);
        cloudText.setTypeface(typefaceRegular);
        TextView cloud = (TextView) rootView.findViewById(R.id.city_cloud);
        cloud.setTypeface(typefaceLight);

        name.setText(cityWeather.getName());
        icon.setImageResource(cityWeather.getIcon());
        description.setText(cityWeather.getWeatherDescriptionForeign());
        temp.setText(String.valueOf(Math.round(cityWeather.getTemp())) + "°");

        tempRange.setText(String.valueOf(Math.round(cityWeather.getTempMin())) + "°/"
                + String.valueOf(Math.round(cityWeather.getTempMax())) + "°");

        humidity.setText(String.valueOf(cityWeather.getHumidity()) + "%");

        DBHelper dbHelper = new DBHelper(mainActivity.getApplicationContext());
        switch (dbHelper.getWeatherUnits()) {
            case "imperial": {
                wind.setText(cityWeather.getWindLetter() + " "
                        + String.valueOf(Math.round(cityWeather.getWindSpeed())) + " mph");
                break;
            }
            default: {
                wind.setText(cityWeather.getWindLetter() + " "
                        + String.valueOf(cityWeather.getWindSpeed()) + " m/sec");
            }
        }

        pressure.setText(String.valueOf(Math.round(cityWeather.getPressure())) + " hPa");

        cloud.setText(String.valueOf(cityWeather.getCloud()) + "%");
    }

}
