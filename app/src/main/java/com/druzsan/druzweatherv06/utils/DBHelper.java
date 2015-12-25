package com.druzsan.druzweatherv06.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.druzsan.druzweatherv06.model.Weather;

import java.util.ArrayList;

/**
 * Created by druzsan on 18.12.2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private final Context context;
    private static final String DATABASE_NAME = "weather.db";
    private  static final int DB_VERSION = 1;
    public static final String TABLE_CITY_NAME = "city";
    public static final String TABLE_FORECAST_FIVE_NAME = "forecast_five";
    public static final String TABLE_SETTINGS_NAME = "data_settings";
    //public static final String TABLE_CURRENT_WEATHER_NAME = "current_weather";
    //public static final String TABLE_FORECAST_DAILY_NAME = "forecast_daily";

    public String createTableCityString = "CREATE TABLE " + TABLE_CITY_NAME + " ("
            + "id integer PRIMARY KEY AUTOINCREMENT, "
            + "city_id text NOT NULL UNIQUE, "
            + "name text NOT NULL, "
            + "country text NOT NULL, "

            + "lon real NOT NULL, "
            + "lat real NOT NULL "
            + ");";

    public String createTableSettingsString = "CREATE TABLE " + TABLE_SETTINGS_NAME + " ("
            + "id integer PRIMARY KEY AUTOINCREMENT, "
            + "weather_units text NOT NULL, "
            + "forecast_count integer NOT NULL"
            + ");";

    public String createTableForecastFiveString = "CREATE TABLE " + TABLE_FORECAST_FIVE_NAME + " ("
            + "id integer PRIMARY KEY AUTOINCREMENT, "
            + "city_id text NOT NULL, "
            + "date_system integer NOT NULL, "
            + "date text NOT NULL, "
            + "temp real NOT NULL, "
            + "temp_min real NOT NULL, "
            + "temp_max real NOT NULL, "
            + "pressure real NOT NULL, "
            + "humidity integer NOT NULL, "
            + "weather_id integer NOT NULL, "
            + "main_description text NOT NULL, "
            + "foreign_description text NOT NULL, "
            + "icon_id text NOT NULL, "
            + "cloudness integer NOT NULL, "
            + "wind_speed real NOT NULL, "
            + "wind_direction real NOT NULL"
            + ");";

    /*public String createTableCurrentWeatherString = "CREATE TABLE " + TABLE_CURRENT_WEATHER_NAME + " ("
            + "id integer PRIMARY KEY AUTOINCREMENT, "
            + "city_id text NOT NULL UNIQUE, "
            + "date text NOT NULL, "
            + "temp real NOT NULL, "
            + "weather_id integer NOT NULL, "
            + "icon_id text NOT NULL, "

            + "weather_description_main text NOT NULL, "
            + "weather_description_foreign text NOT NULL, "

            + "temp_min real NOT NULL, "
            + "temp_max real NOT NULL, "

            + "humidity integer NOT NULL, "

            + "wind_speed real NOT NULL, "
            + "wind_direction real NOT NULL, "

            + "pressure real NOT NULL, "

            + "clouds integer NOT NULL" + ");";*/

    /*public String createTableForecastDailyString = "CREATE TABLE " + TABLE_FORECAST_DAILY_NAME + " ("
            + "id integer PRIMARY KEY AUTOINCREMENT, "
            + "city_id text NOT NULL, "
            + "date text NOT NULL, "
            + "temp_min real NOT NULL, "
            + "temp_max real NOT NULL, "
            + "weather_id integer NOT NULL, "
            + "icon_id text NOT NULL" + ");";*/

    public DBHelper(Context _context) {
        super(_context, DATABASE_NAME, null, DB_VERSION);
        context = _context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableCityString);
        db.execSQL(createTableSettingsString);
        db.execSQL(createTableForecastFiveString);
        //db.execSQL(createTableCurrentWeatherString);
        //db.execSQL(createTableForecastDailyString);
        initiateDB(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITY_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORECAST_FIVE_NAME);
            //db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURRENT_WEATHER_NAME);
            //db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORECAST_DAILY_NAME);
            onCreate(db);
        }
    }

    private void initiateDB(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put("city_id", "501175");
        cv.put("name", "Rostov-na-Donu");
        cv.put("country", "RU");
        cv.put("lon", 39.71389);
        cv.put("lat", 47.236389);
        db.insert(TABLE_CITY_NAME, "city_id", cv);

        ContentValues cv1 = new ContentValues();
        cv1.put("weather_units", "metric");
        cv1.put("forecast_count", "8");
        db.insert(TABLE_SETTINGS_NAME, null, cv1);
    }

    public String getWeatherUnits() {
        Cursor c = getReadableDatabase().query(TABLE_SETTINGS_NAME, new String[] { "weather_units" }
                , null, null, null, null, null, null);
        String weatherUnits;
        if (c.moveToFirst()) {
            int weatherUnitsColIndex = c.getColumnIndex("weather_units");
            do {
                weatherUnits = c.getString(weatherUnitsColIndex);
            } while (c.moveToNext());
            return weatherUnits;
        }
        return "metric";
    }

    public int getForecastCount() {
        Cursor c = getReadableDatabase().query(TABLE_SETTINGS_NAME, new String[]{"forecast_count"}
                , null, null, null, null, null, null);
        int forecastCount;
        if (c.moveToFirst()) {
            int forecastCountColIndex = c.getColumnIndex("forecast_count");
            do {
                forecastCount = c.getInt(forecastCountColIndex);
            } while (c.moveToNext());
            return forecastCount;
        }
        return 8;
    }
    
    public ArrayList<String> getCitiesId() {
        Cursor c = getReadableDatabase().query(TABLE_CITY_NAME, new String[] { "city_id" }
                , null, null, null, null, null, null);
        ArrayList<String>arr = new ArrayList<String>();
        if (c.moveToFirst()) {
            int cityIdColIndex = c.getColumnIndex("city_id");
            do {
                arr.add(c.getString(cityIdColIndex));
            } while (c.moveToNext());
        }
        if (arr == null)
            return null;
        return arr;
    }

    public ArrayList<Weather> getCities() {

        Cursor c = getReadableDatabase().rawQuery("SELECT c.city_id, c.name AS name, c.country AS country"
                + ", f.temp AS temp, f.weather_id AS weather_id, f.icon_id AS icon_id FROM "
                + TABLE_CITY_NAME + " AS c INNER JOIN (SELECT f1.city_id as city_id"
                + ", min(date_system) as min_date_system FROM " + TABLE_FORECAST_FIVE_NAME
                + " AS f1 GROUP BY f1.city_id) AS res ON c.city_id = res.city_id INNER JOIN "
                + TABLE_FORECAST_FIVE_NAME + " AS f ON res.city_id = f.city_id "
                + "AND res.min_date_system = f.date_system ORDER BY c.id", null);
        ArrayList<Weather>arr = new ArrayList<Weather>();
        if (c.moveToFirst()) {
            int cityIdColIndex = c.getColumnIndex("city_id");
            int nameColIndex = c.getColumnIndex("name");
            int countryColIndex = c.getColumnIndex("country");
            int tempColIndex = c.getColumnIndex("temp");
            int weatherIdColIndex = c.getColumnIndex("weather_id");
            int iconIdColIndex = c.getColumnIndex("icon_id");

            do {
                arr.add(new Weather(c.getString(cityIdColIndex), c.getString(nameColIndex)
                        , c.getString(countryColIndex), c.getDouble(tempColIndex)
                        , c.getInt(weatherIdColIndex), c.getString(iconIdColIndex)));
            } while (c.moveToNext());
        }
        if (arr == null)
            return null;
        return arr;
    }

    public Weather getCityWeather(String cityId) {
        Cursor c = getReadableDatabase().rawQuery("SELECT c.name AS name, f.temp AS temp"
                + ", f.temp_min AS temp_min, f.temp_max AS temp_max, f.pressure AS pressure"
                + ", f.humidity AS humidity, f.weather_id AS weather_id"
                + ", f.foreign_description AS foreign_description, f.icon_id AS icon_id" +
                ", f.cloudness AS cloudness, f.wind_speed AS wind_speed" +
                ", f.wind_direction AS wind_direction FROM " + TABLE_CITY_NAME
                + " AS c INNER JOIN (SELECT f1.city_id as city_id" +
                ", min(f1.date_system) AS min_date_system FROM " + TABLE_FORECAST_FIVE_NAME
                + " AS f1 WHERE f1.city_id = " + cityId + " GROUP BY f1.city_id) AS res "
                + "ON c.city_id = res.city_id INNER JOIN " + TABLE_FORECAST_FIVE_NAME
                + " AS f ON res.city_id = f.city_id AND res.min_date_system "
                + "= f.date_system", null);
        Weather weather = new Weather();
        if (c.moveToFirst()) {
            int nameColIndex = c.getColumnIndex("name");
            int tempColIndex = c.getColumnIndex("temp");
            int tempMinColIndex = c.getColumnIndex("temp_min");
            int tempMaxColIndex = c.getColumnIndex("temp_max");
            int pressureColIndex = c.getColumnIndex("pressure");
            int humidityColIndex = c.getColumnIndex("humidity");
            int weatherIdColIndex = c.getColumnIndex("weather_id");
            int descriptionForeignColIndex = c.getColumnIndex("foreign_description");
            int iconIdColIndex = c.getColumnIndex("icon_id");
            int cloudnessColIndex = c.getColumnIndex("cloudness");
            int windSpeedColIndex = c.getColumnIndex("wind_speed");
            int windDirectionColIndex = c.getColumnIndex("wind_direction");

            do {
                weather = new Weather(c.getString(nameColIndex)
                        , c.getString(descriptionForeignColIndex), c.getInt(weatherIdColIndex)
                        , c.getString(iconIdColIndex), c.getDouble(tempColIndex)
                        , c.getDouble(tempMinColIndex), c.getDouble(tempMaxColIndex)
                        , c.getInt(humidityColIndex), c.getDouble(windSpeedColIndex)
                        , c.getDouble(windDirectionColIndex), c.getDouble(pressureColIndex)
                        , c.getInt(cloudnessColIndex));
            } while (c.moveToNext());
        }
        if (weather == null)
            return null;
        return weather;
    }

    public ArrayList<Weather> getCityForecastFive(String cityId) {
        Cursor c = getReadableDatabase().rawQuery("SELECT f.date_system AS date_system" +
                ", f.temp_min AS temp_min, f.temp_max AS temp_max, f.weather_id AS weather_id"
                + ", f.icon_id AS icon_id FROM " + TABLE_FORECAST_FIVE_NAME
                + " AS f WHERE c.city_id = " + cityId + " ORDER BY f.date_system", null);
        ArrayList<Weather>arr = new ArrayList<Weather>();
        if (c.moveToFirst()) {
            int dateSystemColIndex = c.getColumnIndex("date");
            int tempMinColIndex = c.getColumnIndex("temp_min");
            int tempMaxColIndex = c.getColumnIndex("temp_max");
            int weatherIdColIndex = c.getColumnIndex("weather_id");
            int iconIdColIndex = c.getColumnIndex("icon_id");

            do {
                arr.add(new Weather(c.getString(dateSystemColIndex)
                        , c.getInt(weatherIdColIndex), c.getString(iconIdColIndex)
                        , c.getDouble(tempMaxColIndex), c.getDouble(tempMinColIndex)));
            } while (c.moveToNext());
        }
        if (arr == null)
            return null;
        return arr;
    }

    public void deleteCity(String cityId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CITY_NAME, "city_id = ?", new String[] { cityId });
        db.delete(TABLE_FORECAST_FIVE_NAME, "city_id = ?", new String[] { cityId });
    }

    /*public ArrayList<Weather> getCities() {

        Cursor c = getReadableDatabase().rawQuery("SELECT c.city_id, c.name AS name"
                + ", c.country AS country, cw.temp AS temp, cw.weather_id AS weather_id"
                + ", cw.icon_id AS icon_id FROM " + TABLE_CITY_NAME + " AS c INNER JOIN "
                + TABLE_CURRENT_WEATHER_NAME + " AS cw ON c.city_id = cw.city_id "
                + "ORDER BY c.id", null);
        ArrayList<Weather>arr = new ArrayList<Weather>();
        if (c.moveToFirst()) {
            int cityIdColIndex = c.getColumnIndex("city_id");
            int nameColIndex = c.getColumnIndex("name");
            int countryColIndex = c.getColumnIndex("country");
            int tempColIndex = c.getColumnIndex("temp");
            int weatherIdColIndex = c.getColumnIndex("weather_id");
            int iconIdColIndex = c.getColumnIndex("icon_id");

            do {
                arr.add(new Weather(c.getString(cityIdColIndex), c.getString(nameColIndex)
                        , c.getString(countryColIndex), c.getDouble(tempColIndex)
                        , c.getInt(weatherIdColIndex), c.getString(iconIdColIndex)));
            } while (c.moveToNext());
        }
        if (arr == null)
            return null;
        return arr;
    }*/

}
