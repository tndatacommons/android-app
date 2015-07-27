package org.tndata.android.compass.adapter;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.ui.CompassPopupMenu;

public class CategoryFragmentAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface CategoryFragmentAdapterInterface {
        public void chooseBehaviors(Goal goal);

        public void viewGoal(Goal goal);

        public void deleteGoal(Goal goal);

        public void cardExpand();

        public void cardCollapse();
    }

    static class CategoryGoalViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        RelativeLayout circleView;
        LinearLayout goalContainer;
        ImageView iconImageView;
        ImageView menuImageView;
        TextView moreInfoTextView;

        public CategoryGoalViewHolder(View view) {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id
                    .list_item_category_goal_title_textview);
            descriptionTextView = (TextView) view.findViewById(R.id
                    .list_item_category_goal_description_textview);
            circleView = (RelativeLayout) view.findViewById(R.id
                    .list_item_category_goal_circle_view);
            goalContainer = (LinearLayout) view.findViewById(R.id
                    .list_item_category_goal_goal_container);
            iconImageView = (ImageView) view.findViewById(R.id
                    .list_item_category_goal_icon_imageview);
            menuImageView = (ImageView) view.findViewById(R.id.goal_popup_imageview);

            moreInfoTextView = (TextView) view.findViewById(R.id
                    .list_item_category_goal_more_info_textview);
        }

        public void setCircleViewBackgroundColor(String colorString) {
            GradientDrawable gradientDrawable = (GradientDrawable) circleView.getBackground();
            if (colorString != null && !colorString.isEmpty()) {
                gradientDrawable.setColor(Color.parseColor(colorString));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                circleView.setBackground(gradientDrawable);
            } else {
                circleView.setBackgroundDrawable(gradientDrawable);
            }
        }

        public void toggleCard(CategoryFragmentAdapterInterface mCallback) {
            // If the card is collapsed, expand it; if it's expanded, collapse it.
            if (descriptionTextView.getVisibility() == View.GONE) {
                circleView.setVisibility(View.GONE);
                descriptionTextView.setVisibility(View.VISIBLE);
                moreInfoTextView.setVisibility(View.VISIBLE);
                mCallback.cardExpand();
            } else {
                circleView.setVisibility(View.VISIBLE);
                descriptionTextView.setVisibility(View.GONE);
                moreInfoTextView.setVisibility(View.GONE);
                mCallback.cardCollapse();
            }
        }

    }

    private CompassApplication mApplication;
    private Context mContext;
    private Category mCategory;
    private CategoryFragmentAdapterInterface mCallback;
    private final String TAG = "CatFragAdapter";

    public CategoryFragmentAdapter(Context context, CompassApplication application, Category category,
                                   CategoryFragmentAdapterInterface callback) {

        if (application.getCategoryGoals(category) == null) {
            throw new IllegalArgumentException("Goals List must not be null");
        }

        this.mApplication = application;
        this.mContext = context;
        this.mCategory = category;
        this.mCallback = callback;
    }

    @Override
    public int getItemCount() {
        return mApplication.getCategoryGoals(mCategory).size();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder,
                                 final int position) {
        final Goal goal = mApplication.getCategoryGoals(mCategory).get(position);
        ((CategoryGoalViewHolder) viewHolder).titleTextView.setText(goal.getTitle());
        ((CategoryGoalViewHolder) viewHolder).descriptionTextView.setText(
                goal.getDescription());
        ((CategoryGoalViewHolder) viewHolder).setCircleViewBackgroundColor(
                mCategory.getColor());

        ImageView iconImageView = ((CategoryGoalViewHolder) viewHolder).iconImageView;

        // If the user hasn't selected any Behaviors display the goal's icon instead of the
        // progress widget
        goal.loadIconIntoView(mContext, iconImageView);

        final Boolean goalIsEmpty = goal.getBehaviors().isEmpty();
        TextView ctaTextView = ((CategoryGoalViewHolder) viewHolder).moreInfoTextView;
        if (goalIsEmpty) {
            ctaTextView.setText(R.string.goal_card_add_label);
        } else {
            ctaTextView.setText(R.string.goal_card_details_label);
        }

        ctaTextView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (goalIsEmpty) {
                            // when the user has *not* selected Behaviors/Actions, launch the picker
                            mCallback.chooseBehaviors(goal);
                        } else {
                            // When the user has selected Behaviors/Actions, view the goal's details
                            mCallback.viewGoal(goal);
                        }
                    }
                }
        );

        // Hook up the popup menu
        ((CategoryGoalViewHolder) viewHolder).menuImageView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopup(v, goal);
                    }
                }
        );

        // Expand/Collapse the card when tapped
        ((CategoryGoalViewHolder) viewHolder).itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((CategoryGoalViewHolder) viewHolder).toggleCard(mCallback);
                    }
                }
        );
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup,
                                                      int viewType) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.list_item_category_goal, viewGroup, false);
        return new CategoryGoalViewHolder(itemView);
    }

    public void showPopup(final View anchor, final Goal goal) {

        CompassPopupMenu popup = CompassPopupMenu.newInstance(mContext, anchor);
        popup.getMenuInflater().inflate(R.menu.menu_goal_details, popup.getMenu());
        popup.setOnMenuItemClickListener(new CompassPopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_popup_remove_goal:
                        mCallback.deleteGoal(goal);
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

}
