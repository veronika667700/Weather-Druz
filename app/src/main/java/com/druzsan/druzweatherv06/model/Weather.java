package com.druzsan.druzweatherv06.model;

import com.druzsan.druzweatherv06.R;

/**
 * Created by druzsan on 23.12.2015.
 */
public class Weather {

    private String cityId;
    private String name;
    private String country;
    private double temp;
    private int weatherId;
    private String iconId;
    private String weatherDescriptionForeign;
    private String date;
    private double tempMin;
    private double tempMax;
    private int humidity;
    private double windSpeed;
    private double windDirection;
    private double pressure;
    private int cloud;

    public Weather() {}

    public Weather(String cityId, String name, String country
            , double temp, int weatherId, String iconId) {
        this.cityId = cityId;
        this.name = name;
        this.country = country;
        this.temp = temp;
        this.weatherId = weatherId;
        this.iconId = iconId;
    }

    public Weather(String name, String weatherDescriptionForeign, int weatherId, String iconId
            , double temp, double tempMin, double tempMax, int humidity, double windSpeed
            , double windDirection, double pressure, int cloud) {
        this.name = name;
        this.weatherDescriptionForeign = weatherDescriptionForeign;
        this.weatherId = weatherId;
        this.iconId = iconId;
        this.temp = temp;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.pressure = pressure;
        this.cloud = cloud;
    }

    public Weather(String date, int weatherId, String iconId, double tempMax, double tempMin) {
        this.date = date;
        this.weatherId = weatherId;
        this.iconId = iconId;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
    }

    public String getCityId() { return this.cityId; }

    public String getName() { return this.name; }

    public String getCountry() { return this.country; }

    public double getTemp() { return this.temp; }

    public int getWeatherId() { return this.weatherId; }

    public String getIconId() { return this.iconId; }

    public String getWeatherDescriptionForeign() { return this.weatherDescriptionForeign; }

    public String getDate() { return this.date; }

    public double getTempMin() { return this.tempMin; }

    public double getTempMax() { return this.tempMax; }

    public int getHumidity() { return this.humidity; }

    public double getWindSpeed() { return this.windSpeed; }

    public double getWindDirection() { return this.windDirection; }

    public double getPressure() { return this.pressure; }

    public int getCloud() { return this.cloud; }

    public int getIcon() {

        //switch (this.weatherId) {}

        switch (this.iconId) {
            case "01d": return (R.drawable.sun);
            case "01n": return (R.drawable.moon);
            case "02d": return (R.drawable.sun_with_1cloud);
            case "02n": return (R.drawable.moon_with_clouds);
            case "03d":
            case "03n": return (R.drawable.clouds);
            case "04d":
            case "04n": return (R.drawable.clouds); //here must be many clouds
            case "09d":
            case "09n": return (R.drawable.clouds_with_rain);
            case "10d": return (R.drawable.sun_with_2cloud_littlerain);
            case "10n": return (R.drawable.moon_drizzle_01);
            case "11d":
            case "11n": return (R.drawable.clouds_with_lighting);
            case "13d":
            case "13n": return (R.drawable.clouds_with_littlesnow);
            case "50d": return (R.drawable.sun_haze_01);
            case "50n": return (R.drawable.moon_haze_01);
            default: return (R.drawable.unknown);
        }
    }

    public String getWindLetter() {
        if ((this.windDirection >= 0.0) && (this.windDirection <= 11.25)
                || (this.windDirection > 348.75) && (this.windDirection <=360.0)) {
            return "E";
        }
        if ((this.windDirection > 11.25) && (this.windDirection <= 33.75)) {
            return "NEE";
        }
        if ((this.windDirection > 33.75) && (this.windDirection <= 56.25)) {
            return "NE";
        }
        if ((this.windDirection > 56.25) && (this.windDirection <= 78.75)) {
            return "NNE";
        }
        if ((this.windDirection > 78.75) && (this.windDirection <= 101.25)) {
            return "N";
        }
        if ((this.windDirection > 101.25) && (this.windDirection <= 123.75)) {
            return "NNW";
        }
        if ((this.windDirection > 123.75) && (this.windDirection <= 146.25)) {
            return "NW";
        }
        if ((this.windDirection > 146.25) && (this.windDirection <= 168.75)) {
            return "NWW";
        }
        if ((this.windDirection > 168.75) && (this.windDirection <= 191.25)) {
            return "W";
        }
        if ((this.windDirection > 191.25) && (this.windDirection <= 213.75)) {
            return "SWW";
        }
        if ((this.windDirection > 213.75) && (this.windDirection <= 236.25)) {
            return "SW";
        }
        if ((this.windDirection > 236.25) && (this.windDirection <= 258.75)) {
            return "SSW";
        }
        if ((this.windDirection > 258.75) && (this.windDirection <= 281.25)) {
            return "S";
        }
        if ((this.windDirection > 281.25) && (this.windDirection <= 303.75)) {
            return "SSE";
        }
        if ((this.windDirection > 303.75) && (this.windDirection <= 326.25)) {
            return "SE";
        }
        if ((this.windDirection > 326.25) && (this.windDirection <= 348.75)) {
            return "SEE";
        }
        return "";
    }

}
