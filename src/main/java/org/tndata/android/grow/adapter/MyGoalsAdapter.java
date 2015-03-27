package org.tndata.android.grow.adapter;

import java.util.List;

import org.tndata.android.grow.R;
import org.tndata.android.grow.model.Goal;
import org.tndata.android.grow.util.ImageCache;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MyGoalsAdapter extends
        RecyclerView.Adapter<MyGoalsAdapter.MyGoalsViewHolder> {
    public interface OnClickEvent {
        void onClick(View v, int position);
    }

    private Context mContext;
    private List<Goal> mItems;
    private OnClickEvent mOnClickEvent;

    static class MyGoalsViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        ImageView iconImageView;

        public MyGoalsViewHolder(View view) {
            super(view);
            iconImageView = (ImageView) view
                    .findViewById(R.id.list_item_goal_imageview);
            titleTextView = (TextView) view
                    .findViewById(R.id.list_item_goal_title_textview);
            descriptionTextView = (TextView) view
                    .findViewById(R.id.list_item_goal_description_textview);
        }
    }

    public MyGoalsAdapter(Context context, List<Goal> objects) {
        if (objects == null) {
            throw new IllegalArgumentException("Goals List must not be null");
        }
        this.mItems = objects;
        this.mContext = context;
    }

    public void updateEntries(List<Goal> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void setOnClickEvent(OnClickEvent onClickEvent) {
        mOnClickEvent = onClickEvent;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onBindViewHolder(MyGoalsViewHolder viewHolder,
            final int position) {
        final Goal goal = mItems.get(position);
        if (goal.getIconUrl() != null && !goal.getIconUrl().isEmpty()) {
            ImageCache.instance(mContext).loadBitmap(viewHolder.iconImageView,
                    goal.getIconUrl(), false);
        } else {
            viewHolder.iconImageView.setImageResource(R.drawable.default_image);
        }
        viewHolder.titleTextView.setText(goal.getTitle());
        viewHolder.descriptionTextView.setText(goal.getSubtitle());
        if (mOnClickEvent != null)
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickEvent.onClick(v, position);
                }
            });
    }

    @Override
    public MyGoalsViewHolder onCreateViewHolder(ViewGroup viewGroup,
            int position) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.list_item_goal, viewGroup, false);

        return new MyGoalsViewHolder(itemView);

    }

}
