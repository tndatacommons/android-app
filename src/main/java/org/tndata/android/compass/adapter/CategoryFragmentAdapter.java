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

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.ui.CompassPopupMenu;

import java.util.List;

public class CategoryFragmentAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface CategoryFragmentAdapterInterface {
        public void chooseBehaviors(Goal goal);
        public void viewGoal(Goal goal);
        public void deleteGoal(Goal goal);
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

        public void toggleCard() {
            // If the card is collapsed, expand it; if it's expanded, collapse it.
            if(descriptionTextView.getVisibility() == View.GONE) {
                circleView.setVisibility(View.GONE);
                descriptionTextView.setVisibility(View.VISIBLE);
                moreInfoTextView.setVisibility(View.VISIBLE);
            }else {
                circleView.setVisibility(View.VISIBLE);
                descriptionTextView.setVisibility(View.GONE);
                moreInfoTextView.setVisibility(View.GONE);
            }
        }

    }

    private Context mContext;
    private Category mCategory;
    private List<Goal> mItems;
    private CategoryFragmentAdapterInterface mCallback;

    public CategoryFragmentAdapter(Context context, List<Goal> objects, Category category,
                                   CategoryFragmentAdapterInterface callback) {
        if (objects == null) {
            throw new IllegalArgumentException("Goals List must not be null");
        }
        this.mItems = objects;
        this.mContext = context;
        this.mCategory = category;
        this.mCallback = callback;
    }

    public void updateEntries(List<Goal> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder,
                                 final int position) {
        final Goal goal = mItems.get(position);
        ((CategoryGoalViewHolder) viewHolder).titleTextView.setText(goal.getTitle());
        ((CategoryGoalViewHolder) viewHolder).descriptionTextView.setText(
                goal.getDescription());
        ((CategoryGoalViewHolder) viewHolder).setCircleViewBackgroundColor(
                mCategory.getColor());
        ((CategoryGoalViewHolder) viewHolder).iconImageView.setImageResource(
                goal.getProgressIcon());

        ((CategoryGoalViewHolder) viewHolder).moreInfoTextView.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.viewGoal(goal);
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
                    ((CategoryGoalViewHolder) viewHolder).toggleCard();
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
                    case R.id.menu_popup_add_behavior:
                        mCallback.chooseBehaviors(goal);
                        break;
                    case R.id.menu_popup_view_details:
                        mCallback.viewGoal(goal);
                        break;
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
