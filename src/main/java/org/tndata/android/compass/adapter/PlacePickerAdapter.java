package org.tndata.android.compass.adapter;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.tndata.android.compass.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by isma on 9/8/15.
 */
public class PlacePickerAdapter
        extends BaseAdapter
        implements Filterable, ResultCallback<AutocompletePredictionBuffer>{

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;

    private List<GooglePlace> mPlaces;

    private int mQueryLength;


    public PlacePickerAdapter(Context context){
        mContext = context;
        mPlaces = new ArrayList<>();
        mQueryLength = 0;
    }

    @Override
    public int getCount(){
        return mPlaces.size();
    }

    @Override
    public String getItem(int position){
        return mPlaces.get(position).mDescription;
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    public String getPlaceId(int position){
        return mPlaces.get(position).mPlaceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder;

        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_auto_place, parent, false);
            holder.mPlace = (TextView)convertView.findViewById(R.id.place_auto_name);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.mPlace.setText(getItem(position));

        return convertView;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient){
        this.mGoogleApiClient = googleApiClient;
    }

    @Override
    public Filter getFilter(){
        return new Filter(){
            @Override
            protected FilterResults performFiltering(CharSequence constraint){
                if (constraint.length() < mQueryLength){
                    if (mPlaces.size() != 0){
                        ((Activity)mContext).runOnUiThread(new Runnable(){
                            @Override
                            public void run(){
                                mPlaces.clear();
                                notifyDataSetChanged();
                            }
                        });
                    }
                }
                else{
                    if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()){
                        Toast.makeText(mContext, "Not connected", Toast.LENGTH_SHORT).show();
                        return null;
                    }

                    Log.d("PlacePicker", constraint.toString());
                    displayPredictiveResults(constraint.toString());
                }
                mQueryLength = constraint.length();
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results){
                //Unused
            }
        };
    }

    private void displayPredictiveResults(String query){
        LatLngBounds bounds;
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null){
            //Southwest corner to Northeast corner.
            bounds = new LatLngBounds(new LatLng(-90, -180), new LatLng(90, 180));
            Log.d("PlacePicker", "No location");
        }
        else{
            LatLng sw = new LatLng(location.getLatitude()-0.2, location.getLongitude()-0.2);
            LatLng ne = new LatLng(location.getLatitude()+0.2, location.getLongitude()+0.2);
            bounds = new LatLngBounds(sw, ne);
            Log.d("PlacePicker", location.toString());
        }

        Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, query, bounds, null)
                .setResultCallback(this, 60, TimeUnit.SECONDS);
    }

    @Override
    public void onResult(final AutocompletePredictionBuffer autocompletePredictions){
        ((Activity)mContext).runOnUiThread(new Runnable(){
            @Override
            public void run(){
                if (autocompletePredictions == null){
                    return;
                }

                if (autocompletePredictions.getStatus().isSuccess()){
                    mPlaces.clear();
                    for (AutocompletePrediction prediction:autocompletePredictions){
                        //Add as a new item to avoid IllegalArgumentsException when buffer is released
                        mPlaces.add(new GooglePlace(prediction.getPlaceId(), prediction.getDescription()));
                    }
                    notifyDataSetChanged();
                }

                //Prevent memory leak by releasing buffer
                autocompletePredictions.release();
            }
        });
    }


    private class GooglePlace{
        private String mPlaceId;
        private String mDescription;


        private GooglePlace(String placeId, String description){
            mPlaceId = placeId;
            mDescription = description;
        }
    }


    private class ViewHolder{
        private TextView mPlace;
    }
}
