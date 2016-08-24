package com.noemiesaulnier.android.weatherapp.tools;

import android.util.Log;

import com.noemiesaulnier.android.weatherapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by noemiesaulnier on 28/07/16.
 */
public class Util {

    public static final String APPIKEY = "&APPID=22195588bf1b39f950ad0b464f931877";
    public static final String URL_CURRENT = "http://api.openweathermap.org/data/2.5/weather?";
    public static final String URL_CURRENT_GROUP = "http://api.openweathermap.org/data/2.5/group?";
    public static final String URL_FORECAST_DAY = "http://api.openweathermap.org/data/2.5/forecast/daily?";
    public static final String URL_METRIC = "&units=metric";

    public static final String LAT_KEY = "lat_key";
    public static final String LNG_KEY = "lng_key";

    public static final String PREFS = "MyPrefsFile";


    public static final int MY_PERMISSIONS_REQUEST_GEOLOC = 10;


    /**
     * Call API to get JsonObject of data
     * @param urlstr
     * @return
     */
    public static JSONObject callApiWeather(String urlstr) throws Exception
    {
        JSONObject data = null;

        // connection with web service
        URL url = new URL(urlstr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // get the response send by serveur trought getInputStream
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream()
        ));

        // read line by line
        String response = "";
        String line = "";
        while ((line = reader.readLine()) != null) {
            response = response + line;
        }
        reader.close();

        // the string is transformed in Json
        data = new JSONObject(response);

        if(data.has("cod")) {
            // if the json has a status cod != 200, it failed; so we return null
            if(data.getString("cod").compareTo("200") != 0) {
                return null;
            }
        }


        return data;

    }

    /**
     * Decide which language between english and french we want the data from API
     * @return
     */
    public static String getUrlLang() {
        Log.d("lang", "---> " + Locale.getDefault().getDisplayLanguage());
        if(Locale.getDefault().getDisplayLanguage().compareTo("français") == 0) {
            return "&lang=fr";
        }

        return "";
    }

    /**
     * Get Id of image resource for white weather icon
     * @param codeIcon from Json
     * @return
     */
    public static int findIconWhite(int codeIcon, long sunrise, long sunset){

        int idImage = 0;

        if(codeIcon < 300){
            idImage = R.drawable.weather_thunder_white;
        } else if(codeIcon < 400){
            idImage = R.drawable.weather_drizzle_white;
        } else if(codeIcon < 600){
            idImage = R.drawable.weather_rainy_white;
        } else if(codeIcon < 700){
            idImage = R.drawable.weather_snowy_white;

        } else if(codeIcon == 800){
            // clear sky. Night or day ?
            long currentTime = new Date().getTime()/1000;
            if (currentTime >= sunrise && currentTime < sunset) {
                idImage = R.drawable.weather_sunny_white;
            } else {
                idImage = R.drawable.weather_clear_night_white;
            }

        } else if(codeIcon < 900){
            idImage = R.drawable.weather_cloudy_white;
        } else if(codeIcon < 950){
            idImage = R.drawable.weather_thunder_white;
        }
        return idImage;
    }


    /**
     * Get Id of image resource for grey weather icon
     * whith night possibility
     * @param codeIcon from Json
     * @return
     */
    public static int findIconGrey(int codeIcon, long sunrise, long sunset){

        int idImage = 0;

        if(codeIcon < 300){
            idImage = R.drawable.weather_thunder_grey;
        } else if(codeIcon < 400){
            idImage = R.drawable.weather_drizzle_grey;
        } else if(codeIcon < 600){
            idImage = R.drawable.weather_rainy_grey;
        } else if(codeIcon < 700){
            idImage = R.drawable.weather_snowy_grey;
        } else if(codeIcon == 800){
            // clear sky. Night or day ?
            long currentTime = new Date().getTime()/1000;
            if (currentTime >= sunrise && currentTime < sunset) {
                idImage = R.drawable.weather_sunny_grey;
            } else {
                idImage = R.drawable.weather_clear_night_grey;
            }
        } else if(codeIcon < 900){
            idImage = R.drawable.weather_cloudy_grey;
        } else if(codeIcon < 950){
            idImage = R.drawable.weather_thunder_grey;
        }
        return idImage;
    }

    /**
     * Get Id of image resource for grey weather icon
     * No night icon
     * @param codeIcon from Json
     * @return
     */
    public static int findIconGrey(int codeIcon){

        int idImage = 0;

        if(codeIcon < 300){
            idImage = R.drawable.weather_thunder_grey;
        } else if(codeIcon < 400){
            idImage = R.drawable.weather_drizzle_grey;
        } else if(codeIcon < 600){
            idImage = R.drawable.weather_rainy_grey;
        } else if(codeIcon < 700){
            idImage = R.drawable.weather_snowy_grey;
        } else if(codeIcon == 800){
            idImage = R.drawable.weather_sunny_grey;
        } else if(codeIcon < 900){
            idImage = R.drawable.weather_cloudy_grey;
        } else if(codeIcon < 950){
            idImage = R.drawable.weather_thunder_grey;
        }
        return idImage;
    }


}
