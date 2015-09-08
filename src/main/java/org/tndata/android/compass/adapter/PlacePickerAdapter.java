package org.tndata.android.compass.adapter;

import android.content.Context;
import android.location.Location;
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
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
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
public class PlacePickerAdapter extends BaseAdapter implements Filterable{
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;

    private List<String> mAddresses;


    public PlacePickerAdapter(Context context){
        mContext = context;
        mAddresses = new ArrayList<>();
    }

    @Override
    public int getCount(){
        return mAddresses.size();
    }

    @Override
    public String getItem(int position){
        return mAddresses.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder;

        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_auto_place, parent, false);
            holder.text = (TextView)convertView.findViewById(R.id.place_auto_name);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.text.setText(getItem(position));

        return convertView;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient){
        this.mGoogleApiClient = googleApiClient;
    }

    private class ViewHolder{
        private TextView text;
    }

    @Override
    public Filter getFilter(){
        return new Filter(){
            @Override
            protected FilterResults performFiltering(CharSequence constraint){

                if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()){
                    Toast.makeText(mContext, "Not connected", Toast.LENGTH_SHORT).show();
                    return null;
                }

                mAddresses.clear();
                notifyDataSetChanged();

                displayPredictiveResults(constraint.toString());

                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results){
                notifyDataSetChanged();
            }
        };
    }

    private void displayPredictiveResults(String query){
        LatLngBounds bounds;
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null){
            //Southwest corner to Northeast corner.
            bounds = new LatLngBounds(new LatLng(-90, -180), new LatLng(90, 180));
        }
        else{
            LatLng sw = new LatLng(location.getLatitude()-0.2, location.getLongitude()-0.2);
            LatLng ne = new LatLng(location.getLatitude()+0.2, location.getLongitude()+0.2);
            bounds = new LatLngBounds(sw, ne);
        }

        //Filter: https://developers.google.com/places/supported_types#table3
        List<Integer> filterTypes = new ArrayList<>();
        filterTypes.add(Place.TYPE_ESTABLISHMENT);

        Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, query, bounds, AutocompleteFilter.create(filterTypes))
                .setResultCallback(
                        new ResultCallback<AutocompletePredictionBuffer>(){
                            @Override
                            public void onResult(AutocompletePredictionBuffer buffer){

                                if (buffer == null){
                                    return;
                                }

                                if (buffer.getStatus().isSuccess()){
                                    for (AutocompletePrediction prediction : buffer){
                                        //Add as a new item to avoid IllegalArgumentsException when buffer is released
                                        mAddresses.add(prediction.getDescription());
                                    }
                                    notifyDataSetChanged();
                                }

                                //Prevent memory leak by releasing buffer
                                buffer.release();
                            }
                        }, 60, TimeUnit.SECONDS);
    }
}
