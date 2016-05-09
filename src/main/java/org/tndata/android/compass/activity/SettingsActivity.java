package org.tndata.android.compass.activity;

import org.tndata.android.compass.R;
import org.tndata.android.compass.fragment.NotificationSettingsFragment;
import org.tndata.android.compass.fragment.SettingsFragment;
import org.tndata.android.compass.fragment.SettingsFragment.OnSettingsClickListener;
import org.tndata.android.compass.service.LogOutService;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;


/**
 * Activity that hosts the settings fragment and carries out the appropriate action
 * depending on the event triggered by the fragment.
 */
public class SettingsActivity extends AppCompatActivity implements OnSettingsClickListener{
    public static final int LOGGED_OUT_RESULT_CODE = 2200;

    private static final String TOS_URL = "https://app.tndata.org/terms/";
    private static final String PRIVACY_POLICY_URL = "https://app.tndata.org/privacy/";

    private Toolbar mToolbar;

    private boolean sub;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_toolbar);

        mToolbar = (Toolbar)findViewById(R.id.tool_bar);
        mToolbar.setTitle(R.string.action_settings);
        mToolbar.setNavigationIcon(R.drawable.ic_back_white_24dp);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Fragment fragment = new SettingsFragment();
        getFragmentManager().beginTransaction().add(R.id.base_content, fragment).commit();

        sub = false;
    }

    @Override
    protected void onResume(){
        super.onResume();
        mToolbar.getBackground().setAlpha(255);
    }

    @Override
    public void onBackPressed(){
        if (sub){
            getFragmentManager().popBackStack();
            sub = false;
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public void notifications(){
        getFragmentManager().beginTransaction()
                .replace(R.id.base_content, new NotificationSettingsFragment())
                .addToBackStack(null).commit();

        sub = true;
    }

    @Override
    public void logOut(){
        startService(new Intent(this, LogOutService.class));
        setResult(LOGGED_OUT_RESULT_CODE);
        finish();
    }

    @Override
    public void tos(){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(TOS_URL)));
    }

    @Override
    public void privacy(){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL)));
    }

    @Override
    public void sources(){
        startActivity(new Intent(this, SourcesActivity.class));
    }
}
