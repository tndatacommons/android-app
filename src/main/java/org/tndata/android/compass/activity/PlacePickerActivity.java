package org.tndata.android.compass.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.PlacePickerAdapter;


/**
 * Created by isma on 9/8/15.
 */
public class PlacePickerActivity
        extends AppCompatActivity
        implements
                GoogleApiClient.ConnectionCallbacks,
                GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient mGoogleApiClient;

    private AutoCompleteTextView mResults;
    private PlacePickerAdapter mAdapter;


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

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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
}
