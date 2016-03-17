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
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.GoalContent;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter for the goal picker.
 *
 * @author Ismael Alonso
 * @version 2.0.0
 */
public class ChooseGoalsAdapter extends MaterialAdapter{
    private Context mContext;
    private ChooseGoalsListener mListener;
    private CategoryContent mCategory;

    private GoalsAdapter mGoalsAdapter;
    private List<GoalContent> mGoals;


    /**
     * Constructor.
     *
     * @param context the application context.
     * @param listener the event listener.
     * @param category the category from where the goals are pulled.
     */
    public ChooseGoalsAdapter(@NonNull Context context, @NonNull ChooseGoalsListener listener,
                              @NonNull CategoryContent category){

        super(context, ContentType.LIST, true);

        mContext = context;
        mListener = listener;
        mCategory = category;

        mGoals = new ArrayList<>();
    }

    @Override
    protected boolean isEmpty(){
        return mGoals.isEmpty();
    }

    @Override
    protected void bindHeaderHolder(RecyclerView.ViewHolder rawHolder){
        HeaderViewHolder holder = (HeaderViewHolder)rawHolder;
        holder.setTitle(mCategory.getTitle());
        holder.setContent(mCategory.getDescription());
    }

    @Override
    protected void bindListHolder(RecyclerView.ViewHolder rawHolder){
        ListViewHolder holder = (ListViewHolder)rawHolder;
        holder.setHeaderColor(Color.parseColor(mCategory.getColor()));
        holder.setTitleColor(Color.WHITE);
        holder.setTitle(mContext.getString(R.string.library_goals_content_header));
        mGoalsAdapter = new GoalsAdapter();
        holder.setAdapter(new GoalsAdapter());
    }

    /**
     * Adds a set of goals to the backing list.
     *
     * @param goals the list of goals to be added.
     * @param showLoading whether the load switch should be kept or removed.
     */
    public void add(@NonNull List<GoalContent> goals, boolean showLoading){
        //If there are no goals, insert the goals card
        if (isEmpty()){
            notifyListInserted();
        }
        //Update the load switch
        updateLoading(showLoading);

        //Record the initial position of the new sub-list in the master list
        int positionStart = mGoals.size();
        //Add all the goals in the goal list
        mGoals.addAll(goals);
        //If the adapter has been created already, trigger animations
        if (mGoalsAdapter != null){
            prepareListChange();
            mGoalsAdapter.notifyItemRangeInserted(positionStart, goals.size());
            notifyListChanged();
        }
    }

    @Override
    protected void loadMore(){
        mListener.loadMore();
    }


    private class GoalsAdapter extends RecyclerView.Adapter<GoalViewHolder>{
        @Override
        public int getItemCount(){
            return mGoals.size();
        }

        @Override
        public GoalViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View rootView = inflater.inflate(R.layout.item_goal, parent, false);
            return new GoalViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(GoalViewHolder holder, int position){
            holder.bind(mGoals.get(position));
        }
    }


    /**
     * The view holder for a goal.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class GoalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private View mSeparator;
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
            mSeparator = rootView.findViewById(R.id.goal_separator);
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
        public void bind(@NonNull GoalContent goal){
            //If this is the first item, do not show the separator
            if (getAdapterPosition() == 0){
                mSeparator.setVisibility(View.GONE);
            }
            else{
                mSeparator.setVisibility(View.VISIBLE);
            }

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
            mListener.onGoalSelected(mGoals.get(getAdapterPosition()));
        }
    }


    /**
     * Listener interface for the adapter.
     *
     * @author Ismael Alonso
     * @version 1.1.0
     */
    public interface ChooseGoalsListener{
        /**
         * Called when the add button is tapped.
         *
         * @param goal the goal whose add was tapped.
         */
        void onGoalSelected(@NonNull GoalContent goal);

        /**
         * Called when the user scrolls to the bottom of the page.
         */
        void loadMore();
    }
}
