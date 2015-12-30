package org.tndata.android.compass.adapter.feed;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Goal;


/**
 * View holder for a goal card.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
class GoalHolder extends MainFeedViewHolder implements View.OnClickListener{
    RelativeLayout mIconContainer;
    ImageView mIcon;
    TextView mTitle;


    /**
     * Constructor.
     *
     * @param adapter a reference to the adapter that will handle the holder.
     * @param rootView the root view held by this holder.
     */
    GoalHolder(MainFeedAdapter adapter, View rootView){
        super(adapter, rootView);

        mIconContainer = (RelativeLayout)rootView.findViewById(R.id.card_goal_icon_container);
        mIcon = (ImageView)rootView.findViewById(R.id.card_goal_icon);
        mTitle = (TextView)rootView.findViewById(R.id.card_goal_title);

        rootView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        int position = getAdapterPosition()-(CardTypes.getMyGoalsHeaderPosition()+1);
        mAdapter.mListener.onGoalSelected(mAdapter.getDataHandler().getGoals().get(position));
    }

    void bind(Goal goal, boolean suggestion){
        if (!suggestion){
            GradientDrawable gradientDrawable = (GradientDrawable)mIconContainer.getBackground();
            if (goal.getPrimaryCategory() != null){
                gradientDrawable.setColor(Color.parseColor(goal.getPrimaryCategory().getColor()));
            }
            else{
                //TODO another workaround
                gradientDrawable.setColor(mAdapter.mContext.getResources().getColor(R.color.grow_primary));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                mIconContainer.setBackground(gradientDrawable);
            }
            else{
                mIconContainer.setBackgroundDrawable(gradientDrawable);
            }
        }

        goal.loadIconIntoView(mIcon);
        mTitle.setText(goal.getTitle());
    }
}
