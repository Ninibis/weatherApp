package com.noemiesaulnier.android.weatherapp.models;

import com.noemiesaulnier.android.weatherapp.tools.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by noemiesaulnier on 28/07/16.
 */
public class WeatherData {

    private Double latitude;
    private Double longitude;

    private int idCity;

    // For current weather
    private JSONObject dataCurrentWeather;
    private String summary;
    private String city;
    private String country;
    private int temperature;
    private int idIconWhite;
    private int idIconGrey;


    // For weather forecast
    private ArrayList<Forecast> forecastList;


    /**
     * Construtor
     * @param lat
     * @param lng
     * @throws Exception if error in saving data. Should be catch by Activity
     */
    public WeatherData(Double lat, Double lng) throws Exception {

        this.latitude = lat;
        this.longitude = lng;

        this.forecastList = new ArrayList<>();
        this.dataCurrentWeather = new JSONObject();

        // call API and save json data for current weather
        this.dataCurrentWeather = callApiCurrentWeather();

        // save current weather data we need
        saveCurrentData(dataCurrentWeather);

        // call API for forecast
        JSONObject dataPrevision = callApiForecast();

        // for each day, save forecast data (in ArrayList)
        JSONArray arrayPrev = dataPrevision.getJSONArray("list");
        for (int i = 1; i<arrayPrev.length(); i++)
        {
            saveForecastData(arrayPrev.getJSONObject(i));
        }



    }


    /**
     * Constructor. Use by FavoritesActivity
     * @param data json from API
     * @throws Exception
     */
    public WeatherData(JSONObject data) throws Exception{

        // save json data for current weather
        this.dataCurrentWeather = data;

        // save only current weather data we need
        saveCurrentData(data);
    }


    /**
     * Call API to return JSON data about current weather
     * @return
     */
    private JSONObject callApiCurrentWeather()
    {
        try {
            return Util.callApiWeather(Util.URL_CURRENT + "lat=" + String.valueOf(latitude) + "&lon=" + String.valueOf(longitude)
                    + Util.getUrlLang() + Util.URL_METRIC + Util.APPIKEY );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * Call API to return JSON data about forecast
     * @return
     */
    private JSONObject callApiForecast() {

        try {
            return Util.callApiWeather(Util.URL_FORECAST_DAY + "lat=" + String.valueOf(latitude) + "&lon=" + String.valueOf(longitude)
                    + Util.getUrlLang() + Util.URL_METRIC + "&cnt=5" + Util.APPIKEY );
        } catch (Exception e) {
            return  null;
        }
    }


    /**
     * Create a Forecast object with json data and add it to the ArraysList of Forecasts
     * @param json
     */
    private void saveForecastData(JSONObject json) {

        Forecast prev = new Forecast(json);
        forecastList.add(prev);
    }


    /**
     * Pick current weather data we need from json and save them
     * @param json
     */
    private void saveCurrentData(JSONObject json) throws Exception {

        this.idCity = json.getInt("id");
        this.city = json.getString("name");
        this.country = json.getJSONObject("sys").getString("country");
        this.temperature =(int) json.getJSONObject("main").getDouble("temp");

        JSONObject weather = json.getJSONArray("weather").getJSONObject(0);
        WeatherData.this.summary = weather.getString("description");
        WeatherData.this.idIconWhite = Util.findIconWhite(weather.getInt("id"),
                json.getJSONObject("sys").getLong("sunrise"), json.getJSONObject("sys").getLong("sunset"));
        WeatherData.this.idIconGrey = Util.findIconGrey(weather.getInt("id"),
                json.getJSONObject("sys").getLong("sunrise"), json.getJSONObject("sys").getLong("sunset"));

    }


    // GETTERS

    public int getIdCity() {
        return idCity;
    }
    public String getSummary() {return summary;}
    public int getTemperature() { return temperature;}
    public String getCity() { return  city;}
    public String getCountry() {return country;}
    public int getIdIconWhite() {return idIconWhite;}
    public int getIdIconGrey() {return idIconGrey;}
    public ArrayList<Forecast> getForecastList() {return forecastList;}

}
