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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.task.ActionLoaderTask;
import org.tndata.android.compass.ui.SpacingItemDecoration;
import org.tndata.android.compass.ui.parallaxrecyclerview.HeaderLayoutManagerFixed;
import org.tndata.android.compass.ui.parallaxrecyclerview.ParallaxRecyclerAdapter;
import org.tndata.android.compass.ui.parallaxrecyclerview.ParallaxRecyclerAdapter.OnClickEvent;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.ImageCache;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * The ChooseActionsActivity is where a user selects Actions for a chosen Behavior.
 * 
 */
public class ChooseActionsActivity extends ActionBarActivity implements
        ActionLoaderTask.ActionLoaderListener {

    private Toolbar mToolbar;
    private Category mCategory = null;
    private Goal mGoal;
    private Behavior mBehavior;
    private ArrayList<Action> mActionList;  // Array of Actions from which the user can Choose.
    private ParallaxRecyclerAdapter<Action> mAdapter;
    private RecyclerView mRecyclerView;
    private View mFakeHeader;
    private View mHeaderView;
    private HashSet<Action> mExpandedActions = new HashSet<>();
    private int mCurrentlyExpandedPosition = -1;
    private CompassApplication application;

    static class ActionViewHolder extends RecyclerView.ViewHolder {
        public ActionViewHolder(View itemView) {
            // TODO: update layouts
            // todo: -  activity_choose_actions
            // todo: -  list_item_choose_actions
            // todo: -  header_choose_actions
            super(itemView);
            iconImageView = (ImageView) itemView
                    .findViewById(R.id.list_item_action_imageview);
            headerCardTextView = (TextView) itemView
                    .findViewById(R.id.list_item_action_header_textview);
            titleTextView = (TextView) itemView
                    .findViewById(R.id.list_item_action_title_textview);
            descriptionTextView = (TextView) itemView
                    .findViewById(R.id.list_item_action_description_textview);
            tryItTextView = (TextView) itemView.findViewById(R.id.list_item_action_try_it_textview);
        }

        TextView titleTextView;
        TextView descriptionTextView;
        TextView tryItTextView;
        ImageView iconImageView;
        TextView headerCardTextView;
    }

    private Action createHeaderObject() {
        // NOTE: We want a single _Header Card_ for each collection; It'll contain the
        // parent's description (in this case the Behavior), but so the card can be created
        // with he rest of the collection, we'll construct an Action object with only
        // a description.
        Action headerAction = new Action();
        headerAction.setDescription(mBehavior.getDescription());
        headerAction.setId(0); // it's not a real object, so it doesn't have a real ID.

        return headerAction;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_actions);

        application = (CompassApplication) getApplication();
        mBehavior = (Behavior) getIntent().getSerializableExtra("behavior");
        mGoal = (Goal) getIntent().getSerializableExtra("goal");
        mCategory = (Category) getIntent().getSerializableExtra("category");

        mToolbar = (Toolbar) findViewById(R.id.choose_actions_toolbar);
        mToolbar.setTitle(mBehavior.getTitle());
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.choose_actions_recyclerview);
        HeaderLayoutManagerFixed manager = new HeaderLayoutManagerFixed(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(new SpacingItemDecoration(30));
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);

        mActionList = new ArrayList<Action>();
        mActionList.add(0, createHeaderObject());

        mAdapter = new ParallaxRecyclerAdapter<>(mActionList);
        mAdapter.implementRecyclerAdapterMethods(new ParallaxRecyclerAdapter
                .RecyclerAdapterMethods() {
            @Override
            public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder,
                                         final int i) {
                Action action = mActionList.get(i);

                if(i == 0 && action.getId() == 0) {

                    // Display the Header Card

                    ((ActionViewHolder) viewHolder).headerCardTextView.setText(action.getDescription());
                    ((ActionViewHolder) viewHolder).headerCardTextView.setVisibility(View.VISIBLE);
                    ((ActionViewHolder) viewHolder).descriptionTextView.setVisibility(View.GONE);
                    ((ActionViewHolder) viewHolder).titleTextView.setVisibility(View.GONE);
                    ((ActionViewHolder) viewHolder).iconImageView.setVisibility(View.GONE);
                } else {

                    // Handle all other cards

                    ((ActionViewHolder) viewHolder).titleTextView.setText(action.getTitle());
                    ((ActionViewHolder) viewHolder).descriptionTextView
                            .setText(action.getDescription());

                    if (mExpandedActions.contains(action)) {
                        ((ActionViewHolder) viewHolder).descriptionTextView.setVisibility(View
                                .VISIBLE);
                        ((ActionViewHolder) viewHolder).tryItTextView.setVisibility(View.VISIBLE);
                        ((ActionViewHolder) viewHolder).iconImageView.setVisibility(View.GONE);
                    } else {
                        ((ActionViewHolder) viewHolder).descriptionTextView.setVisibility(View
                                .GONE);
                        ((ActionViewHolder) viewHolder).tryItTextView.setVisibility(View.GONE);
                        ((ActionViewHolder) viewHolder).iconImageView.setVisibility(View.VISIBLE);
                    }
                    if (action.getIconUrl() != null
                            && !action.getIconUrl().isEmpty()) {
                        ImageCache.instance(getApplicationContext()).loadBitmap(
                                ((ActionViewHolder) viewHolder).iconImageView,
                                action.getIconUrl(), false);
                    }

                    // Set up a Click Listener for all other cards.
                    ((ActionViewHolder) viewHolder).tryItTextView.setOnClickListener(new View
                            .OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            // TODO: What's the Action's CTA Info? The ActionTriggerActivity.
                            Toast.makeText(getApplicationContext(),
                                    "Coming Soon, Setting Reminder", Toast.LENGTH_SHORT);

//                            Intent intent = new Intent(getApplicationContext(),
//                                    BehaviorActivity.class);
//                            intent.putExtra("behavior", mBehaviorList.get(i));
//                            intent.putExtra("goal", mGoal);
//                            intent.putExtra("category", mCategory);
//                            startActivityForResult(intent, Constants.VIEW_BEHAVIOR_REQUEST_CODE);
                        }
                    });
                }
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(
                    ViewGroup viewGroup, int i) {
                return new ActionViewHolder(getLayoutInflater().inflate(
                        R.layout.list_item_choose_action, viewGroup, false));
            }

            @Override
            public int getItemCount() {
                return mActionList.size();
            }
        });

        // -----------------------------------------------------
        mFakeHeader = getLayoutInflater().inflate(
                R.layout.header_choose_actions, mRecyclerView, false);
        ImageView goalIconView = (ImageView) mFakeHeader.findViewById(R.id.choose_actions_header_imageview);
        mBehavior.loadIconIntoView(getApplicationContext(), goalIconView);
        RelativeLayout circleView = (RelativeLayout) mFakeHeader.findViewById(R.id.choose_actions_header_circle_view);
        GradientDrawable gradientDrawable = (GradientDrawable) circleView.getBackground();
        if (!mCategory.getSecondaryColor().isEmpty()) {
            gradientDrawable.setColor(Color.parseColor(mCategory.getSecondaryColor()));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            circleView.setBackground(gradientDrawable);
        } else {
            circleView.setBackgroundDrawable(gradientDrawable);
        }

        mHeaderView = findViewById(R.id.choose_actions_material_view);
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
                if (position <= 0) {
                    // This is the header or header card, so ignore.
                    // This fixes a bug when clicking a description
                    return;
                }
                Action action = mActionList.get(position);

                if (mExpandedActions.contains(action)) {
                    mExpandedActions.remove(action);
                } else {
                    mExpandedActions.clear();
                    if (mCurrentlyExpandedPosition >= 0) {
                        mAdapter.notifyItemChanged(mCurrentlyExpandedPosition);
                    }
                    mExpandedActions.add(action);
                }
                try {
                    // let us redraw the item that has changed, this forces the RecyclerView to
                    // respect the layout of each item, and none will overlap. Add 1 to position
                    // to account for the header view
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
        loadActions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.VIEW_BEHAVIOR_REQUEST_CODE) {
            setResult(resultCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadActions() {
        new ActionLoaderTask(this).executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR,
                application.getToken(),
                String.valueOf(mBehavior.getId()));
    }

    @Override
    public void actionsLoaded(ArrayList<Action> actions) {
        if (actions != null && !actions.isEmpty()) {
            mActionList.addAll(actions);
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
