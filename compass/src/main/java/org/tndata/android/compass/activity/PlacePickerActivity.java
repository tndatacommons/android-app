package org.tndata.android.compass.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.PlacePickerAdapter;
import org.tndata.android.compass.model.Place;
import org.tndata.android.compass.model.UserPlace;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.SharedPreferencesManager;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * Allows the user to pick a spot in the map by typing a place in the form of an address,
 * the name of an establishment, or code.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class PlacePickerActivity
        extends AppCompatActivity
        implements
                GoogleApiClient.ConnectionCallbacks,
                GoogleApiClient.OnConnectionFailedListener,
                ResultCallback<PlaceBuffer>,
                AdapterView.OnItemClickListener,
                OnMapReadyCallback,
                HttpRequest.RequestCallback,
                DialogInterface.OnClickListener{

    //Data keys
    public static final String PLACE_KEY = "org.tndata.compass.Place";
    public static final String PLACE_RESULT_KEY = "org.tndata.compass.ResultPlace";

    private static final int LOCATION_PERMISSION_RC = 2;


    private UserPlace mPlace;

    private GoogleApiClient mGoogleApiClient;

    private AutoCompleteTextView mResults;
    private PlacePickerAdapter mAdapter;

    private FrameLayout mMapContainer;
    private GoogleMap mMap;
    private Marker mMarker;

    private MenuItem mSave;

    private AlertDialog mRationaleDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);

        //Retrieve the place, which may be null
        mPlace = getIntent().getParcelableExtra(PLACE_KEY);

        //Get and set the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.place_picker_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_white_24dp);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //Create and set the adapter
        mAdapter = new PlacePickerAdapter(this);
        mResults = (AutoCompleteTextView)findViewById(R.id.place_picker_results);
        mResults.setAdapter(mAdapter);
        mResults.setOnItemClickListener(this);

        //Retrieve the map
        mMapContainer = (FrameLayout)findViewById(R.id.place_picker_map_container);
        ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.place_picker_map))
                .getMapAsync(this);
        mMarker = null;

        //Initialise the api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //Check whether this is M or post M, we don't need to display permissions dialogs for pre M
        boolean postM = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;

        //The first time the user fires the place picker the location permission should be requested
        boolean firstTime = SharedPreferencesManager.isFirstLocationPermissionRequest(this);

        //Additionally, the permission should be requested if the user previously denied the request
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        boolean rationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);

        if (postM && (firstTime || rationale)){
            firePermissionRequest();
        }
        else{
            //Show the keyboard only if no place is being edited
            if (mPlace.getId() == -1){
                mResults.requestFocus();
                ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                        .toggleSoftInput(InputMethodManager.SHOW_FORCED,
                                InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        //Set the map and place a marker if a place was passed
        mMap = googleMap;
        if (mPlace.getId() != -1 && !(mPlace.isPrimary() && !mPlace.isSet())){
            onPlaceSelected(mPlace.getLocation());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_place_picker, menu);
        mSave = menu.findItem(R.id.place_picker_save);
        //If a place was passed, enable save
        if (mPlace != null){
            mSave.setEnabled(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.place_picker_save){
            if (mPlace.getId() == -1){
                HttpRequest.post(this, API.URL.postPutPlace(mPlace),
                        API.BODY.postPutPlace(mPlace));
            }
            else{
                HttpRequest.put(this, API.URL.postPutPlace(mPlace),
                        API.BODY.postPutPlace(mPlace));
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        try{
            //Set the id
            mPlace.setId(new JSONObject(result).getInt("id"));

            //Return the place
            Intent data = new Intent();
            data.putExtra(PLACE_RESULT_KEY, mPlace);
            setResult(RESULT_OK, data);
            finish();
        }
        catch (JSONException jx){
            Toast.makeText(this, R.string.places_save_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void onConnectionFailed(@NonNull  ConnectionResult connectionResult){
        //Unused
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        Log.d("PlacePicker", mAdapter.getItem(position));

        //Hide the keyboard
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(mResults.getWindowToken(), 0);

        //Make the api call to retrieve the place information
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, mAdapter.getPlaceId(position))
                .setResultCallback(this);
    }

    @Override
    public void onResult(@NonNull PlaceBuffer placeBuffer){
        if (placeBuffer.getStatus().isSuccess()){
            //If the place doesn't exist, create it
            if (mPlace == null){
                mPlace = new UserPlace(new Place(""), -1, 0, 0);
            }
            //Populate the place
            LatLng latLng = placeBuffer.get(0).getLatLng();
            mPlace.setLatitude(latLng.latitude);
            mPlace.setLongitude(latLng.longitude);
            onPlaceSelected(latLng);
        }
        placeBuffer.release();
    }

    /**
     * Makes the map visible, animates the camera towards the selected place, cleans up
     * the previously placed marker, places a new one in the selected place, and enables
     * the save button..
     *
     * @param position the position of the selected place.
     */
    private void onPlaceSelected(LatLng position){
        mMapContainer.setVisibility(View.VISIBLE);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
        if (mMarker != null){
            mMarker.remove();
        }
        mMarker = mMap.addMarker(new MarkerOptions().position(position));
        if (mSave != null){
            mSave.setEnabled(true);
        }
    }

    /**
     * Fires the permission rationale dialog or the permission request dialog.
     */
    private void firePermissionRequest(){
        if (mRationaleDialog == null){
            //Show the rationale dialog
            mRationaleDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.place_picker_location_rationale_title)
                    .setMessage(R.string.place_picker_location_rationale)
                    .setPositiveButton(R.string.place_picker_location_rationale_button, this)
                    .create();
            mRationaleDialog.show();
        }
        else{
            //Request permissions
            SharedPreferencesManager.locationPermissionRequested(this);
            String locationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
            ActivityCompat.requestPermissions(this, new String[]{locationPermission},
                    LOCATION_PERMISSION_RC);
            mRationaleDialog = null;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int i){
        if (dialog == mRationaleDialog){
            firePermissionRequest();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults){
        switch (requestCode){
            case LOCATION_PERMISSION_RC:
                //Show the keyboard only if no place is being edited
                if (mPlace.getId() == -1){
                    Log.d("PlacePickerActivity", "Permission result, place id is -1");
                    mResults.requestFocus();
                    //TODO this doesn't work, fix
                    ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                            .toggleSoftInput(InputMethodManager.SHOW_FORCED,
                                    InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
                break;
        }
    }
}
