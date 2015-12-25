package com.druzsan.druzweatherv06;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Created by druzsan on 23.12.2015.
 */
public class AddCityFragment extends Fragment {

    public AddCityFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_city
                , container, false);

        final MainActivity mainActivity = (MainActivity) getActivity();
        final Context context = getActivity().getApplicationContext();
        final EditText evNewCity = (EditText) rootView.findViewById(R.id.et_add_new_city);
        Typeface typefaceLight = Typeface.createFromAsset(mainActivity.getAssets()
                , "fonts/Roboto-Light.ttf");
        evNewCity.setTypeface(typefaceLight);
        final ImageButton buttonNewCity = (ImageButton) rootView
                .findViewById(R.id.button_add_new_city);
        buttonNewCity.setBackgroundColor(getResources().getColor(R.color.primary));

        mainActivity.setTitle("Add new city");
        mainActivity.setFabVisibility(false);

        buttonNewCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonNewCity.setBackgroundColor(getResources().getColor(R.color.primary_dark));
                if (evNewCity.getText().length() != 0) {
                    MainActivity.getWeather(context, "city", evNewCity.getText().toString());
                    mainActivity.onBackPressed();
                } else {
                    buttonNewCity.setBackgroundColor(getResources().getColor(R.color.primary));
                }
            }
        });

        evNewCity.setOnKeyListener(new View.OnKeyListener() {
                                       public boolean onKey(View v, int keyCode, KeyEvent event) {
                                           if (event.getAction() == KeyEvent.ACTION_DOWN &&
                                                   (keyCode == KeyEvent.KEYCODE_ENTER)) {
                                               if (evNewCity.getText().length() != 0) {
                                                   MainActivity.getWeather(context, "city"
                                                           , evNewCity.getText().toString());
                                                   mainActivity.onBackPressed();
                                                   return true;
                                               }
                                           }
                                           return false;
                                       }
                                   }
        );

        return rootView;
    }
}
