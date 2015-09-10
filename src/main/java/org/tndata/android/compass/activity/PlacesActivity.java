package org.tndata.android.compass.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.PlacesAdapter;
import org.tndata.android.compass.model.Place;
import org.tndata.android.compass.task.PrimaryPlaceLoaderTask;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by isma on 9/3/15.
 */
public class PlacesActivity
        extends AppCompatActivity
        implements
                AdapterView.OnItemClickListener,
                PrimaryPlaceLoaderTask.PrimaryPlaceLoaderCallback,
                DialogInterface.OnShowListener,
                View.OnClickListener{

    private static final int PLACE_PICKER_REQUEST_CODE = 65485;

    private CompassApplication mApplication;

    private ProgressBar mProgress;
    private ListView mList;
    private MenuItem mAdd;

    private EditText mName;
    private AlertDialog mNameDialog;
    private boolean mEdition;

    private PlacesAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        mApplication = (CompassApplication)getApplication();

        Toolbar toolbar = (Toolbar)findViewById(R.id.places_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mProgress = (ProgressBar)findViewById(R.id.places_progress);
        mList = (ListView)findViewById(R.id.places_list);

        mName = new EditText(this);
        mName.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        mNameDialog = new AlertDialog.Builder(this)
                .setTitle("What's the name of the place?")
                .setView(mName)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Accept", null)
                .create();

        mNameDialog.setOnShowListener(this);

        new PrimaryPlaceLoaderTask(this).execute();
    }

    @Override
    public void onPlacesLoaded(List<Place> primaryPlaces){
        //In either case, the progress bar is hidden
        mProgress.setVisibility(View.GONE);
        //If the data couldn't be retrieved the user is notified
        if (primaryPlaces == null){
            Toast.makeText(this, "There was an error loading the data", Toast.LENGTH_SHORT).show();
        }
        //Otherwise, the list is loaded
        else{
            //Two lists are used to sort the list
            List<Place> places = new ArrayList<>();
            List<Place> userPlaces = new ArrayList<>();
            List<Place> currentPlaces = mApplication.getUserData().getPlaces();

            //The user places are added to the list in the appropriate order
            for (Place place:currentPlaces){
                //If the place is primary it is added at the head, otherwise it is added at the tail
                if (primaryPlaces.contains(place)){
                    place.setPrimary(true);
                    place.setSet(true);
                    places.add(place);
                    //The primary place is removed from the list to keep track of which ones have
                    //  been added already
                    primaryPlaces.remove(place);
                }
                else{
                    userPlaces.add(place);
                }
            }
            //The reminder of primary places need to be added to the list as well
            for (Place place:primaryPlaces){
                place.setSet(false);
                places.add(place);
            }
            //The list of user places are added to the final list
            places.addAll(userPlaces);

            mAdapter = new PlacesAdapter(this, places);
            mList.setAdapter(mAdapter);
            mList.setOnItemClickListener(this);
            mList.setVisibility(View.VISIBLE);
            mAdd.setEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_places, menu);
        mAdd = menu.findItem(R.id.places_add);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.places_add){
            beginAddProcess();
            return true;
        }
        return false;
    }

    private void beginAddProcess(){
        mEdition = false;
        mName.setText("");
        mNameDialog.show();
    }

    @Override
    public void onShow(DialogInterface dialog){
        mNameDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        String name = mName.getText().toString().trim();
        if (name.equals("")){
            Toast.makeText(this, "You must set a name!", Toast.LENGTH_SHORT).show();
        }
        else{
            boolean duplicate = false;
            for (Place place:mAdapter.getPlaces()){
                if (place.getName().equals(name)){
                    duplicate = true;
                    break;
                }
            }
            if (duplicate){
                Toast.makeText(this, "There is already a place with that name", Toast.LENGTH_SHORT).show();
            }
            else{
                mNameDialog.dismiss();
                if (mEdition){
                    //savePlace();
                }
                else{
                    firePlacePicker();
                }
            }
        }
    }

    private void firePlacePicker(){
        startActivityForResult(new Intent(this, PlacePickerActivity.class), PLACE_PICKER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK){
            if (requestCode == PLACE_PICKER_REQUEST_CODE){
                Place place = (Place)data.getSerializableExtra(PlacePickerActivity.PLACE_RESULT_KEY);
                Log.d("PlacesActivity", place.toString());
                mAdapter.addPlace(place);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        Place place = mAdapter.getItem(position);
        Intent add = new Intent(this, PlacePickerActivity.class);
        add.putExtra(PlacePickerActivity.PLACE_KEY, place);
        startActivityForResult(add, PLACE_PICKER_REQUEST_CODE);
    }
}
