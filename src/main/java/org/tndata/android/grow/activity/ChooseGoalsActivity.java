package org.tndata.android.grow.activity;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.tndata.android.grow.GrowApplication;
import org.tndata.android.grow.R;
import org.tndata.android.grow.model.Category;
import org.tndata.android.grow.model.Goal;
import org.tndata.android.grow.task.AddGoalTask;
import org.tndata.android.grow.task.GoalLoaderTask;
import org.tndata.android.grow.ui.parallaxrecyclerview.HeaderLayoutManagerFixed;
import org.tndata.android.grow.ui.parallaxrecyclerview.ParallaxRecyclerAdapter;
import org.tndata.android.grow.util.ImageCache;

import java.util.ArrayList;

public class ChooseGoalsActivity extends ActionBarActivity implements AddGoalTask.AddGoalsTaskListener,
        GoalLoaderTask.GoalLoaderListener {
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private TextView mHeaderTextView;
    private TextView mErrorTextView;
    private View mFakeHeader;
    private ImageView mHeaderImageView;
    private Button mDoneButton;
    private ArrayList<Goal> mItems;
    private ArrayList<Goal> mSelectedGoals = new ArrayList<Goal>();
    private ParallaxRecyclerAdapter<Goal> mAdapter;
    private Category mCategory = null;

    static class ChooseGoalViewHolder extends RecyclerView.ViewHolder {
        public ChooseGoalViewHolder(View itemView) {
            super(itemView);
            iconImageView = (ImageView) itemView
                    .findViewById(R.id.list_item_choose_goal_imageview);
            titleTextView = (TextView) itemView
                    .findViewById(R.id.list_item_choose_goal_title_textview);
            checkImageView = (ImageView) itemView
                    .findViewById(R.id.list_item_choose_goal_selected_imageview);
            selectButton = (Button) itemView
                    .findViewById(R.id.list_item_choose_goal_select_button);
            moreInfoButton = (Button) itemView
                    .findViewById(R.id.list_item_choose_goal_more_info_button);
        }

        TextView titleTextView;
        ImageView iconImageView;
        ImageView checkImageView;
        Button selectButton;
        Button moreInfoButton;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_goals);

        mCategory = (Category) getIntent().getSerializableExtra("category");

        mToolbar = (Toolbar) findViewById(R.id.choose_goals_toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.choose_goals_recyclerview);
        HeaderLayoutManagerFixed manager = new HeaderLayoutManagerFixed(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        mErrorTextView = (TextView) findViewById(R.id.choose_goals_error_textview);

//        mDoneButton = (Button) findViewById(R.id.choose_goals_done_button);
//        mDoneButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                goalsSelected(mSelectedGoals);
//            }
//        });
//        mDoneButton.setVisibility(View.GONE);
        mItems = new ArrayList<Goal>();
        mAdapter = new ParallaxRecyclerAdapter<>(mItems);
        mAdapter.implementRecyclerAdapterMethods(new ParallaxRecyclerAdapter.RecyclerAdapterMethods() {
            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder,
                                         int i) {
                final Goal goal = mItems.get(i);

                ((ChooseGoalViewHolder) viewHolder).titleTextView.setText(goal
                        .getTitle());
                if (goal.getIconUrl() != null
                        && !goal.getIconUrl().isEmpty()) {
                    ImageCache.instance(getApplicationContext()).loadBitmap(
                            ((ChooseGoalViewHolder) viewHolder).iconImageView,
                            goal.getIconUrl(), false);
                } else {
                    ((ChooseGoalViewHolder) viewHolder).iconImageView
                            .setImageResource(R.drawable.default_image);
                }
                if (mSelectedGoals.contains(goal)) {
                    ((ChooseGoalViewHolder) viewHolder).checkImageView.setVisibility(View.VISIBLE);
                    ((ChooseGoalViewHolder) viewHolder).selectButton.setVisibility(View.INVISIBLE);
                } else {
                    ((ChooseGoalViewHolder) viewHolder).checkImageView.setVisibility(View.INVISIBLE);
                    ((ChooseGoalViewHolder) viewHolder).selectButton.setVisibility(View.VISIBLE);
                }
                ((ChooseGoalViewHolder) viewHolder).selectButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        goalSelected(goal);
                    }
                });
                ((ChooseGoalViewHolder) viewHolder).checkImageView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        goalSelected(goal);
                    }
                });
                ((ChooseGoalViewHolder) viewHolder).moreInfoButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        moreInfoPressed(goal);
                    }
                });
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(
                    ViewGroup viewGroup, int i) {
                return new ChooseGoalViewHolder(getLayoutInflater().inflate(
                        R.layout.list_item_choose_goal, viewGroup, false));
            }

            @Override
            public int getItemCount() {
                return mItems.size();
            }
        });

        mFakeHeader = getLayoutInflater().inflate(R.layout.header_choose_goals,
                mRecyclerView, false);
        mHeaderTextView = (TextView) mFakeHeader.findViewById(R.id.choose_goals_header_label_textview);
        mHeaderImageView = (ImageView) findViewById(R.id.choose_goals_material_imageview);
        manager.setHeaderIncrementFixer(mFakeHeader);
        mAdapter.setShouldClipView(false);
        mAdapter.setParallaxHeader(mFakeHeader, mRecyclerView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mAdapter.setOnParallaxScroll(new ParallaxRecyclerAdapter.OnParallaxScroll() {
                @SuppressLint("NewApi")
                @Override
                public void onParallaxScroll(float percentage, float offset,
                                             View parallax) {
//TODO fix the toolbar alpha
                    Drawable c = mToolbar.getBackground();
                    c.setAlpha(Math.round(percentage * 255));
                    mToolbar.setBackground(c);
                    mHeaderImageView.setTranslationY(-offset * 0.5f);

                }
            });
        }
        mRecyclerView.setAdapter(mAdapter);

        if (mCategory.getIconUrl() != null && !mCategory.getIconUrl().isEmpty()) {
            ImageCache.instance(getApplicationContext()).loadBitmap(
                    mHeaderImageView, mCategory.getIconUrl(), false);
        }

        if (mCategory.getGoals() != null) {
            mSelectedGoals.addAll(mCategory.getGoals());
        }

        loadGoals();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) { // Back key pressed
            goalsSelected(mSelectedGoals);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goalsSelected(mSelectedGoals);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showError() {
        mRecyclerView.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    @SuppressLint("DefaultLocale")
    private void loadGoals() {
        if (mCategory == null) {
            return;
        } else if (!mCategory.getTitle().isEmpty()) {
            mHeaderTextView.setText(getString(
                    R.string.choose_goals_header_label,
                    mCategory.getTitle().toUpperCase()));
        }
        new GoalLoaderTask(getApplicationContext(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                ((GrowApplication) getApplication()).getToken(),
                String.valueOf(mCategory.getId()));
    }

    @Override
    public void goalsAdded(ArrayList<Goal> goals) {
        if (goals != null) {
            ArrayList<Goal> allGoals = ((GrowApplication) getApplication()).getGoals();
            allGoals.addAll(goals);
            ((GrowApplication) getApplication()).setGoals(allGoals);
        }
        finish();
    }

    public void goalSelected(Goal goal) {

        if (mSelectedGoals.contains(goal)) {
            mSelectedGoals.remove(goal);
        } else {
            mSelectedGoals.add(goal);
        }
        mAdapter.notifyDataSetChanged();
    }

    public void moreInfoPressed(Goal goal) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChooseGoalsActivity.this);

            builder.setMessage(goal.getDescription()).setTitle(goal.getTitle());
            builder.setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void goalsSelected(ArrayList<Goal> goals) {
        Log.e("GOALS", "GOALS SELECTED");
        //Lets just save the new ones...
        ArrayList<Goal> goalsToDelete = new ArrayList<Goal>();
        ArrayList<Goal> goalsToAdd = new ArrayList<Goal>();
        for (Goal goal : mCategory.getGoals()) {
            Log.d("SHOULD DELETE?", goal.getTitle());
            if (!goals.contains(goal)) {
                Log.d("Delete Goal", goal.getTitle());
                goalsToDelete.add(goal);
            }
        }
        for (Goal goal : goals) {
            Log.d("SHOULD ADD?", goal.getTitle());
            if (!mCategory.getGoals().contains(goal)) {
                Log.d("Add Goal", goal.getTitle());
                goalsToAdd.add(goal);
            }
        }

        // TODO delete goals, then remove from application

        ArrayList<String> goalList = new ArrayList<String>();
        for (Goal goal : goalsToAdd) {
            goalList.add(String.valueOf(goal.getId()));
        }
        if (goalList.size() > 0) {
            new AddGoalTask(this, this, goalList)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            finish();
        }
    }

    @Override
    public void goalLoaderFinished(ArrayList<Goal> goals) {
        if (goals != null && !goals.isEmpty()) {
            mItems.addAll(goals);
            mAdapter.notifyDataSetChanged();
        } else {
            showError();
        }
    }
}
