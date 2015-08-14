package org.tndata.android.compass.activity;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.fragment.SettingsFragment;
import org.tndata.android.compass.fragment.SettingsFragment.OnSettingsClickListener;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.util.Constants;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;

public class SettingsActivity extends AppCompatActivity implements
        OnSettingsClickListener {
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle(R.string.action_settings);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Fragment fragment = new SettingsFragment();
        if (fragment != null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.base_content, fragment).commit();
        }
    }

    @Override
    public void logOut() {
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("auth_token", "");
        editor.putString("first_name", "");
        editor.putString("last_name", "");
        editor.putString("email", "");
        editor.putString("username", "");
        editor.putString("password", "");
        editor.putInt("id", -1);
        editor.commit();

        ((CompassApplication)getApplication()).setUserData(new UserData());
        setResult(Constants.LOGGED_OUT_RESULT_CODE);
        finish();
    }
}
