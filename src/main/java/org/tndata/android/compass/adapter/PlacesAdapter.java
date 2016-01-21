package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.UserPlace;

import java.util.List;


/**
 * Displays the list of UserPlaces.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class PlacesAdapter extends BaseAdapter{
    private Context mContext;
    private List<UserPlace> mPlaces;


    public PlacesAdapter(@NonNull Context context, @NonNull List<UserPlace> places){
        mContext = context;
        mPlaces = places;
    }

    @Override
    public int getCount(){
        return mPlaces.size();
    }

    @Override
    public UserPlace getItem(int position){
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

        ((TextView)convertView.findViewById(R.id.item_place_name)).setText(getItem(position).getDisplayString());

        return convertView;
    }

    public void addPlace(UserPlace place){
        mPlaces.add(place);
        notifyDataSetChanged();
    }

    public List<UserPlace> getPlaces(){
        return mPlaces;
    }
}
