package org.tndata.android.compass.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

/**
 * The GoalTryActivity is where a user selects Behaviors for a chosen Goal.
 * 
 */
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
    private int mCurrentlyExpandedPosition = -1;

    static class TryGoalViewHolder extends RecyclerView.ViewHolder {
        public TryGoalViewHolder(View itemView) {
            super(itemView);
            iconImageView = (ImageView) itemView
                    .findViewById(R.id.list_item_behavior_imageview);
            headerCardTextView = (TextView) itemView
                    .findViewById(R.id.list_item_behavior_header_textview);
            titleTextView = (TextView) itemView
                    .findViewById(R.id.list_item_behavior_title_textview);
            descriptionTextView = (TextView) itemView
                    .findViewById(R.id.list_item_behavior_description_textview);
            tryItTextView = (TextView) itemView.findViewById(R.id.list_item_behavior_try_it_textview);
        }

        TextView titleTextView;
        TextView descriptionTextView;
        TextView tryItTextView;
        ImageView iconImageView;
        TextView headerCardTextView;
    }

    private Behavior createHeaderObject() {
        // NOTE: We want a single _Header Card_ for each collection; It'll contain the
        // parent's description (in this case the goal), but so the card can be created
        // with he rest of the collection, we'll construct a Behavior object with only
        // a description.
        Behavior headerBehavior = new Behavior();
        headerBehavior.setDescription(mGoal.getDescription());
        headerBehavior.setId(0); // it's not a real object, so it doesn't have a real ID.

        return headerBehavior;
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
        mBehaviorList.add(0, createHeaderObject());

        mAdapter = new ParallaxRecyclerAdapter<>(mBehaviorList);
        mAdapter.implementRecyclerAdapterMethods(new ParallaxRecyclerAdapter
                .RecyclerAdapterMethods() {
            @Override
            public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder,
                                         final int i) {
                Behavior behavior = mBehaviorList.get(i);

                if(i == 0 && behavior.getId() == 0) {

                    // Display the Header Card

                    ((TryGoalViewHolder) viewHolder).headerCardTextView.setText(behavior.getDescription());
                    ((TryGoalViewHolder) viewHolder).headerCardTextView.setVisibility(View.VISIBLE);
                    ((TryGoalViewHolder) viewHolder).descriptionTextView.setVisibility(View.GONE);
                    ((TryGoalViewHolder) viewHolder).titleTextView.setVisibility(View.GONE);
                    ((TryGoalViewHolder) viewHolder).iconImageView.setVisibility(View.GONE);
                } else {

                    // Handle all other cards

                    ((TryGoalViewHolder) viewHolder).titleTextView.setText(behavior
                            .getTitle());
                    ((TryGoalViewHolder) viewHolder).descriptionTextView
                            .setText(behavior.getDescription());

                    if (mExpandedBehaviors.contains(behavior)) {
                        ((TryGoalViewHolder) viewHolder).descriptionTextView.setVisibility(View
                                .VISIBLE);
                        ((TryGoalViewHolder) viewHolder).tryItTextView.setVisibility(View.VISIBLE);
                        ((TryGoalViewHolder) viewHolder).iconImageView.setVisibility(View.GONE);
                    } else {
                        ((TryGoalViewHolder) viewHolder).descriptionTextView.setVisibility(View
                                .GONE);
                        ((TryGoalViewHolder) viewHolder).tryItTextView.setVisibility(View.GONE);
                        ((TryGoalViewHolder) viewHolder).iconImageView.setVisibility(View.VISIBLE);
                    }
                    if (behavior.getIconUrl() != null
                            && !behavior.getIconUrl().isEmpty()) {
                        ImageCache.instance(getApplicationContext()).loadBitmap(
                                ((TryGoalViewHolder) viewHolder).iconImageView,
                                behavior.getIconUrl(), false);
                    }
                    // Set up a Click Listener for all other cards.
                    ((TryGoalViewHolder) viewHolder).tryItTextView.setOnClickListener(new View
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
        ImageView goalIconView = (ImageView) mFakeHeader.findViewById(R.id.goal_try_header_imageview);
        mGoal.loadIconIntoView(getApplicationContext(), goalIconView);
        RelativeLayout circleView = (RelativeLayout) mFakeHeader.findViewById(R.id.goal_try_header_circle_view);
        GradientDrawable gradientDrawable = (GradientDrawable) circleView.getBackground();
        if (!mCategory.getSecondaryColor().isEmpty()) {
            gradientDrawable.setColor(Color.parseColor(mCategory.getSecondaryColor()));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            circleView.setBackground(gradientDrawable);
        } else {
            circleView.setBackgroundDrawable(gradientDrawable);
        }

        mHeaderView = findViewById(R.id.goal_try_material_view);
        manager.setHeaderIncrementFixer(mFakeHeader);
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
                if (position <= 0) {
                    // This is the header or header card, so ignore.
                    // This fixes a bug when clicking a description
                    return;
                }
                Behavior behavior = mBehaviorList.get(position);

                if (mExpandedBehaviors.contains(behavior)) {
                    mExpandedBehaviors.remove(behavior);
                } else {
                    mExpandedBehaviors.clear();
                    if (mCurrentlyExpandedPosition >= 0) {
                        mAdapter.notifyItemChanged(mCurrentlyExpandedPosition);
                    }
                    mExpandedBehaviors.add(behavior);
                }
                try {
                    // let us redraw the item that has changed, this forces the RecyclerView to
                    // respect
                    //  the layout of each item, and none will overlap. Add 1 to position to account
                    //  for the header view
                    mCurrentlyExpandedPosition = position + 1;
                    mAdapter.notifyItemChanged(mCurrentlyExpandedPosition);
                    mRecyclerView.scrollToPosition(mCurrentlyExpandedPosition);
                } catch (Exception e) {
                    e.printStackTrace();
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
