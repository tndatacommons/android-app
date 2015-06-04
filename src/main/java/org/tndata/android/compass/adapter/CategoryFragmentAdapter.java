package org.tndata.android.compass.adapter;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;

import java.util.List;

public class CategoryFragmentAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public interface CategoryFragmentAdapterInterface {
        public void chooseBehaviors(Goal goal);

        public void viewBehavior(Goal goal, Behavior behavior);
    }

    static class CategoryGoalViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        RelativeLayout circleView;
        LinearLayout goalContainer;
        ImageView iconImageView;

        public CategoryGoalViewHolder(View view) {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id
                    .list_item_category_goal_title_textview);
            circleView = (RelativeLayout) view.findViewById(R.id
                    .list_item_category_goal_circle_view);
            goalContainer = (LinearLayout) view.findViewById(R.id
                    .list_item_category_goal_goal_container);
            iconImageView = (ImageView) view.findViewById(R.id
                    .list_item_category_goal_icon_imageview);
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
        ((CategoryGoalViewHolder) viewHolder).setCircleViewBackgroundColor(
                mCategory.getColor());

        // Set the progress widget for the Goal.
        ((CategoryGoalViewHolder) viewHolder).iconImageView.setImageResource(
                goal.getProgressIcon());

        ((CategoryGoalViewHolder) viewHolder).goalContainer.setOnClickListener(new View
                .OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.chooseBehaviors(goal);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup,
                                                      int viewType) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.list_item_category_goal, viewGroup, false);
        return new CategoryGoalViewHolder(itemView);
    }

}
