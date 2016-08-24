package com.noemiesaulnier.android.weatherapp.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.noemiesaulnier.android.weatherapp.R;
import com.noemiesaulnier.android.weatherapp.adapters.FavoriAdapter;
import com.noemiesaulnier.android.weatherapp.models.WeatherData;
import com.noemiesaulnier.android.weatherapp.tools.Util;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {

    ArrayList<WeatherData> favorisList;
    ListView mListViewFavoris;

    private Handler mHandler;
    FavoriAdapter adapter;

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
        setContentView(R.layout.activity_favoris);
        mHandler = new Handler();

        // Install ActionBar with back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListViewFavoris = (ListView) findViewById(R.id.list_view_favoris);
        favorisList = new ArrayList<>();

        // Initialize listView (empty for now)
        adapter = new FavoriAdapter(FavoritesActivity.this, favorisList);
        mListViewFavoris.setAdapter(adapter);

        // show progressBar
        showInformation(false);

        // Get savings favorites
        getInFile();

        // Display dialog to suppress a favorite
        mListViewFavoris.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                // get which favorite is selected
                final WeatherData favoriSelected = favorisList.get(position);

                // construct the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(FavoritesActivity.this);
                builder.setMessage(getString(R.string.dialog_fav_suppress) + " " + favoriSelected.getCity() + " ?");
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // suppress the favorite from the ArrayList
                        favorisList.remove(favoriSelected);

                        // tell to adapter that the list view has changed
                        adapter.notifyDataSetChanged();

                        // rewrite the file in order to save suppression
                        saveInFile();

                    }
                });

                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // close the dialog
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });
    }


    /**
     * Add a city to favorites by showing AlertDialog with EditText
     * @param view
     */
    public void buttonSearch(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(FavoritesActivity.this);
        View dialogView = View.inflate(FavoritesActivity.this, R.layout.dialog_search, null);
        builder.setView(dialogView);


        final EditText mEditTextSearch = (EditText) dialogView.findViewById(R.id.edit_text_search);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // get the city given by the user
                final String city = mEditTextSearch.getText().toString();

                new Thread(){
                    public void run() {

                        // call API to get data about this city
                        JSONObject data;
                        try {
                            data = Util.callApiWeather(Util.URL_CURRENT + "q=" + city + Util.URL_METRIC +
                            Util.getUrlLang() + Util.APPIKEY);

                        } catch (Exception e) {
                            data = null;
                        }


                        final JSONObject finalData = data;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                            // transform these data to a WeatherData object
                            // add it to a ArrayList
                            // tell to the adapter about changes
                            try {
                                WeatherData favori = new WeatherData(finalData);
                                favorisList.add(favori);
                                adapter.notifyDataSetChanged();

                                // save the id of the city in a file to get it next time
                                saveInFile();

                            } catch (Exception e) {
                                // display an error message
                                Toast.makeText(FavoritesActivity.this, getString(R.string.toast_fav_request_error), Toast.LENGTH_SHORT).show();
                            }
                            }
                        });

                    }
                }.start();

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // close dialog
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    /**
     * Save id of cities in SharedPreferences
     */
    private void saveInFile(){

        // Concatenate all the ids
        String idS = "";
        for(int i=0; i<favorisList.size()-1; i++){
            idS = idS + (String.valueOf(favorisList.get(i).getIdCity()) + ",");
        }
        idS = idS + (String.valueOf(favorisList.get(favorisList.size()-1).getIdCity()));

        // Save all ids in SharedPreferences.
        SharedPreferences settings = getSharedPreferences(Util.PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("idCities", idS);
        editor.apply();

    }

    /**
     * Read SharedPreferences to det id of Cities and call the API
     */
    private void getInFile() {

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(Util.PREFS, MODE_PRIVATE);
        final String idCities = settings.getString("idCities", "");

        new Thread(){
            public void run(){
                try {

                    // get data for all cities
                    final JSONObject finalData = Util.callApiWeather(Util.URL_CURRENT_GROUP + "id=" + idCities + Util.URL_METRIC +
                            Util.getUrlLang() + Util.APPIKEY);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            // transform data into WeatherData object and add it to the ArrayList
                            groupDataToFavori(finalData);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    /**
     * Transform Json to WeatherData object
     * add it to the ArrayList
     * Tell to the adapter there is changes and show the listView
     * @param json
     */
    private void groupDataToFavori(JSONObject json) {

        try {

            JSONArray list = json.getJSONArray("list");

            for(int i=0; i<list.length(); i++){
                WeatherData favori = new WeatherData(list.getJSONObject(i));
                favorisList.add(favori);
            }

            // as the adapter was set before CallApi, we have to tell it there is new WetherData in the ArrayList
            adapter.notifyDataSetChanged();
            showInformation(true);

        } catch (Exception e) {
            // display an error message
            Toast.makeText(FavoritesActivity.this, getString(R.string.toast_fav_file_error), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Choose between show progressBar or show weather informations
     * @param show
     */
    private void showInformation(boolean show) {
        if(show) {
            findViewById(R.id.list_view_favoris).setVisibility(View.VISIBLE);
            findViewById(R.id.fav_progress_bar).setVisibility(View.INVISIBLE);
        } else {
            findViewById(R.id.list_view_favoris).setVisibility(View.INVISIBLE);
            findViewById(R.id.fav_progress_bar).setVisibility(View.VISIBLE);
        }
    }

}
