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
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.task.AddGoalTask;
import org.tndata.android.compass.task.DeleteGoalTask;
import org.tndata.android.compass.task.GoalLoaderTask;
import org.tndata.android.compass.ui.SpacingItemDecoration;
import org.tndata.android.compass.ui.parallaxrecyclerview.HeaderLayoutManagerFixed;
import org.tndata.android.compass.ui.parallaxrecyclerview.ParallaxRecyclerAdapter;
import org.tndata.android.compass.util.CompassTagHandler;
import org.tndata.android.compass.util.ImageHelper;
import org.tndata.android.compass.util.ImageLoader;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * The ChooseGoalsActivity is where a user selects Goals within a selected Category.
 */
public class ChooseGoalsActivity extends ActionBarActivity implements AddGoalTask
        .AddGoalsTaskListener,
        GoalLoaderTask.GoalLoaderListener, DeleteGoalTask.DeleteGoalTaskListener {

    private CompassApplication application;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private ImageView mHeaderImageView;
    private TextView mErrorTextView;
    private View mFakeHeader;
    private View mHeaderView;
    private RelativeLayout mHeaderCircleView;
    private ArrayList<Goal> mItems; // Array of available Goals to choose
    private ParallaxRecyclerAdapter<Goal> mAdapter;
    private Category mCategory = null;

    private HashSet<Goal> mExpandedGoals = new HashSet<>();
    private int mCurrentlyExpandedPosition = -1;

    static class ChooseGoalViewHolder extends RecyclerView.ViewHolder {
        public ChooseGoalViewHolder(View itemView) {
            super(itemView);
            iconImageView = (ImageView) itemView
                    .findViewById(R.id.list_item_choose_goal_icon_imageview);
            iconContainerView = (RelativeLayout) itemView.findViewById(R.id
                    .list_item_choose_goal_imageview_container);
            titleTextView = (TextView) itemView
                    .findViewById(R.id.list_item_choose_goal_title_textview);
            selectButton = (ImageView) itemView
                    .findViewById(R.id.list_item_choose_goal_select_button);
            descriptionTextView = (TextView) itemView
                    .findViewById(R.id.list_item_choose_goal_description_textview);
            detailContainerView = (RelativeLayout) itemView.findViewById(R.id
                    .list_item_choose_goal_detail_container);
            headerCardTextView = (TextView) itemView
                    .findViewById(R.id.list_item_choose_goal_header_textview);
        }

        TextView titleTextView;
        TextView descriptionTextView;
        ImageView iconImageView;
        ImageView selectButton;
        RelativeLayout iconContainerView;
        RelativeLayout detailContainerView;
        TextView headerCardTextView;
    }

    private Goal createHeaderObject() {
        // NOTE: We want a single _Header Card_ for each collection; It'll contain the
        // parent's description (in this case the Category), but so the card can be created
        // with he rest of the collection, we'll construct a Goal object with only
        // a description.
        Goal headerGoal = new Goal();
        headerGoal.setDescription(mCategory.getDescription());
        headerGoal.setId(0); // it's not a real object, so it doesn't have a real ID.

        return headerGoal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_goals);

        application = (CompassApplication) getApplication();

        mCategory = (Category) getIntent().getSerializableExtra("category");

        mToolbar = (Toolbar) findViewById(R.id.choose_goals_toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        mToolbar.setTitle(getString(R.string.choose_goals_header_label,
                mCategory.getTitle()));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.choose_goals_recyclerview);
        HeaderLayoutManagerFixed manager = new HeaderLayoutManagerFixed(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(new SpacingItemDecoration(this, 10));
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        mErrorTextView = (TextView) findViewById(R.id.choose_goals_error_textview);

        mItems = new ArrayList<Goal>();
        mItems.add(0, createHeaderObject());

        mAdapter = new ParallaxRecyclerAdapter<>(mItems);
        mAdapter.implementRecyclerAdapterMethods(new ParallaxRecyclerAdapter
                .RecyclerAdapterMethods() {
            @Override
            public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder,
                                         final int i) {
                final Goal goal = mItems.get(i);

                if (i == 0 && goal.getId() == 0) {

                    // Display the Header Card

                    ((ChooseGoalViewHolder) viewHolder).titleTextView.setVisibility(View.GONE);
                    ((ChooseGoalViewHolder) viewHolder).iconContainerView.setVisibility(View.GONE);
                    ((ChooseGoalViewHolder) viewHolder).detailContainerView.setVisibility(View.GONE);
                    ((ChooseGoalViewHolder) viewHolder).descriptionTextView.setVisibility(View.GONE);
                    if (!goal.getHTMLDescription().isEmpty()) {
                        ((ChooseGoalViewHolder) viewHolder).headerCardTextView.setText(Html.fromHtml(goal.getHTMLDescription(), null, new CompassTagHandler()));
                    } else {
                        ((ChooseGoalViewHolder) viewHolder).headerCardTextView.setText(goal.getDescription());
                    }
                    ((ChooseGoalViewHolder) viewHolder).headerCardTextView.setVisibility(View.VISIBLE);
                } else {

                    // Handle all other cards

                    ((ChooseGoalViewHolder) viewHolder).titleTextView.setText(goal
                            .getTitle());
                    if (mExpandedGoals.contains(goal)) {
                        ((ChooseGoalViewHolder) viewHolder).iconContainerView.setVisibility(View.GONE);
                        ((ChooseGoalViewHolder) viewHolder).descriptionTextView.setVisibility(View
                                .VISIBLE);
                    } else {
                        ((ChooseGoalViewHolder) viewHolder).descriptionTextView.setVisibility(View
                                .GONE);
                        ((ChooseGoalViewHolder) viewHolder).iconContainerView.setVisibility(View
                                .VISIBLE);
                    }
                    if (!goal.getHTMLDescription().isEmpty()) {
                        ((ChooseGoalViewHolder) viewHolder).descriptionTextView.setText(Html.fromHtml(goal.getHTMLDescription(), null, new CompassTagHandler()));
                    } else {
                        ((ChooseGoalViewHolder) viewHolder).descriptionTextView.setText(goal.getDescription());
                    }
                    if (goal.getIconUrl() != null
                            && !goal.getIconUrl().isEmpty()) {
                        ImageLoader.loadBitmap(((ChooseGoalViewHolder) viewHolder).iconImageView,
                                goal.getIconUrl(), false);
                    }

                    if (application.getGoals().contains(goal)) {
                        ImageHelper.setupImageViewButton(getResources(),
                                ((ChooseGoalViewHolder) viewHolder).selectButton, ImageHelper.SELECTED);
                    } else {
                        ImageHelper.setupImageViewButton(getResources(),
                                ((ChooseGoalViewHolder) viewHolder).selectButton, ImageHelper.ADD);
                    }

                    ((ChooseGoalViewHolder) viewHolder).selectButton.setOnClickListener(new View
                            .OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            goalSelected(goal);
                        }
                    });

                    GradientDrawable gradientDrawable = (GradientDrawable) ((ChooseGoalViewHolder)
                            viewHolder).iconContainerView.getBackground();
                    String colorString = mCategory.getColor();
                    if (colorString != null && !colorString.isEmpty()) {
                        gradientDrawable.setColor(Color.parseColor(colorString));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            ((ChooseGoalViewHolder) viewHolder).iconContainerView.setBackground
                                    (gradientDrawable);
                        } else {
                            ((ChooseGoalViewHolder) viewHolder).iconContainerView
                                    .setBackgroundDrawable(gradientDrawable);
                        }
                    }
                }
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
        mHeaderCircleView = (RelativeLayout) mFakeHeader.findViewById(R.id.choose_goals_header_circle_view);
        mHeaderImageView = (ImageView) mFakeHeader.findViewById(R.id.choose_goals_header_imageview);
        mCategory.loadImageIntoView(getApplicationContext(), mHeaderImageView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mHeaderCircleView.setClipToOutline(true);
        }

        mHeaderView = findViewById(R.id.choose_goals_material_view);
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
        mAdapter.setOnClickEvent(new ParallaxRecyclerAdapter.OnClickEvent() {

            @Override
            public void onClick(View v, int position) {
                if (position <= 0) {
                    // This is the header, ignore. This fixes a bug when clicking a description
                    return;
                }
                Goal goal = mItems.get(position);

                if (mExpandedGoals.contains(goal)) {
                    mExpandedGoals.remove(goal);
                } else {
                    mExpandedGoals.clear();
                    if (mCurrentlyExpandedPosition >= 0) {
                        mAdapter.notifyItemChanged(mCurrentlyExpandedPosition);
                    }
                    mExpandedGoals.add(goal);
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

        loadGoals();
    }

    private void showError() {
        mRecyclerView.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    @SuppressLint("DefaultLocale")
    private void loadGoals() {
        if (mCategory == null) {
            return;
        }
        new GoalLoaderTask(getApplicationContext(), this).executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR,
                application.getToken(),
                String.valueOf(mCategory.getId()));
    }

    @Override
    public void goalsAdded(ArrayList<Goal> goals) {
        // we've already added the goal to the application's collection.
        Log.d("ChooseGoalsActivity", "Goal added via API");
        for (Goal goal : goals) {
            application.addGoal(goal); // should include the user's goal mapping id.
        }
        mAdapter.notifyDataSetChanged();
    }

    public void goalSelected(Goal goal) {
        // When a goal has been selected, save it in our list of selected goals, and then
        // immediately launch the user into the Behavior Selection workflow.

        if (application.getGoals().contains(goal)) {
            deleteGoal(goal);
        } else {
            //mSelectedGoals.add(goal);
            addGoal(goal);

            if (goal.getBehaviorCount() > 0) {
                // Launch the GoalTryActivity (where users choose a behavior for the Goal)
                Intent intent = new Intent(getApplicationContext(), GoalTryActivity.class);
                intent.putExtra("goal", goal);
                intent.putExtra("category", mCategory);
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.goal_selected, Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void deleteGoal(Goal goal) {
        // Remove the goal from the application's collection and DELETE from the API

        // Ensure the goal contains the usermapping id
        if (goal.getMappingId() <= 0) {
            for (Goal g : application.getGoals()) {
                if (goal.getId() == g.getId()) {
                    goal.setMappingId(g.getMappingId());
                    break;
                }
            }
        }

        ArrayList<String> goalsToDelete = new ArrayList<String>();
        goalsToDelete.add(String.valueOf(goal.getMappingId()));
        Log.d("ChooseGoalsActivity", "About to delete goal: id = " + goal.getId() +
                ", usergoal.id = " + goal.getMappingId() + "; " + goal.getTitle());

        new DeleteGoalTask(this, this, goalsToDelete).executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR);

        application.removeGoal(goal);
        mAdapter.notifyDataSetChanged();
    }

    protected void addGoal(Goal goal) {
        // Save the user's selected goal via the API, and add it to the application's collection.
        ArrayList<String> goalsToAdd = new ArrayList<String>();
        goalsToAdd.add(String.valueOf(goal.getId()));
        new AddGoalTask(this, this, goalsToAdd).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

    @Override
    public void goalsDeleted() {
        Toast.makeText(this, getText(R.string.choose_goals_goal_removed), Toast.LENGTH_SHORT).show();

    }
}
