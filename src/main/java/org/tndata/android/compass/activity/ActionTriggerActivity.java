package org.tndata.android.compass.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;

import org.tndata.android.compass.R;
import org.tndata.android.compass.fragment.ActionTriggerFragment;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Goal;

public class ActionTriggerActivity extends ActionBarActivity implements
        ActionTriggerFragment.ActionTriggerFragmentListener {

    // TODO: A simple activity that lets users set their triggers
    // TODO: this should extend BaseTriggerActivity
    // - move time/recurrence pickers here
    // - launch this from the "Edit Notifications" popup in GoalDetailsActivity
    // - ensure we display default values for given Actions.
    // - implement the AddActionTriggerTaskListener interface to save updates.

    private Toolbar mToolbar;
    private Goal mGoal;
    private Action mAction;

    public void todo() {
        // TODO: implement anything the fragment needs.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);

        mGoal = (Goal) getIntent().getSerializableExtra("goal");
        mAction = (Action) getIntent().getSerializableExtra("action");

        setContentView(R.layout.activity_base);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle(mGoal.getTitle());
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Fragment fragment = ActionTriggerFragment.newInstance(mGoal, mAction);
        if (fragment != null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.base_content, fragment).commit();
        }
    }

}
