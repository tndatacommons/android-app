package org.tndata.android.grow.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.tndata.android.grow.GrowApplication;
import org.tndata.android.grow.R;
import org.tndata.android.grow.task.GetUserProfileTask;
import org.tndata.android.grow.task.GetUserProfileTask.UserProfileTaskInterface;

public class UserProfileActivity extends ActionBarActivity implements UserProfileTaskInterface {
    private Toolbar mToolbar;
    private ListView mListView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle(R.string.action_myself);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListView = (ListView) findViewById(R.id.myself_listview);
        mProgressBar = (ProgressBar) findViewById(R.id.myself_load_progress);

        loadUserProfile();
    }

    private void loadUserProfile() {
        mProgressBar.setVisibility(View.VISIBLE);
        new GetUserProfileTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                ((GrowApplication) getApplication()).getToken());
    }

    @Override
    public void userProfileFound() {
        mProgressBar.setVisibility(View.GONE);
    }

}
