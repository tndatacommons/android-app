package org.tndata.android.compass.activity;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.fragment.SettingsFragment;
import org.tndata.android.compass.fragment.SettingsFragment.OnSettingsClickListener;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.service.LogOutService;
import org.tndata.android.compass.util.Constants;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;


/**
 * Activity that hosts the settings fragment and carries out the appropriate action
 * depending on the event triggered by the fragment.
 */
public class SettingsActivity extends AppCompatActivity implements OnSettingsClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        toolbar.setTitle(R.string.action_settings);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Fragment fragment = new SettingsFragment();
        getFragmentManager().beginTransaction().replace(R.id.base_content, fragment).commit();
    }

    @Override
    public void logOut(){
        startService(new Intent(this, LogOutService.class));

        ((CompassApplication)getApplication()).setUserData(new UserData());
        setResult(Constants.LOGGED_OUT_RESULT_CODE);
        finish();
    }

    @Override
    public void sources(){
        startActivity(new Intent(this, SourcesActivity.class));
    }
}
