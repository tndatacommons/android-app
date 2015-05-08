package org.tndata.android.grow.adapter;


import android.app.Activity;
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

import org.tndata.android.grow.GrowApplication;
import org.tndata.android.grow.R;
import org.tndata.android.grow.model.Action;
import org.tndata.android.grow.model.Behavior;
import org.tndata.android.grow.model.Category;
import org.tndata.android.grow.model.Goal;
import org.tndata.android.grow.ui.ActionListView;
import org.tndata.android.grow.ui.BehaviorListView;
import org.tndata.android.grow.util.ImageCache;

import java.util.ArrayList;
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
        LinearLayout behaviorContainer;
        ImageView iconImageView;

        public CategoryGoalViewHolder(View view) {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id
                    .list_item_category_goal_title_textview);
            circleView = (RelativeLayout) view.findViewById(R.id
                    .list_item_category_goal_circle_view);
            goalContainer = (LinearLayout) view.findViewById(R.id
                    .list_item_category_goal_goal_container);
            behaviorContainer = (LinearLayout) view.findViewById(R.id
                    .list_item_category_goal_behavior_container);
            iconImageView = (ImageView) view.findViewById(R.id
                    .list_item_category_goal_icon_imageview);
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
        GradientDrawable gradientDrawable = (GradientDrawable) ((CategoryGoalViewHolder)
                viewHolder).circleView.getBackground();
        String colorString = mCategory.getColor();
        if (colorString != null && !colorString.isEmpty()) {
            gradientDrawable.setColor(Color.parseColor(colorString));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ((CategoryGoalViewHolder) viewHolder).circleView.setBackground
                    (gradientDrawable);
        } else {
            ((CategoryGoalViewHolder) viewHolder).circleView
                    .setBackgroundDrawable(gradientDrawable);
        }
        if (goal.getIconUrl() != null
                && !goal.getIconUrl().isEmpty()) {
            ImageCache.instance(mContext).loadBitmap(
                    ((CategoryGoalViewHolder) viewHolder).iconImageView,
                    goal.getIconUrl(), false);
        }
        ((CategoryGoalViewHolder) viewHolder).goalContainer.setOnClickListener(new View
                .OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.chooseBehaviors(goal);
            }
        });
        ArrayList<Behavior> behaviors = goal.getBehaviors();
        ((CategoryGoalViewHolder) viewHolder).behaviorContainer.removeAllViews();
        if (behaviors != null && !behaviors.isEmpty()) {
            for (final Behavior behavior : behaviors) {
                BehaviorListView behaviorListView = new BehaviorListView(mContext);
                behaviorListView.setBehavior(behavior, mCategory);
                behaviorListView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallback.viewBehavior(goal, behavior);
                    }
                });
                ((CategoryGoalViewHolder) viewHolder).behaviorContainer.addView(behaviorListView);
                if (mContext instanceof Activity) {
                    for (final Action action : ((GrowApplication) ((Activity) mContext)
                            .getApplication()).getActions()) {
                        if ((action.getBehavior() != null && action.getBehavior().equals(behavior))
                                || (action.getBehavior_id() == behavior.getId())) {
                            ActionListView actionListView = new ActionListView(mContext);
                            actionListView.setAction(action);

                            ((CategoryGoalViewHolder) viewHolder).behaviorContainer.addView
                                    (actionListView);

                        }
                    }
                }
            }
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup,
                                                      int viewType) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.list_item_category_goal, viewGroup, false);
        return new CategoryGoalViewHolder(itemView);
    }

}
