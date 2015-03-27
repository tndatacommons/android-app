package org.tndata.android.grow.adapter;

import java.util.ArrayList;
import java.util.List;

import org.tndata.android.grow.R;
import org.tndata.android.grow.model.Goal;
import org.tndata.android.grow.util.ImageCache;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class OnBoardingGoalAdapter extends ArrayAdapter<Goal> {
    public interface OnBoardingGoalAdapterListener {
        public ArrayList<Goal> getCurrentlySelectedGoals();

        public void goalSelected(Goal goal);

        public void moreInfoPressed(Goal goal);
    }

    private Context mContext;
    private List<Goal> mItems;
    private OnBoardingGoalAdapterListener mCallback;

    public OnBoardingGoalAdapter(Context context, int resource,
            List<Goal> objects, OnBoardingGoalAdapterListener callback) {
        super(context, resource, objects);
        this.mItems = objects;
        this.mContext = context;
        this.mCallback = callback;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    public void updateEntries(List<Goal> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item_onboarding_goal, null);
        }

        final Goal goal = mItems.get(position);
        final TextView title = (TextView) v
                .findViewById(R.id.list_item_onboarding_goal_title_textview);
        title.setText(goal.getTitle());
        final ImageView icon = (ImageView) v
                .findViewById(R.id.list_item_onboarding_goal_imageview);
        if (goal.getIconUrl() != null && !goal.getIconUrl().isEmpty()) {
            ImageCache.instance(mContext).loadBitmap(icon, goal.getIconUrl(),
                    false);
        } else {
            icon.setImageResource(R.drawable.default_image);
        }

        final ImageView check = (ImageView) v
                .findViewById(R.id.list_item_onboarding_goal_selected_imageview);
        final Button select = (Button) v
                .findViewById(R.id.list_item_onboarding_goal_select_button);
        final Button moreInfo = (Button) v
                .findViewById(R.id.list_item_onboarding_goal_more_info_button);
        final ArrayList<Goal> selectedItems = mCallback
                .getCurrentlySelectedGoals();
        if (selectedItems.contains(goal)) {
            check.setVisibility(View.VISIBLE);
            select.setVisibility(View.INVISIBLE);
        } else {
            check.setVisibility(View.INVISIBLE);
            select.setVisibility(View.VISIBLE);
        }
        select.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.goalSelected(goal);
            }
        });
        check.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.goalSelected(goal);
            }
        });
        moreInfo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.moreInfoPressed(goal);
            }
        });

        return v;
    }
}
