package org.tndata.android.compass.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.task.ActionLoaderTask;
import org.tndata.android.compass.task.AddActionTask;
import org.tndata.android.compass.task.DeleteActionTask;
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
        ActionLoaderTask.ActionLoaderListener, AddActionTask.AddActionTaskListener,
        DeleteActionTask.DeleteActionTaskListener {

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
    private final String TAG = "ChooseActionsActivity";

    static class ActionViewHolder extends RecyclerView.ViewHolder {
        public ActionViewHolder(View itemView) {
            super(itemView);
            iconImageView = (ImageView) itemView
                    .findViewById(R.id.list_item_action_imageview);
            headerCardTextView = (TextView) itemView
                    .findViewById(R.id.list_item_action_header_textview);
            titleTextView = (TextView) itemView
                    .findViewById(R.id.list_item_action_title_textview);
            descriptionTextView = (TextView) itemView
                    .findViewById(R.id.list_item_action_description_textview);

            iconsWrapper = (RelativeLayout) itemView.findViewById(R.id.list_action_icons_wrapper);
            selectActionImageView = (ImageView) itemView.findViewById(
                    R.id.list_item_select_action_imageview);
            moreInfoImageView = (ImageView) itemView.findViewById(
                    R.id.list_item_action_info_imageview);
        }

        TextView titleTextView;
        TextView descriptionTextView;
        ImageView iconImageView;
        TextView headerCardTextView;

        RelativeLayout iconsWrapper;
        ImageView selectActionImageView;
        ImageView moreInfoImageView;
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
                final Action action = mActionList.get(i);
                final boolean action_is_selected = application.getActions().contains(action);

                if(i == 0 && action.getId() == 0) {

                    // Display the Header Card

                    ((ActionViewHolder) viewHolder).headerCardTextView.setText(action.getDescription());
                    ((ActionViewHolder) viewHolder).headerCardTextView.setVisibility(View.VISIBLE);
                    ((ActionViewHolder) viewHolder).descriptionTextView.setVisibility(View.GONE);
                    ((ActionViewHolder) viewHolder).titleTextView.setVisibility(View.GONE);
                    ((ActionViewHolder) viewHolder).iconImageView.setVisibility(View.GONE);
                    ((ActionViewHolder) viewHolder).iconsWrapper.setVisibility(View.GONE);
                } else {

                    // Handle all other cards

                    ((ActionViewHolder) viewHolder).titleTextView.setText(action.getTitle());
                    ((ActionViewHolder) viewHolder).descriptionTextView
                            .setText(action.getDescription());

                    if (mExpandedActions.contains(action)) {
                        ((ActionViewHolder) viewHolder).descriptionTextView.setVisibility(View
                                .VISIBLE);
                        ((ActionViewHolder) viewHolder).iconImageView.setVisibility(View.GONE);
                        ((ActionViewHolder) viewHolder).iconsWrapper.setVisibility(View.VISIBLE);
                    } else {
                        ((ActionViewHolder) viewHolder).descriptionTextView.setVisibility(View
                                .GONE);
                        ((ActionViewHolder) viewHolder).iconsWrapper.setVisibility(View.GONE);
                        ((ActionViewHolder) viewHolder).iconImageView.setVisibility(View.VISIBLE);
                    }
                    if (action.getIconUrl() != null
                            && !action.getIconUrl().isEmpty()) {
                        ImageCache.instance(getApplicationContext()).loadBitmap(
                                ((ActionViewHolder) viewHolder).iconImageView,
                                action.getIconUrl(), false);
                    }

                    if(action_is_selected) {
                        ((ActionViewHolder) viewHolder).selectActionImageView.setImageResource(
                                R.drawable.ic_blue_check_circle);
                    } else {
                        ((ActionViewHolder) viewHolder).selectActionImageView.setImageResource(
                                R.drawable.ic_blue_plus_circle);
                    }

                    // Set up a Click Listener for all other cards.
                    ((ActionViewHolder) viewHolder).moreInfoImageView.setOnClickListener(new View
                            .OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            moreInfoPressed(action);
                        }
                    });
                    ((ActionViewHolder) viewHolder).selectActionImageView.setOnClickListener(new View
                            .OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if(action_is_selected) {
                                deleteAction(action);
                            } else {
                                addAction(action);
                            }
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

    public void moreInfoPressed(Action action) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChooseActionsActivity.this);
            builder.setMessage(action.getMoreInfo()).setTitle(action.getTitle());
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

    public void addAction(Action action) {
        Toast.makeText(getApplicationContext(),
                getText(R.string.action_saving), Toast.LENGTH_SHORT).show();
        new AddActionTask(this, this, action).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void actionAdded(Action action) {
        Toast.makeText(getApplicationContext(),
                getString(R.string.action_added, action.getTitle()),
                Toast.LENGTH_SHORT).show();

        // Add to the application's collection
        application.addAction(action);
        mAdapter.notifyDataSetChanged();

        // launch trigger stuff
        Intent intent = new Intent(getApplicationContext(), ActionTriggerActivity.class);
        intent.putExtra("goal", mGoal);
        intent.putExtra("action", action);
        startActivity(intent);
    }

    public void deleteAction(Action action) {
        // Make sure we find the action that contains the user's mapping id.
        if(action.getMappingId() <= 0) {
            for(Action a : application.getActions()) {
                if(action.getId() == a.getId()) {
                    action.setMappingId(a.getMappingId());
                    break;
                }
            }
        }

        Log.e(TAG, "Deleting Action, id = " + action.getId() + ", useraction id = "
                + action.getMappingId() + ", " + action.getTitle());
        if(action.getMappingId() > 0) {

            String actionMappingId = String.valueOf(action.getMappingId());
            new DeleteActionTask(this, this, actionMappingId).executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR);

            // Remove from the application's collection
            application.removeAction(action);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void actionDeleted() {
        Toast.makeText(getApplicationContext(),
                getString(R.string.action_deleted), Toast.LENGTH_SHORT).show();
    }
}
