package org.tndata.android.grow.activity;

import java.util.ArrayList;

import org.tndata.android.grow.R;
import org.tndata.android.grow.fragment.BehaviorFragment;
import org.tndata.android.grow.fragment.BehaviorFragment.BehaviorFragmentListener;
import org.tndata.android.grow.fragment.LearnMoreFragment;
import org.tndata.android.grow.fragment.LearnMoreFragment.LearnMoreFragmentListener;
import org.tndata.android.grow.model.Action;
import org.tndata.android.grow.model.Behavior;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;

public class BehaviorActivity extends ActionBarActivity implements
        BehaviorFragmentListener, LearnMoreFragmentListener {
    private static final int BEHAVIOR = 0;
    private static final int LEARN_MORE = 1;
    private Toolbar mToolbar;
    private Behavior mBehavior;
    private BehaviorFragment mBehaviorFragment = null;
    private LearnMoreFragment mLearnMoreFragment = null;
    private ArrayList<Fragment> mFragmentStack = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mBehavior = (Behavior) getIntent().getSerializableExtra("behavior");

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle(mBehavior.getTitle());
        mToolbar.getBackground().setAlpha(255);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        swapFragments(BEHAVIOR, true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) { // Back key pressed
            handleBackStack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            handleBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void addBehavior(Behavior behavior) {
        // TODO Auto-generated method stub

    }

    @Override
    public void learnMore() {
        swapFragments(LEARN_MORE, true);
    }

    @Override
    public void cancel() {
        handleBackStack();
    }

    @Override
    public void addAction(Action action) {
        // TODO Auto-generated method stub

    }

    private void handleBackStack() {
        if (!mFragmentStack.isEmpty()) {
            mFragmentStack.remove(mFragmentStack.size() - 1);
        }

        if (mFragmentStack.isEmpty()) {
            finish();
        } else {
            Fragment fragment = mFragmentStack.get(mFragmentStack.size() - 1);

            int index = BEHAVIOR;
            if (fragment instanceof LearnMoreFragment) {
                index = LEARN_MORE;
            }

            swapFragments(index, false);
        }
    }

    private void swapFragments(int index, boolean addToStack) {
        Fragment fragment = null;
        switch (index) {
        case BEHAVIOR:
            if (mBehaviorFragment == null) {
                mBehaviorFragment = BehaviorFragment.newInstance(mBehavior);
            }
            fragment = mBehaviorFragment;
            break;
        case LEARN_MORE:
            if (mLearnMoreFragment == null) {
                mLearnMoreFragment = LearnMoreFragment.newInstance(mBehavior);
            }
            fragment = mLearnMoreFragment;
            break;
        }
        if (fragment != null) {
            if (addToStack) {
                mFragmentStack.add(fragment);
            }
            getFragmentManager().beginTransaction()
                    .replace(R.id.base_content, fragment).commit();
        }
    }
}
