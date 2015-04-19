package org.tndata.android.grow.activity;

import org.tndata.android.grow.GrowApplication;
import org.tndata.android.grow.R;
import org.tndata.android.grow.fragment.SettingsFragment;
import org.tndata.android.grow.fragment.SettingsFragment.OnSettingsClickListener;
import org.tndata.android.grow.model.Category;
import org.tndata.android.grow.model.Goal;
import org.tndata.android.grow.util.Constants;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;

import java.util.ArrayList;

public class SettingsActivity extends ActionBarActivity implements
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
        ((GrowApplication) getApplication()).setCategories(new ArrayList<Category>());
        ((GrowApplication) getApplication()).setGoals(new ArrayList<Goal>());
        setResult(Constants.LOGGED_OUT_RESULT_CODE);
        finish();
    }
}
