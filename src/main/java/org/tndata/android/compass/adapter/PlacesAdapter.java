package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Place;

import java.util.List;


/**
 * Created by isma on 9/3/15.
 */
public class PlacesAdapter extends BaseAdapter{
    private Context mContext;
    private List<Place> mPlaces;


    public PlacesAdapter(@NonNull Context context, @NonNull List<Place> places){
        mContext = context;
        mPlaces = places;
    }

    @Override
    public int getCount(){
        return mPlaces.size();
    }

    @Override
    public Object getItem(int position){
        return mPlaces.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_place, parent, false);
        }

        ((TextView)convertView.findViewById(R.id.item_place_name)).setText(getItem(position).toString());

        return convertView;
    }

    public void addPlace(Place place){
        mPlaces.add(place);
        notifyDataSetChanged();
    }
}
