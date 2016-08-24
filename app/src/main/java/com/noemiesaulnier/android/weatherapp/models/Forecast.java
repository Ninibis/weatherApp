package com.noemiesaulnier.android.weatherapp.models;


import com.noemiesaulnier.android.weatherapp.R;
import com.noemiesaulnier.android.weatherapp.tools.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by noemiesaulnier on 22/08/16.
 */
public class Forecast {

    private int dayId;
    private int tempMin;
    private int tempMax;
    private int idIconGrey;

    public Forecast(JSONObject json) {

        try {
            this.dayId = findDay(json.getInt("dt"));

            this.idIconGrey = Util.findIconGrey(json.getJSONArray("weather").getJSONObject(0).getInt("id"));

            JSONObject mainPrev = json.getJSONObject("temp");
            this.tempMin = (int) mainPrev.getDouble("min");
            this.tempMax = (int) mainPrev.getDouble("max");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Determine which day corresponds to forecast data
     * @param unixTime
     * @return
     */
    private int findDay(int unixTime) {

        Date date = new Date ();
        date.setTime((long) unixTime * 1000);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int numDay = cal.get(Calendar.DAY_OF_WEEK);

        switch (numDay) {
            case Calendar.MONDAY:
                return R.string.monday;

            case Calendar.TUESDAY:
                return R.string.tuesday;

            case Calendar.WEDNESDAY:
                return R.string.wednesday;

            case Calendar.THURSDAY:
                return R.string.thursday;

            case Calendar.FRIDAY:
                return R.string.friday;

            case Calendar.SATURDAY:
                return R.string.saturday;

            case Calendar.SUNDAY:
                return R.string.sunday;
            default:
                return 0;
        }

    }

    // GETTERS

    public int getDay() {return dayId;}

    public int getTempMin() {
        return tempMin;
    }

    public int getTempMax() {
        return tempMax;
    }

    public int getIdIconGrey() {
        return idIconGrey;
    }

}
