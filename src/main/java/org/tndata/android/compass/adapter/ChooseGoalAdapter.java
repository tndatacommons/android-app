package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.model.TDCGoal;

import java.util.List;


/**
 * Created by isma on 5/4/16.
 */
public class ChooseGoalAdapter extends RecyclerView.Adapter<ChooseGoalAdapter.GoalViewHolder>{
    private Context mContext;
    private ChooseGoalListener mListener;
    private TDCCategory mCategory;
    private List<TDCGoal> mGoals;


    public ChooseGoalAdapter(Context context, ChooseGoalListener listener, TDCCategory category, List<TDCGoal> goals){
        mContext = context;
        mListener = listener;
        mCategory = category;
        mGoals = goals;
    }

    @Override
    public int getItemCount(){
        return mGoals.size();
    }

    @Override
    public GoalViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(R.layout.card_goal, parent, false);
        return new GoalViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(GoalViewHolder holder, int position){
        holder.bind(mGoals.get(position));
    }

    /**
     * The view holder for a goal.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    class GoalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private RelativeLayout mIconContainer;
        private ImageView mIcon;
        private TextView mTitle;


        /**
         * Constructor. Extracts the UI components from the root view.
         *
         * @param rootView the root view held by this view holder.
         */
        public GoalViewHolder(View rootView){
            super(rootView);

            //Fetch UI components
            mIconContainer = (RelativeLayout)rootView.findViewById(R.id.goal_icon_container);
            mIcon = (ImageView)rootView.findViewById(R.id.goal_icon);
            mTitle = (TextView)rootView.findViewById(R.id.goal_title);

            rootView.setOnClickListener(this);
        }

        /**
         * Binds a behavior to the holder.
         *
         * @param goal the behavior to be bound.
         */
        @SuppressWarnings("deprecation")
        public void bind(@NonNull TDCGoal goal){
            GradientDrawable gradientDrawable = (GradientDrawable)mIconContainer.getBackground();
            gradientDrawable.setColor(Color.parseColor(mCategory.getColor()));
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
                mIconContainer.setBackgroundDrawable(gradientDrawable);
            }
            else{
                mIconContainer.setBackground(gradientDrawable);
            }
            goal.loadIconIntoView(mIcon);
            mTitle.setText(goal.getTitle());
        }

        @Override
        public void onClick(View v){
            mListener.onGoalSelected(mGoals.get(getAdapterPosition()), mCategory);
        }
    }


    /**
     * Listener interface for the adapter.
     *
     * @author Ismael Alonso
     * @version 1.1.0
     */
    public interface ChooseGoalListener{
        /**
         * Called when the add button is tapped.
         *
         * @param goal the goal whose add was tapped.
         */
        void onGoalSelected(@NonNull TDCGoal goal, TDCCategory category);
    }
}
