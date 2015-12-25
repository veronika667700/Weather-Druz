package com.druzsan.druzweatherv06.utils;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.druzsan.druzweatherv06.CityFragment;
import com.druzsan.druzweatherv06.MainActivity;
import com.druzsan.druzweatherv06.R;
import com.druzsan.druzweatherv06.model.Weather;

import java.util.List;

/**
 * Created by druzsan on 18.12.2015.
 */
public class CityListAdapter extends BaseAdapter {

    private MainActivity mainActivity;
    private Context context;
    private List<Weather> cityList;

    public CityListAdapter(MainActivity mainActivity, Context context
            , List<Weather> cityList) {
        this.mainActivity = mainActivity;
        this.context = context;
        this.cityList = cityList;
    }

    @Override
    public int getCount() {
        return cityList.size();
    }

    @Override
    public Weather getItem(int position) {
        return cityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected void initItem(final int position, final ViewHolder view) {

        final Weather cityItem = cityList.get(position);

        view.city.setText(cityItem.getName());

        view.country.setText(cityItem.getCountry());

        view.temp.setText(Integer.toString((int) Math.round(cityItem.getTemp())) + "°");

        int curIcon = cityItem.getIcon();
        if (curIcon != -1)
            view.icon.setImageResource(curIcon);

        view.lvContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("CITY_ID", String.valueOf(getItem(position).getCityId()));
                Fragment fragment = new CityFragment();
                fragment.setArguments(bundle);
                MainActivity.getMainActivity().setCurrentFragment(fragment, true);
            }
        });
        view.lvContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View rowView = convertView;
        final ViewHolder view;

        if (rowView == null) {

            Typeface typefaceLight = Typeface.createFromAsset(mainActivity.getAssets()
                    , "fonts/Roboto-Light.ttf");
            Typeface typefaceRegular = Typeface.createFromAsset(mainActivity.getAssets()
                    , "fonts/Roboto-Regular.ttf");
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_cities_item, parent, false);
            view = new ViewHolder();
            view.city = (TextView) rowView.findViewById(R.id.lv_cities_city);
            view.city.setTypeface(typefaceLight);
            view.country = (TextView) rowView.findViewById(R.id.lv_cities_country);
            view.country.setTypeface(typefaceRegular);
            view.temp = (TextView) rowView.findViewById(R.id.lv_cities_temp);
            view.temp.setTypeface(typefaceLight);
            view.icon = (ImageView) rowView.findViewById(R.id.lv_cities_icon);
            view.lvContainer = (RelativeLayout) rowView.findViewById(R.id.lv_cities_container);
            rowView.setTag(view);
        } else {
            view = (ViewHolder) rowView.getTag();
        }
        initItem(position, view);
        return rowView;
    }

    protected static class ViewHolder {
        protected TextView city;
        protected TextView country;
        protected TextView temp;
        protected ImageView icon;
        protected RelativeLayout lvContainer;
    }
}
