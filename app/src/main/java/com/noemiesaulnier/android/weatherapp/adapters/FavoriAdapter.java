package com.noemiesaulnier.android.weatherapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.noemiesaulnier.android.weatherapp.R;
import com.noemiesaulnier.android.weatherapp.models.WeatherData;

import java.util.ArrayList;

/**
 * Created by noemiesaulnier on 29/07/16.
 */
public class FavoriAdapter extends BaseAdapter {

    Context mContext;

    private ArrayList<WeatherData> favorisList;

    private LayoutInflater inflater;


    public FavoriAdapter(Context context, ArrayList<WeatherData> favoris) {
        this.mContext = context;
        this.favorisList = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
        this.favorisList = favoris;
    }

    @Override
    public int getCount() {
        return favorisList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewParent) {

        ViewHolder holder;

        if (convertView == null) {

            holder =  new ViewHolder();

            convertView =  inflater.inflate(R.layout.list_view_favori , null);

            holder.mTextViewCity = (TextView) convertView.findViewById(R.id.text_view_city );
            holder.mTextViewSummary = (TextView) convertView.findViewById(R.id.text_view_summary );
            holder.mTextViewTemp = (TextView) convertView.findViewById(R.id.text_view_temperature);
            holder.mImageViewIcon = (ImageView) convertView.findViewById(R.id.image_view_icon);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();

        }

        if (favorisList.size() != 0) {
            holder.mTextViewCity.setText(favorisList.get(i).getCity() + ", " + favorisList.get(i).getCountry());
            holder.mTextViewSummary.setText(favorisList.get(i).getSummary());
            holder.mTextViewTemp.setText(String.valueOf(favorisList.get(i).getTemperature())
                    + mContext.getString(R.string.degree));
            holder.mImageViewIcon.setImageResource(favorisList.get(i).getIdIconGrey());
        }
        return convertView;

    }

    private class ViewHolder {

        TextView mTextViewCity;
        TextView mTextViewSummary;
        TextView mTextViewTemp;
        ImageView mImageViewIcon;
    }

}
