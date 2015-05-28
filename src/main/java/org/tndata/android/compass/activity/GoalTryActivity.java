package org.tndata.android.compass.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
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

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.task.BehaviorLoaderTask;
import org.tndata.android.compass.task.BehaviorLoaderTask.BehaviorLoaderListener;
import org.tndata.android.compass.ui.SpacingItemDecoration;
import org.tndata.android.compass.ui.parallaxrecyclerview.HeaderLayoutManagerFixed;
import org.tndata.android.compass.ui.parallaxrecyclerview.ParallaxRecyclerAdapter;
import org.tndata.android.compass.ui.parallaxrecyclerview.ParallaxRecyclerAdapter.OnClickEvent;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.ImageCache;

import java.util.ArrayList;
import java.util.HashSet;

public class GoalTryActivity extends ActionBarActivity implements
        BehaviorLoaderListener {
    private Toolbar mToolbar;
    private Goal mGoal;
    private ArrayList<Behavior> mBehaviorList;
    private ParallaxRecyclerAdapter<Behavior> mAdapter;
    private RecyclerView mRecyclerView;
    private View mFakeHeader;
    private View mHeaderView;
    private Category mCategory = null;
    private HashSet<Behavior> mExpandedBehaviors = new HashSet<>();

    static class TryGoalViewHolder extends RecyclerView.ViewHolder {
        public TryGoalViewHolder(View itemView) {
            super(itemView);
            iconImageView = (ImageView) itemView
                    .findViewById(R.id.list_item_behavior_imageview);
            titleTextView = (TextView) itemView
                    .findViewById(R.id.list_item_behavior_title_textview);
            descriptionTextView = (TextView) itemView
                    .findViewById(R.id.list_item_behavior_description_textview);
            tryIt = (Button) itemView.findViewById(R.id.list_item_behavior_try_it_button);
        }

        TextView titleTextView;
        TextView descriptionTextView;
        Button tryIt;
        ImageView iconImageView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_try);

        mGoal = (Goal) getIntent().getSerializableExtra("goal");
        Log.d("mGoal?", "id:" + mGoal.getId() + " title:" + mGoal.getTitle());
        mCategory = (Category) getIntent().getSerializableExtra("category");

        mToolbar = (Toolbar) findViewById(R.id.goal_try_toolbar);
        mToolbar.setTitle(mGoal.getTitle());
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.goal_try_recyclerview);
        HeaderLayoutManagerFixed manager = new HeaderLayoutManagerFixed(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(new SpacingItemDecoration(30));
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);

        mBehaviorList = new ArrayList<Behavior>();
        mAdapter = new ParallaxRecyclerAdapter<>(mBehaviorList);
        mAdapter.implementRecyclerAdapterMethods(new ParallaxRecyclerAdapter
                .RecyclerAdapterMethods() {
            @Override
            public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder,
                                         final int i) {
                Behavior behavior = mBehaviorList.get(i);

                ((TryGoalViewHolder) viewHolder).titleTextView.setText(behavior
                        .getTitle());
                ((TryGoalViewHolder) viewHolder).descriptionTextView
                        .setText(behavior.getDescription());
                if (mExpandedBehaviors.contains(behavior)) {
                    ((TryGoalViewHolder) viewHolder).descriptionTextView.setVisibility(View
                            .VISIBLE);
                    ((TryGoalViewHolder) viewHolder).tryIt.setVisibility(View.VISIBLE);
                    ((TryGoalViewHolder) viewHolder).iconImageView.setVisibility(View.GONE);
                } else {
                    ((TryGoalViewHolder) viewHolder).descriptionTextView.setVisibility(View
                            .GONE);
                    ((TryGoalViewHolder) viewHolder).tryIt.setVisibility(View.GONE);
                    ((TryGoalViewHolder) viewHolder).iconImageView.setVisibility(View.VISIBLE);
                }
                if (behavior.getIconUrl() != null
                        && !behavior.getIconUrl().isEmpty()) {
                    ImageCache.instance(getApplicationContext()).loadBitmap(
                            ((TryGoalViewHolder) viewHolder).iconImageView,
                            behavior.getIconUrl(), false);
                }

                ((TryGoalViewHolder) viewHolder).tryIt.setOnClickListener(new View
                        .OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),
                                BehaviorActivity.class);
                        intent.putExtra("behavior", mBehaviorList.get(i));
                        intent.putExtra("goal", mGoal);
                        intent.putExtra("category", mCategory);
                        startActivityForResult(intent, Constants.VIEW_BEHAVIOR_REQUEST_CODE);
                    }
                });

            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(
                    ViewGroup viewGroup, int i) {
                return new TryGoalViewHolder(getLayoutInflater().inflate(
                        R.layout.list_item_behavior, viewGroup, false));
            }

            @Override
            public int getItemCount() {
                return mBehaviorList.size();
            }
        });

        mFakeHeader = getLayoutInflater().inflate(R.layout.header_try_goal,
                mRecyclerView, false);
        TextView goalDescription = (TextView) mFakeHeader.findViewById(R.id.goal_try_label);
        goalDescription.setText(mGoal.getDescription());
        mHeaderView = findViewById(R.id.goal_try_material_view);
        manager.setHeaderIncrementFixer(mFakeHeader);
        mAdapter.setShouldClipView(false);
        mAdapter.setParallaxHeader(mFakeHeader, mRecyclerView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mAdapter.setOnParallaxScroll(new ParallaxRecyclerAdapter.OnParallaxScroll() {
                @SuppressLint("NewApi")
                @Override
                public void onParallaxScroll(float percentage, float offset,
                                             View parallax) {
                    Drawable c = mToolbar.getBackground();
                    c.setAlpha(Math.round(percentage * 255));
                    mToolbar.setBackground(c);
                    mHeaderView.setTranslationY(-offset * 0.5f);

                }
            });
        }
        mAdapter.setOnClickEvent(new OnClickEvent() {

            @Override
            public void onClick(View v, int position) {
                //lets get semantic
                Behavior behavior = mBehaviorList.get(position);

                if (mExpandedBehaviors.contains(behavior)) {
                    collapseCardView(v);
                    mExpandedBehaviors.remove(behavior);
                } else {
                    expandCardView(v);
                    mExpandedBehaviors.add(behavior);
                }
            }

        });
        mRecyclerView.setAdapter(mAdapter);
        if (mCategory != null && !mCategory.getColor().isEmpty()) {
            mHeaderView.setBackgroundColor(Color.parseColor(mCategory.getColor()));
            mToolbar.setBackgroundColor(Color.parseColor(mCategory.getColor()));
        }
        loadBehaviors();
    }



    private void expandCardView(View card) {
        card.findViewById(R.id.list_item_behavior_try_it_button).setVisibility(View.VISIBLE);
        card.findViewById(R.id.list_item_behavior_description_textview).setVisibility(View.VISIBLE);
        card.findViewById(R.id.list_item_behavior_imageview).setVisibility(View.GONE);
    }

    private void collapseCardView(View card) {
        card.findViewById(R.id.list_item_behavior_try_it_button).setVisibility(View.GONE);
        card.findViewById(R.id.list_item_behavior_description_textview).setVisibility(View.GONE);
        card.findViewById(R.id.list_item_behavior_imageview).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.VIEW_BEHAVIOR_REQUEST_CODE) {
            setResult(resultCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadBehaviors() {
        new BehaviorLoaderTask(this).executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR,
                ((CompassApplication) getApplication()).getToken(),
                String.valueOf(mGoal.getId()));
    }

    @Override
    public void behaviorsLoaded(ArrayList<Behavior> behaviors) {
        if (behaviors != null) {
            mBehaviorList.addAll(behaviors);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) { // Back key pressed
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
