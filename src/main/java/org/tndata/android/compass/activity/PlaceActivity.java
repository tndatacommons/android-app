package org.tndata.android.compass.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;

import org.tndata.android.compass.R;


/**
 * Created by isma on 9/3/15.
 */
public class PlaceActivity extends AppCompatActivity{
    public static final String PLACE_KEY = "org.tndata.compass.Place";
    public static final String PLACE_RESULT_KEY = "org.tndata.compass.ResultPlace";

    private EditText mName;
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        Toolbar toolbar = (Toolbar)findViewById(R.id.place_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mName = (EditText)findViewById(R.id.place_name);
        mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.place_map)).getMap();
    }
}
