package org.tndata.android.compass.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.PlacePickerAdapter;


/**
 * Created by isma on 9/8/15.
 */
public class PlacePickerActivity
        extends AppCompatActivity
        implements
                GoogleApiClient.ConnectionCallbacks,
                GoogleApiClient.OnConnectionFailedListener,
                AdapterView.OnItemClickListener{

    private GoogleApiClient mGoogleApiClient;

    private AutoCompleteTextView mResults;
    private PlacePickerAdapter mAdapter;

    private FrameLayout mMapContainer;
    private GoogleMap mMap;
    private Marker mMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);

        //Get and set the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.place_picker_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mAdapter = new PlacePickerAdapter(this);
        mResults = (AutoCompleteTextView)findViewById(R.id.place_picker_results);
        mResults.setAdapter(mAdapter);
        mResults.setOnItemClickListener(this);

        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        mMapContainer = (FrameLayout)findViewById(R.id.place_picker_map_container);
        mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.place_picker_map)).getMap();
        mMarker = null;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_place_picker, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart(){
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop(){
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()){
            mAdapter.setGoogleApiClient(null);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle){
        if (mAdapter != null){
            mAdapter.setGoogleApiClient(mGoogleApiClient);
        }
    }

    @Override
    public void onConnectionSuspended(int i){
        //Unused
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){
        //Unused
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        Log.d("PlacePicker", mAdapter.getItem(position));

        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(mResults.getWindowToken(), 0);

        Places.GeoDataApi.getPlaceById(mGoogleApiClient, mAdapter.getPlaceId(position))
                .setResultCallback(new ResultCallback<PlaceBuffer>(){
                    @Override
                    public void onResult(PlaceBuffer places){
                        if (places.getStatus().isSuccess()){
                            final Place myPlace = places.get(0);
                            Log.d("PlacePicker", "Place found: " + myPlace.getName() + ", " + myPlace.getLatLng());
                            mMapContainer.setVisibility(View.VISIBLE);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPlace.getLatLng(), 16));
                            if (mMarker != null){
                                mMarker.remove();
                            }
                            mMarker = mMap.addMarker(new MarkerOptions().position(myPlace.getLatLng()));
                        }
                        places.release();
                    }
                });
    }
}
