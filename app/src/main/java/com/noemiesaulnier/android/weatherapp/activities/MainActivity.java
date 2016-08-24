package com.noemiesaulnier.android.weatherapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.noemiesaulnier.android.weatherapp.R;
import com.noemiesaulnier.android.weatherapp.models.WeatherData;
import com.noemiesaulnier.android.weatherapp.tools.Util;

public class MainActivity extends AppCompatActivity {

    private Handler mHandler;
    private Intent intentMap;

    LocationManager locManager;
    LocationListener locListener;


    private Double longitude;
    private Double latitude;

    // used to allow MapActivity
    private boolean hasCoord = false;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                if(hasCoord)
                    startActivity(intentMap);
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();

        // Hide Layouts except progressBar
        showInformation(false);

        // Get Location of device
        callForLocation();


    }

    /**
     * Click method to open FavoritesActivity
     * @param view
     */
    public void openFavori(View view) {
        Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
        startActivity(intent);

    }


    /**
     * Geolocation with the device
     * First, we check permission
     * Then we use a Manager with a Provider and a Listener
     */
    public void callForLocation() {


        // Acquire a reference to the system Location Manager
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        locListener = new MyLocationListener();

        // Check Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // if not, we show the request dialog
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Util.MY_PERMISSIONS_REQUEST_GEOLOC);

        } else {
            // if permission is given
            // Register the listener with the Location Manager to receive location updates
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
        }
    }

    /**
     * LocationListener to obtain Latitude and Longitude;
     * After that, it can search and display weather informations
     */
    private class MyLocationListener implements LocationListener {

        // this method is awake when the provider get coords
        @Override
        public void onLocationChanged(Location location) {

            latitude = location.getLatitude();
            longitude = location.getLongitude();
            hasCoord = true;

            // Hide progressBar and display Layouts
            showInformation(true);

            // Launch methods which are waiting for coord (MapActivity and creation of WeatherData object)
            startActivityWithGPS();


            // now we can stop the listener ; only one call is needed
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locManager.removeUpdates(locListener);
                return;
            }


        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        /**
         * When Pprovider (Network, wifi) is disabled
         * display dialog to ask for activation
         * @param s
         */
        @Override
        public void onProviderDisabled(String s) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(getString(R.string.dialog_main_activate_gps))
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {

                            // if user is OK, parameters window is opening
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();

                            // if user is disagree, display warning message
                            Toast.makeText(MainActivity.this, getString(R.string.toast_main_error_gps), Toast.LENGTH_SHORT).show();

                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();

        }
    }

    /**
     * Once we have location, we can share coords with MapActivity
     * and display current weather by creating a WeatherData object
     */
    private void startActivityWithGPS() {
        intentMap = new Intent(MainActivity.this, MapActivity.class);
        intentMap.putExtra(Util.LAT_KEY, String.valueOf(latitude));
        intentMap.putExtra(Util.LNG_KEY, String.valueOf(longitude));

        createWeatherData(latitude, longitude);

    }


    /**
     * Create a WeatherData object with coords
     * Display informations
     * @param lat
     * @param lng
     */
    private void createWeatherData(final Double lat, final Double lng) {

        new Thread() {
            public void run() {

                try {
                    final WeatherData dataFromMyPosition = new WeatherData(lat, lng);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            // display weather informations
                            updateUIPosition(dataFromMyPosition);
                            updateUIPrevision(dataFromMyPosition);
                        }
                    });


                } catch (Exception e) {

                    e.printStackTrace();
                }


            }

        }.start();

    }

    /**
     * Display current weather
     * @param data
     */
    private void updateUIPosition(WeatherData data) {

        TextView mTextViewCity = (TextView) this.findViewById(R.id.text_view_city);
        TextView mTextViewSummary = (TextView) this.findViewById(R.id.text_view_summary);
        TextView mTextViewTemp = (TextView) this.findViewById(R.id.text_view_temperature);
        ImageView mImageViewIcon = (ImageView) this.findViewById(R.id.image_view_icon);

        mTextViewSummary.setText(data.getSummary());
        mTextViewTemp.setText(String.valueOf(data.getTemperature()) + getString(R.string.degree));
        mTextViewCity.setText(data.getCity());

        mImageViewIcon.setImageResource(data.getIdIconWhite());

    }

    /**
     * Display weather forecast
     * @param data
     */
    private void updateUIPrevision(WeatherData data) {

        // Save all ids of items_forecasts in order to update them with a loop
        int idsItem[] = {R.id.item_forecast_1, R.id.item_forecast_2, R.id.item_forecast_3, R.id.item_forecast_4};

        for (int i = 0; i < idsItem.length; i++) {
            View view = findViewById(idsItem[i]);
            ((TextView) view.findViewById(R.id.text_view_forecast_day)).setText(getString(data.getForecastList().get(i).getDay()));
            ((ImageView) view.findViewById(R.id.image_view_forecast_icon)).setImageResource(data.getForecastList().get(i).getIdIconGrey());
            ((TextView) view.findViewById(R.id.text_view_forecast_temp_min)).setText(String.valueOf(data.getForecastList().get(i).getTempMin()) +  getString(R.string.degree));
            ((TextView) view.findViewById(R.id.text_view_forecast_temp_max)).setText(String.valueOf(data.getForecastList().get(i).getTempMax()) +  getString(R.string.degree));
        }

    }


    /**
     * Method called just after user has answered permission
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Util.MY_PERMISSIONS_REQUEST_GEOLOC: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    // search for coords
                    callForLocation();

                } else {

                    // permission denied
                    // display warning message
                    Toast.makeText(MainActivity.this, getString(R.string.toast_main_error_gps), Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    /**
     * Choose between show progressBar or show weather informations
     * @param show
     */
    private void showInformation(boolean show) {
        if(show) {
            Log.d("titi", "show information");
            findViewById(R.id.linear_layout_info).setVisibility(View.VISIBLE);
            findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
        } else {
            Log.d("titi", "hide information");
            findViewById(R.id.linear_layout_info).setVisibility(View.INVISIBLE);
            findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        }
    }

}
