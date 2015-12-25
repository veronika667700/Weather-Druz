package com.druzsan.druzweatherv06;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.druzsan.druzweatherv06.utils.DBHelper;
import com.druzsan.druzweatherv06.network.API;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static MainActivity instance;
    private CharSequence mTitle;
    private static Toolbar mToolbar;
    private static FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrentFragment(new AddCityFragment(), true);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        android.support.v7.app.ActionBarDrawerToggle toggle = new
                android.support.v7.app.ActionBarDrawerToggle(this, drawer, mToolbar
                , R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setCurrentFragment(new CitiesFragment(), false);

        refreshDB(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            setCurrentFragment(new SettingsFragment(), true);
            return true;
        } else if (id == R.id.action_refresh) {
            refreshDB(getApplicationContext());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            setCurrentFragment(new CitiesFragment(), true);
        } else if (id == R.id.nav_add) {
            setCurrentFragment(new AddCityFragment(), true);
        } else if (id == R.id.nav_settings) {
            setCurrentFragment(new SettingsFragment(), true);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setCurrentFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        if (addToBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }

    public static MainActivity getMainActivity() {
        return instance;
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    public static void showToast(Context context, CharSequence text, boolean flag) {
        if (flag) {
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }

    public static void setFabVisibility(boolean flag) {
        if (flag == false) {
            fab.setVisibility(View.INVISIBLE);
        } else {
            fab.setVisibility(View.VISIBLE);
        }
    }

    public static void refreshDB(Context context) {
        if (isNetworkConnected(context)) {
            DBHelper dbHelper = new DBHelper(context);
            ArrayList<String> ids = dbHelper.getCitiesId();
            if (ids != null){
                for (int i=0; i<ids.size(); ++i) {
                    getWeather(context, "id", ids.get(i));
                }
            }
        } else {
            showToast(context, "Sorry! You don't have the internet connection", false);
        }
    }

    public static void getWeather(final Context context, final String mode, final String value) {
        new AsyncTask<Void, Void, API.ApiResponse>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected API.ApiResponse doInBackground(Void... x) {
                ArrayList<String> params = new ArrayList<String>();
                if (mode == "id") {
                    params.add("id");
                } else if (mode == "city"){
                    params.add("q");
                }
                params.add(value);
                DBHelper dbHelper = new DBHelper(context);
                switch (dbHelper.getWeatherUnits()) {
                    case "metric": {
                        params.add("units"); params.add("metric");
                        break;
                    }
                    case "fahrenheit": {
                        params.add("units"); params.add("imperial");
                        break;
                    }
                }
                params.add("cnt"); params.add(String.valueOf(dbHelper.getForecastCount()));
                params.add("APPID"); params.add("58c3cdec0969373fd82d01a13c7de5bc");

                return API.execute(API.ApiMethod.GET_WEATHER.format()
                        , API.HttpMethod.GET, params.toArray(new String[params.size()]));
            }

            @Override
            protected void onPostExecute(API.ApiResponse apiResponse) {
                super.onPostExecute(apiResponse);
                try {
                    if (apiResponse.isSuccess()) {
                        DBHelper dbHelper = new DBHelper(context);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues cvCity = new ContentValues();
                        long check = 0;

                        JSONObject resp = new JSONObject(apiResponse.getJson().toString());
                        JSONObject city = resp.getJSONObject("city");
                        JSONObject cityCoord = city.getJSONObject("coord");
                        String cityId = city.getString("id");

                        cvCity.put("city_id", cityId);
                        cvCity.put("name", city.getString("name"));
                        cvCity.put("lon", cityCoord.getDouble("lon"));
                        cvCity.put("lat", cityCoord.getDouble("lat"));
                        cvCity.put("country", city.getString("country"));

                        check = db.insertWithOnConflict(DBHelper.TABLE_CITY_NAME, null
                                , cvCity, SQLiteDatabase.CONFLICT_IGNORE);
                        if (check == -1) {
                            db.update(DBHelper.TABLE_CITY_NAME, cvCity
                                    , "city_id = ?", new String[] { cityId });
                        }

                        ContentValues cvForecastFive = new ContentValues();
                        JSONArray cityWeatherList = resp.optJSONArray("list");
                        JSONObject weather;
                        JSONObject weatherMain;
                        JSONObject weatherType;
                        check = db.delete(DBHelper.TABLE_FORECAST_FIVE_NAME, "city_id = ?"
                                , new String[] { cityId });
                        for (int i=0; i<cityWeatherList.length(); ++i) {
                            weather = cityWeatherList.getJSONObject(i);
                            weatherMain = weather.getJSONObject("main");
                            weatherType = weather.getJSONArray("weather").getJSONObject(0);

                            cvForecastFive.put("city_id", cityId);
                            cvForecastFive.put("date_system", weather.getLong("dt"));
                            cvForecastFive.put("date", weather.getString("dt_txt"));
                            cvForecastFive.put("temp", weatherMain.getString("temp"));
                            cvForecastFive.put("temp_min", weatherMain.getDouble("temp_min"));
                            cvForecastFive.put("temp_max", weatherMain.getDouble("temp_max"));
                            cvForecastFive.put("pressure", weatherMain.getInt("pressure"));
                            cvForecastFive.put("humidity", weatherMain.getInt("humidity"));
                            cvForecastFive.put("weather_id", weatherType.getInt("id"));
                            cvForecastFive.put("main_description", weatherType.getString("main"));
                            cvForecastFive.put("foreign_description", weatherType
                                    .getString("description"));
                            cvForecastFive.put("icon_id", weatherType.getString("icon"));
                            cvForecastFive.put("cloudness", weather
                                    .getJSONObject("clouds").getInt("all"));
                            cvForecastFive.put("wind_speed", weather
                                    .getJSONObject("wind").getDouble("speed"));
                            cvForecastFive.put("wind_direction", weather
                                    .getJSONObject("wind").getDouble("deg"));

                            check = db.insert(DBHelper.TABLE_FORECAST_FIVE_NAME, null
                                    , cvForecastFive);
                        }
                    } else {
                        showToast(context, "Sorry! No such city was found", false);
                    }
                } catch (Exception e) {
                    Log.e("Weather", "ALERT! ALERT! Exception!", e);
                } finally {}
            }
        }.execute();
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null)
            return false;
        return true;
    }

    /*public static void getCurrentWeather(final Context context, final SQLiteDatabase db
            , final String weatherUnits, final String mode, final String value) {
        new AsyncTask<Void, Void, API.ApiResponse>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected API.ApiResponse doInBackground(Void... x) {
                ArrayList<String> params = new ArrayList<String>();
                if (mode == "id") {
                    params.add("id");
                } else if (mode == "city"){
                    params.add("q");
                }
                params.add(value);
                params.add("units"); params.add(weatherUnits);
                params.add("APPID"); params.add("58c3cdec0969373fd82d01a13c7de5bc");

                android.util.Log.d("Getting current weather", "before API");
                return API.execute("current weather", API.ApiMethod.GET_WEATHER.format()
                        , API.HttpMethod.GET, params.toArray(new String[params.size()]));
            }

            @Override
            protected void onPostExecute(API.ApiResponse apiResponse) {
                super.onPostExecute(apiResponse);
                try {
                    android.util.Log.d("Getting current weather", "before start");
                    if (apiResponse.isSuccess()) {

                        android.util.Log.d("Getting current weather", "start");
                        //DBHelper dbHelper = new DBHelper(context);
                        //SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues cvCity = new ContentValues();
                        ContentValues cvForecastDaily = new ContentValues();
                        long check = 0;

                        JSONObject resp = new JSONObject(apiResponse.getJson().toString());
                        JSONObject cityCoord = resp.getJSONObject("coord");
                        JSONObject weather = resp.getJSONArray("weather").getJSONObject(0);
                        JSONObject main = resp.getJSONObject("main");
                        JSONObject wind = resp.getJSONObject("wind");
                        JSONObject clouds = resp.getJSONObject("clouds");
                        JSONObject sys = resp.getJSONObject("sys");
                        String cityId = resp.getString("id");

                        cvCity.put("city_id", cityId);
                        cvCity.put("name", resp.getString("name"));
                        cvCity.put("country", sys.getString("country"));
                        cvCity.put("lon", cityCoord.getDouble("lon"));
                        cvCity.put("lat", cityCoord.getDouble("lat"));

                        check = db.insertWithOnConflict(DBHelper.TABLE_CITY_NAME, null
                                , cvCity, SQLiteDatabase.CONFLICT_IGNORE);
                        android.util.Log.d("Getting current weather", check
                                + " rows were inserted in " + DBHelper.TABLE_CITY_NAME);
                        if (check == -1) {
                            check = db.update(DBHelper.TABLE_CITY_NAME, cvCity
                                    , "city_id = ?", new String[] { cityId });
                            android.util.Log.d("Getting current weather", check
                                    + " rows were updated in " + DBHelper.TABLE_CITY_NAME);
                        }

                        cvForecastDaily.put("city_id", cityId);
                        cvForecastDaily.put("date", resp.getString("dt"));
                        cvForecastDaily.put("temp", main.getDouble("temp"));
                        cvForecastDaily.put("weather_id", weather.getInt("id"));
                        cvForecastDaily.put("icon_id", weather.getString("icon"));
                        cvForecastDaily.put("weather_description_main", main.getString("main"));
                        cvForecastDaily.put("weather_description_foreign"
                                , main.getString("description"));
                        cvForecastDaily.put("temp_min", main.getDouble("temp_min"));
                        cvForecastDaily.put("temp_max", main.getDouble("temp_max"));
                        cvForecastDaily.put("humidity", main.getInt("humidity"));
                        cvForecastDaily.put("wind_speed", wind.getDouble("speed"));
                        cvForecastDaily.put("wind_direction", wind.getDouble("deg"));
                        cvForecastDaily.put("pressure", main.getDouble("humidity"));
                        cvForecastDaily.put("clouds", clouds.getInt("all"));

                        check = db.insertWithOnConflict(DBHelper.TABLE_CURRENT_WEATHER_NAME, null
                                , cvForecastDaily, SQLiteDatabase.CONFLICT_IGNORE);
                        android.util.Log.d("Getting current weather", check
                                + " rows were inserted in " + DBHelper.TABLE_CURRENT_WEATHER_NAME);
                        if (check == -1) {
                            check = db.update(DBHelper.TABLE_CURRENT_WEATHER_NAME, cvForecastDaily
                                    , "city_id = ?", new String[] { cityId });
                            android.util.Log.d("Getting current weather", check
                                    + " rows were updated in " + DBHelper.TABLE_CURRENT_WEATHER_NAME);
                        }
                    } else {
                        showToast(context, "Sorry! No such city was found", false);
                    }
                } catch (Exception e) {
                    Log.e("Weather", "ALERT! ALERT! Exception!", e);
                } finally {}
            }
        }.execute();
    }

    public static void getDailyWeather(final Context context, final SQLiteDatabase db
            , final String weatherUnits, final String mode, final String value) {
        new AsyncTask<Void, Void, API.ApiResponse>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected API.ApiResponse doInBackground(Void... x) {
                ArrayList<String> params = new ArrayList<String>();
                if (mode == "id") {
                    params.add("id");
                } else if (mode == "city"){
                    params.add("q");
                }
                params.add(value);
                params.add("units"); params.add(weatherUnits);
                params.add("cnt"); params.add("7");
                params.add("APPID"); params.add("58c3cdec0969373fd82d01a13c7de5bc");

                return API.execute("forecast daily", API.ApiMethod.GET_WEATHER.format()
                        , API.HttpMethod.GET, params.toArray(new String[params.size()]));
            }

            @Override
            protected void onPostExecute(API.ApiResponse apiResponse) {
                super.onPostExecute(apiResponse);
                try {
                    android.util.Log.d("Getting daily weather", "before start");
                    if (apiResponse.isSuccess()) {
                        android.util.Log.d("Getting daily weather", "start");
                        DBHelper dbHelper = new DBHelper(context);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues cv = new ContentValues();
                        long check = 0;

                        JSONObject resp = new JSONObject(apiResponse.getJson().toString());
                        JSONObject city = resp.getJSONObject("city");
                        JSONArray cityWeatherList = resp.optJSONArray("list");
                        String cityId = city.getString("id");

                        for (int i=0; i<cityWeatherList.length(); ++i) {
                            cv.put("city_id", cityId);
                            JSONObject weather = cityWeatherList.getJSONObject(i);
                            JSONObject temp = weather.getJSONObject("temp");
                            JSONObject weatherType = weather.getJSONArray("weather")
                                    .getJSONObject(0);

                            cv.put("city_id", cityId);
                            cv.put("date", weather.getString("dt"));
                            cv.put("temp_min", temp.getDouble("min"));
                            cv.put("temp_max", temp.getDouble("max"));
                            cv.put("weather_id", weatherType.getInt("id"));
                            cv.put("icon_id", weatherType.getString("icon"));

                            check = db.delete(DBHelper.TABLE_FORECAST_DAILY_NAME, "city_id = ?"
                                    , new String[] { cityId });
                            android.util.Log.d("Getting daily weather", check
                                    + " rows were deleted from" + DBHelper.TABLE_FORECAST_DAILY_NAME);
                            check = db.insert(DBHelper.TABLE_FORECAST_DAILY_NAME, null, cv);
                            android.util.Log.d("Getting daily weather", check
                                    + " rows were inserted in " + DBHelper.TABLE_FORECAST_DAILY_NAME);
                        }
                    } else {
                        showToast(context, "Sorry! No such city was found", false);
                    }
                } catch (Exception e) {
                    Log.e("Weather", "ALERT! ALERT! Exception!", e);
                } finally {}
            }
        }.execute();
    }*/

}
