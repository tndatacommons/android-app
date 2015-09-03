package org.tndata.android.compass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Place;


/**
 * Created by isma on 9/3/15.
 */
public class PlaceActivity extends AppCompatActivity{
    public static final String EDIT_MODE_KEY = "org.tndata.compass.EditMode";
    public static final String PLACE_KEY = "org.tndata.compass.Place";
    public static final String PLACE_RESULT_KEY = "org.tndata.compass.ResultPlace";

    private EditText mName;
    private GoogleMap mMap;

    private MenuItem mActionItem;

    private boolean mEditMode;
    private Place mPlace;


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

        mEditMode = getIntent().getBooleanExtra(EDIT_MODE_KEY, false);
        if (mEditMode){
            mPlace = (Place)getIntent().getSerializableExtra(PLACE_KEY);

            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(mPlace.getLocation(), 16);
            mMap.animateCamera(update);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_place, menu);
        mActionItem = menu.findItem(R.id.place_action);
        if (mEditMode){
            mActionItem.setTitle("Edit place");
            mActionItem.setTitleCondensed("Edit");
            mActionItem.setIcon(R.drawable.ic_edit);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item == mActionItem && mEditMode){
            mActionItem.setTitle("Save place");
            mActionItem.setTitleCondensed("Save");
            mActionItem.setIcon(R.drawable.ic_save);
            mEditMode = false;
        }
        else if (item == mActionItem && !mEditMode){
            mPlace = new Place();
            mPlace.setName(mName.getText().toString().trim());
            mPlace.setLatitude(mMap.getCameraPosition().target.latitude);
            mPlace.setLongitude(mMap.getCameraPosition().target.longitude);
            Intent save = new Intent();
            save.putExtra(PLACE_RESULT_KEY, mPlace);
            setResult(RESULT_OK, save);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
