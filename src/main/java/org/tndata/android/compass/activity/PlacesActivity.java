package org.tndata.android.compass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.location.places.ui.PlacePicker;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.PlacesAdapter;
import org.tndata.android.compass.model.Place;

import java.util.ArrayList;


/**
 * Created by isma on 9/3/15.
 */
public class PlacesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private PlacesAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        Toolbar toolbar = (Toolbar)findViewById(R.id.places_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mAdapter = new PlacesAdapter(this, new ArrayList<Place>());

        ListView list = (ListView)findViewById(R.id.places_list);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_places, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.places_add){
            Intent add = new Intent(this, PlacePickerActivity.class);
            add.putExtra(PlaceActivity.EDIT_MODE_KEY, false);
            startActivityForResult(add, 0);
            /*int PLACE_PICKER_REQUEST = 1;
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try{
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            }
            catch (Exception e){
                e.printStackTrace();
            }*/
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK){
            Place place = (Place)data.getSerializableExtra(PlaceActivity.PLACE_RESULT_KEY);
            Log.d("PlacesActivity", place.toString());
            mAdapter.addPlace(place);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        Place place = mAdapter.getItem(position);
        Intent add = new Intent(this, PlaceActivity.class);
        add.putExtra(PlaceActivity.EDIT_MODE_KEY, true);
        add.putExtra(PlaceActivity.PLACE_KEY, place);
        startActivityForResult(add, 0);
    }
}
