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
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.task.AddBehaviorTask;
import org.tndata.android.compass.task.BehaviorLoaderTask;
import org.tndata.android.compass.task.BehaviorLoaderTask.BehaviorLoaderListener;
import org.tndata.android.compass.task.DeleteBehaviorTask;
import org.tndata.android.compass.ui.SpacingItemDecoration;
import org.tndata.android.compass.ui.parallaxrecyclerview.HeaderLayoutManagerFixed;
import org.tndata.android.compass.ui.parallaxrecyclerview.ParallaxRecyclerAdapter;
import org.tndata.android.compass.ui.parallaxrecyclerview.ParallaxRecyclerAdapter.OnClickEvent;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.ImageLoader;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * The GoalTryActivity is where a user selects Behaviors for a chosen Goal.
 * 
 */
public class GoalTryActivity extends ActionBarActivity implements
        BehaviorLoaderListener, AddBehaviorTask.AddBehaviorsTaskListener,
        DeleteBehaviorTask.DeleteBehaviorTaskListener {

    private Toolbar mToolbar;
    private Goal mGoal;
    private ArrayList<Behavior> mBehaviorList; // Array of Behaviors from which the user can choose
    private ParallaxRecyclerAdapter<Behavior> mAdapter;
    private RecyclerView mRecyclerView;
    private View mFakeHeader;
    private View mHeaderView;
    private Category mCategory = null;
    private HashSet<Behavior> mExpandedBehaviors = new HashSet<>();
    private int mCurrentlyExpandedPosition = -1;
    public CompassApplication application;

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

            iconsWrapper = (RelativeLayout) itemView.findViewById(R.id.list_icons_wrapper);
            tryItImageView = (ImageView) itemView.findViewById(R.id.list_item_behavior_try_it_imageview);
            selectActionsImageView = (ImageView) itemView.findViewById(R.id.list_item_select_action_imageview);
            moreInfoImageView = (ImageView) itemView.findViewById(R.id.list_item_behavior_info_imageview);
        }

        TextView titleTextView;
        TextView descriptionTextView;
        ImageView iconImageView;
        TextView headerCardTextView;

        RelativeLayout iconsWrapper;
        ImageView tryItImageView;
        ImageView selectActionsImageView;
        ImageView moreInfoImageView;
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

        application = (CompassApplication) getApplication();

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
                final Behavior behavior = mBehaviorList.get(i);
                final boolean behavior_is_selected = application.getBehaviors().contains(behavior);

                if(i == 0 && behavior.getId() == 0) {

                    // Display the Header Card

                    ((TryGoalViewHolder) viewHolder).headerCardTextView.setText(behavior.getDescription());
                    ((TryGoalViewHolder) viewHolder).headerCardTextView.setVisibility(View.VISIBLE);
                    ((TryGoalViewHolder) viewHolder).descriptionTextView.setVisibility(View.GONE);
                    ((TryGoalViewHolder) viewHolder).titleTextView.setVisibility(View.GONE);
                    ((TryGoalViewHolder) viewHolder).iconImageView.setVisibility(View.GONE);
                    ((TryGoalViewHolder) viewHolder).iconsWrapper.setVisibility(View.GONE);
                } else {

                    // Handle all other cards

                    ((TryGoalViewHolder) viewHolder).titleTextView.setText(behavior
                            .getTitle());
                    ((TryGoalViewHolder) viewHolder).descriptionTextView
                            .setText(behavior.getDescription());

                    if (mExpandedBehaviors.contains(behavior)) {
                        ((TryGoalViewHolder) viewHolder).descriptionTextView.setVisibility(View
                                .VISIBLE);
                        ((TryGoalViewHolder) viewHolder).iconsWrapper.setVisibility(View.VISIBLE);
                        ((TryGoalViewHolder) viewHolder).iconImageView.setVisibility(View.GONE);
                    } else {
                        ((TryGoalViewHolder) viewHolder).descriptionTextView.setVisibility(View
                                .GONE);
                        ((TryGoalViewHolder) viewHolder).iconsWrapper.setVisibility(View.GONE);
                        ((TryGoalViewHolder) viewHolder).iconImageView.setVisibility(View.VISIBLE);
                    }
                    if (behavior.getIconUrl() != null
                            && !behavior.getIconUrl().isEmpty()) {
                        ImageLoader.loadBitmap(((TryGoalViewHolder)viewHolder).iconImageView,
                                behavior.getIconUrl(), false);
                    }

                    if(behavior_is_selected) {
                        // If the user has already selected the behavior, update the icon
                        ((TryGoalViewHolder) viewHolder).tryItImageView.setImageResource(
                                R.drawable.ic_blue_check_circle);
                    }

                    if (behavior.getMoreInfo().equals("")){
                        ((TryGoalViewHolder)viewHolder).moreInfoImageView.setVisibility(View.GONE);
                    }
                    else{
                        ((TryGoalViewHolder)viewHolder).moreInfoImageView.setVisibility(View.VISIBLE);
                        // Set up a Click Listener for all other cards.
                        ((TryGoalViewHolder) viewHolder).moreInfoImageView.setOnClickListener(new View
                                .OnClickListener(){

                            @Override
                            public void onClick(View v){
                                Log.d("GoalTryActivity", "Launch More Info");
                                moreInfoPressed(behavior);
                            }
                        });
                    }

                    if (behavior.getActionCount() == 0){
                        ((TryGoalViewHolder) viewHolder).selectActionsImageView.setVisibility(View.GONE);
                    }
                    else{
                        ((TryGoalViewHolder) viewHolder).selectActionsImageView.setVisibility(View.VISIBLE);
                        ((TryGoalViewHolder) viewHolder).selectActionsImageView.setOnClickListener(new View
                                .OnClickListener(){

                            @Override
                            public void onClick(View v){
                                Log.d("GoalTryActivity", "Launch Action Picker");
                                launchActionPicker(behavior);

                            }
                        });
                    }

                    ((TryGoalViewHolder) viewHolder).tryItImageView.setOnClickListener(new View
                            .OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if(behavior_is_selected) {
                                // Tapping this again should remove the behavior
                                Log.d("GoalTryActivity", "Trying to remove behavior: " + behavior.getTitle());
                                deleteBehavior(behavior);
                                ((ImageView) v).setImageResource(R.drawable.ic_blue_plus_circle);
                            } else {
                                // We need to add the behavior to the user's selections.
                                addBehavior(behavior);
                                ((ImageView) v).setImageResource(R.drawable.ic_blue_check_circle);
                            }
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
        loadBehaviors();
    }

    public void launchActionPicker(Behavior behavior) {
        // Launch the ChooseActionsActivity (where users choose actions for this Behavior)
        Intent intent = new Intent(getApplicationContext(), ChooseActionsActivity.class);
        intent.putExtra("category", mCategory);
        intent.putExtra("goal", mGoal);
        intent.putExtra("behavior", behavior);
        startActivity(intent);
    }

    public void moreInfoPressed(Behavior behavior) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(GoalTryActivity.this);
            builder.setMessage(behavior.getMoreInfo()).setTitle(behavior.getTitle());
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

    public void addBehavior(Behavior behavior) {
        ArrayList<String> behaviors = new ArrayList<String>();
        behaviors.add(String.valueOf(behavior.getId()));
        new AddBehaviorTask(this, this, behaviors).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        if (behavior.getActionCount() > 0){
            // Launch the ChooseActionsActivity (where users choose actions for this Behavior)
            Intent intent = new Intent(getApplicationContext(), ChooseActionsActivity.class);
            intent.putExtra("category", mCategory);
            intent.putExtra("goal", mGoal);
            intent.putExtra("behavior", behavior);
            startActivity(intent);
        }
    }

    @Override
    public void behaviorsAdded(ArrayList<Behavior> behaviors) {
        if(behaviors != null) {
            for(Behavior b : behaviors) {
                application.addBehavior(b);
            }
        } else {
            Log.e("GoalTryActivity", "No behaviors added");
        }
        mAdapter.notifyDataSetChanged();
        Toast.makeText(this, getText(R.string.goal_try_behavior_added), Toast.LENGTH_SHORT).show();
    }

    public void deleteBehavior(Behavior behavior) {

        // Make sure we find the behavior that contains the user's mapping id.
        if(behavior.getMappingId() <= 0) {
            for(Behavior b : application.getBehaviors()) {
                if(behavior.getId() == b.getId()) {
                    behavior.setMappingId(b.getMappingId());
                    break;
                }
            }
        }

        Log.e("GoalTryActivity", "Deleting Behavior, id = " + behavior.getId() + ", userbehavior id = "
                + behavior.getMappingId() + ", " + behavior.getTitle());
        ArrayList<String> behaviorsToDelete = new ArrayList<String>();
        behaviorsToDelete.add(String.valueOf(behavior.getMappingId()));
        new DeleteBehaviorTask(this, this, behaviorsToDelete).executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR);

        application.removeBehavior(behavior);
        Toast.makeText(this, getText(R.string.goal_try_behavior_removed), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void behaviorsDeleted() {
        Log.d("GoalTryActivity", "DeleteBehaviorTask completed.");
        application.logSelectedData("AFTER Deleting a Behavior");
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
