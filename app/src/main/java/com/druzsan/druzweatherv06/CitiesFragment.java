package com.druzsan.druzweatherv06;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.druzsan.druzweatherv06.utils.CityListAdapter;
import com.druzsan.druzweatherv06.utils.DBHelper;
import com.druzsan.druzweatherv06.model.Weather;

import java.util.ArrayList;

/**
 * Created by druzsan on 18.12.2015.
 */
public class CitiesFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {

    private MainActivity mainActivity;
    private Context context;
    private View rootView;
    private DBHelper dbHelper;
    private SwipeRefreshLayout swipeRefreshLayout;

    public CitiesFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cities, container, false);

        mainActivity = (MainActivity) getActivity();
        context = getActivity().getApplicationContext();
        dbHelper = new DBHelper(context);

        swipeRefreshLayout = (SwipeRefreshLayout)
                rootView.findViewById(R.id.cities_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary);

        mainActivity.setTitle("Main");
        mainActivity.setFabVisibility(true);
        showCities(rootView);

        return rootView;
    }

    @Override
    public void onRefresh() {
        mainActivity.refreshDB(context);
        showCities(rootView);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showCities(View rootView) {
        ArrayList<Weather> cities = dbHelper.getCities();
        if (cities != null) {
            CityListAdapter cityListAdapter = new CityListAdapter(mainActivity, context, cities);
            ListView lvCities = (ListView) rootView.findViewById(R.id.lv_cities);
            lvCities.setAdapter(cityListAdapter);
        }
    }

}
