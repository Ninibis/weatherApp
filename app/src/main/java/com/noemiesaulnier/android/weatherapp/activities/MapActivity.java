package com.noemiesaulnier.android.weatherapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.noemiesaulnier.android.weatherapp.R;
import com.noemiesaulnier.android.weatherapp.tools.Util;

public class MapActivity extends AppCompatActivity {

    private GoogleMap mGoogleMap;
    private LatLng latLng;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case (android.R.id.home):
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Install ActionBar with back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Fetch coords given by MainActivity
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            Double latitude = Double.parseDouble(extras.getString(Util.LAT_KEY));
            Double longitude = Double.parseDouble(extras.getString(Util.LNG_KEY));

            // Save coords
            latLng = new LatLng(latitude, longitude);

        }

        // display google map
        createMap();

    }

    /**
     * Create a Google map
     * with zoom and marker
     */
    private void createMap() {
        try {
            mGoogleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.
                    map_view)).getMap();
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.map_here)));

            if (mGoogleMap == null) {
                Toast.makeText(this, getString(R.string.toast_map_error), Toast.LENGTH_SHORT).show();
            }

        } catch (NullPointerException exception) {
            Toast.makeText(this, getString(R.string.toast_map_error), Toast.LENGTH_SHORT).show();
        }

    }
}
