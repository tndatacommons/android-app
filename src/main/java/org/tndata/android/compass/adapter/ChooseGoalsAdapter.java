package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.filter.GoalFilter;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.ui.button.TransitionButton;
import org.tndata.android.compass.ui.parallaxrecyclerview.HeaderLayoutManagerFixed;
import org.tndata.android.compass.ui.parallaxrecyclerview.ParallaxRecyclerAdapter;
import org.tndata.android.compass.util.CompassTagHandler;
import org.tndata.android.compass.util.ImageLoader;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter for the goal picker.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ChooseGoalsAdapter
        extends ParallaxRecyclerAdapter<Goal>
        implements ParallaxRecyclerAdapter.OnClickEvent{

    private static final byte STATE_NOT_ADDED = 0;
    private static final byte STATE_ADDING = 1;
    private static final byte STATE_ADDED = 2;
    private static final byte STATE_REMOVING = 3;

    private byte goalStates[];


    private Context mContext;
    private ChooseGoalsListener mListener;
    private CompassApplication mApplication;
    private RecyclerView mRecyclerView;
    private Category mCategory;
    private List<Goal> mGoals;
    private GoalFilter mFilter;

    private int mExpandedGoal;


    /**
     * Constructor.
     *
     * @param context the application context.
     * @param listener the event listener.
     * @param app the application object.
     * @param recyclerView the RecyclerView hosting this adapter.
     * @param category the category from where the goals are pulled.
     */
    public ChooseGoalsAdapter(@NonNull Context context, @NonNull ChooseGoalsListener listener,
                              @NonNull CompassApplication app, @NonNull RecyclerView recyclerView,
                              @NonNull Category category){
        super(new ArrayList<Goal>());

        mContext = context;
        mListener = listener;
        mApplication = app;
        mRecyclerView = recyclerView;
        mCategory = category;
        mGoals = new ArrayList<>();
        mFilter = null;

        //Create the header goal and add it to the list
        Goal headerGoal = new Goal();
        headerGoal.setDescription(mCategory.getDescription());
        headerGoal.setId(0);
        mGoals.add(headerGoal);
        setData(mGoals);

        //Mark no card expanded
        mExpandedGoal = -1;

        //Set the header
        setHeader();

        //Add listeners and interfaces
        implementRecyclerAdapterMethods(new ChooseGoalsAdapterMethods());
        setOnClickEvent(this);
    }

    /**
     * Creates the parallax header view and sets it in the list.
     */
    private void setHeader(){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View header = inflater.inflate(R.layout.header_choose_goals, mRecyclerView, false);

        ImageView headerImageView = (ImageView)header.findViewById(R.id.choose_goals_header_imageview);
        mCategory.loadImageIntoView(mContext, headerImageView);

        ((HeaderLayoutManagerFixed)mRecyclerView.getLayoutManager()).setHeaderIncrementFixer(header);
        setShouldClipView(false);
        setParallaxHeader(header, mRecyclerView);
        setOnParallaxScroll(new ParallaxRecyclerAdapter.OnParallaxScroll(){
            @Override
            public void onParallaxScroll(float percentage, float offset, View parallax){
                mListener.onScroll(percentage, offset);
            }
        });
    }

    /**
     * Populates the state array with the appropriate values.
     */
    private void populateStateArray(){
        if (mGoals != null && mGoals.size() > 1){
            goalStates = new byte[mGoals.size()-1];
            List<Goal> userGoals = mApplication.getGoals();
            for (int i = 1; i < mGoals.size(); i++){
                goalStates[i-1] = userGoals.contains(getItem(i)) ? STATE_ADDED : STATE_NOT_ADDED;
            }
        }
    }

    /**
     * Adds the goals in the list to the backing array.
     *
     * @param goals the list of goals to be added.
     */
    public void addGoals(List<Goal> goals){
        mGoals.clear();

        Goal headerGoal = new Goal();
        headerGoal.setDescription(mCategory.getDescription());
        headerGoal.setId(0);
        mGoals.add(headerGoal);

        mGoals.addAll(goals);
        populateStateArray();
        notifyDataSetChanged();

        if (mFilter == null){
            mFilter = new GoalFilter(this, goals);
        }
    }

    /**
     * Returns the goal at the requested position.
     *
     * @param position the position of the goal in the backing array.
     * @return the goal requested.
     */
    public Goal getItem(int position){
        return mGoals.get(position);
    }

    /**
     * Notifies the adapter that a goal has been added to the user's list.
     *
     * @param goal the goal recently added.
     */
    public void goalAdded(Goal goal){
        int index = mGoals.indexOf(goal);
        goalStates[index-1] = STATE_ADDED;
        ChooseGoalsViewHolder holder = (ChooseGoalsViewHolder)mRecyclerView.findViewHolderForLayoutPosition(index+1);
        if (holder != null){
            holder.select.setActive(true);
        }
        //notifyItemChanged(index+1);
    }

    /**
     * Notifies the adapter that a selected goal wasn't added to the user's list.
     *
     * @param goal the goal that was not added.
     */
    public void goalNotAdded(Goal goal){
        int index = mGoals.indexOf(goal);
        goalStates[index-1] = STATE_NOT_ADDED;
        ChooseGoalsViewHolder holder = (ChooseGoalsViewHolder)mRecyclerView.findViewHolderForLayoutPosition(index+1);
        if (holder != null){
            holder.select.setInactive(true);
        }
        //notifyItemChanged(index+1);
    }

    /**
     * Notifies the adapter that a selected goal was deleted from the user's list.
     *
     * @param goal the goal that was deleted.
     */
    public void goalDeleted(Goal goal){
        int index = mGoals.indexOf(goal);
        goalStates[index-1] = STATE_NOT_ADDED;
        ChooseGoalsViewHolder holder = (ChooseGoalsViewHolder)mRecyclerView.findViewHolderForLayoutPosition(index+1);
        if (holder != null){
            holder.select.setInactive(true);
        }
        //notifyItemChanged(index+1);
    }

    /**
     * Notifies the adapter that a selected goal wasn't added to the user's list.
     *
     * @param goal the goal that was not added.
     */
    public void goalNotDeleted(Goal goal){
        int index = mGoals.indexOf(goal);
        goalStates[index-1] = STATE_ADDED;
        ChooseGoalsViewHolder holder = (ChooseGoalsViewHolder)mRecyclerView.findViewHolderForLayoutPosition(index+1);
        if (holder != null){
            holder.select.setActive(true);
        }
        //notifyItemChanged(index+1);
    }

    @Override
    public void onClick(View v, int position){
        if (position > 0){
            int lastExpanded = mExpandedGoal;

            //Add one to the position to account for the header
            if (mExpandedGoal == position+1){
                mExpandedGoal = -1;
                notifyItemChanged(lastExpanded);
            }
            else{
                mExpandedGoal = position+1;
                notifyItemChanged(mExpandedGoal);
                //Let us redraw the item that has changed, this forces the RecyclerView to
                //  respect the layout of each item, and none will overlap
                if (lastExpanded != -1){
                    notifyItemChanged(lastExpanded);
                }
                mRecyclerView.scrollToPosition(mExpandedGoal);
            }
        }
    }


    /**
     * Adapter methods (as requested by ParallaxRecyclerAdapter).
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    class ChooseGoalsAdapterMethods implements RecyclerAdapterMethods{
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View rootView = inflater.inflate(R.layout.item_choose_goal, parent, false);
            return new ChooseGoalsViewHolder(rootView);
        }

        @Override
        @SuppressWarnings("deprecation")
        public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
            ChooseGoalsViewHolder holder = (ChooseGoalsViewHolder)rawHolder;
            final Goal goal = getItem(position);

            //If the goal is the first item, display the header card
            if (position == 0 && goal.getId() == 0){
                holder.title.setVisibility(View.GONE);
                holder.iconContainer.setVisibility(View.GONE);
                holder.detailContainer.setVisibility(View.GONE);
                holder.description.setVisibility(View.GONE);
                holder.header.setVisibility(View.VISIBLE);
                if (!goal.getHTMLDescription().isEmpty()){
                    holder.header.setText(Html.fromHtml(goal.getHTMLDescription(), null,
                            new CompassTagHandler(mContext)));
                }
                else{
                    holder.header.setText(goal.getDescription());
                }
            }
            //Otherwise, handle all other cards
            else{
                holder.title.setText(goal.getTitle());
                if (mExpandedGoal == position+1){
                    holder.iconContainer.setVisibility(View.GONE);
                    holder.description.setVisibility(View.VISIBLE);
                }
                else{
                    holder.description.setVisibility(View.GONE);
                    holder.iconContainer.setVisibility(View.VISIBLE);
                }

                if (!goal.getHTMLDescription().isEmpty()){
                    holder.description.setText(Html.fromHtml(goal.getHTMLDescription(), null,
                            new CompassTagHandler(mContext)));
                }
                else{
                    holder.description.setText(goal.getDescription());
                }

                if (goal.getIconUrl() != null && !goal.getIconUrl().isEmpty()){
                    ImageLoader.loadBitmap(holder.icon, goal.getIconUrl(), new ImageLoader.Options());
                }

                if (mCategory.areCustomTriggersAllowed()){
                    holder.select.setBackgroundResource(R.drawable.circle_white);
                    holder.select.setImageResource(R.drawable.ic_action_new_large);
                    switch (goalStates[position - 1]){
                        case STATE_NOT_ADDED:
                            holder.select.setInactive(false);
                            break;

                        case STATE_ADDING:
                            holder.select.setTransitioningToActive(false);
                            break;

                        case STATE_ADDED:
                            holder.select.setActive(false);
                            break;

                        case STATE_REMOVING:
                            holder.select.setTransitioningToInactive(false);
                            break;
                    }
                }
                else{
                    holder.select.setBackgroundResource(0);
                    holder.select.setImageResource(R.drawable.ic_selected_blue);
                }

                GradientDrawable gradientDrawable = (GradientDrawable)holder.iconContainer.getBackground();
                String colorString = mCategory.getColor();
                if (colorString != null && !colorString.isEmpty()){
                    gradientDrawable.setColor(Color.parseColor(colorString));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                        holder.iconContainer.setBackground(gradientDrawable);
                    }
                    else{
                        holder.iconContainer.setBackgroundDrawable(gradientDrawable);
                    }
                }
            }
        }

        @Override
        public int getItemCount(){
            return mGoals.size();
        }
    }


    /**
     * The ViewHolder for a goal.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    class ChooseGoalsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView title;
        private TextView description;
        private RelativeLayout iconContainer;
        private ImageView icon;
        private TransitionButton select;
        private RelativeLayout detailContainer;
        private TextView header;


        /**
         * Constructor. Extracts the UI components from the root view.
         *
         * @param itemView the root view held by this view holder.
         */
        public ChooseGoalsViewHolder(View itemView){
            super(itemView);

            //Fetch UI components
            icon = (ImageView)itemView.findViewById(R.id.choose_goal_icon);
            iconContainer = (RelativeLayout)itemView.findViewById(R.id.choose_goal_icon_container);
            title = (TextView)itemView.findViewById(R.id.choose_goal_title);
            select = (TransitionButton)itemView.findViewById(R.id.choose_goal_select);
            description = (TextView)itemView.findViewById(R.id.choose_goal_description);
            detailContainer = (RelativeLayout)itemView.findViewById(R.id.choose_goal_detail_container);
            header = (TextView)itemView.findViewById(R.id.choose_goal_header);

            //Listeners
            select.setOnClickListener(this);
            itemView.findViewById(R.id.choose_goal_select_area).setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            //Account for header only
            Goal goal = getItem(getAdapterPosition() - 1);

            if (mCategory.areCustomTriggersAllowed()){
                //Account for the header and the category description
                switch (goalStates[getAdapterPosition() - 2]){
                    case STATE_NOT_ADDED:
                        if (goal.getBehaviorCount() > 0){
                            goalStates[getAdapterPosition() - 2] = STATE_ADDED;
                            notifyItemChanged(getAdapterPosition());
                        }
                        else{
                            goalStates[getAdapterPosition() - 2] = STATE_ADDING;
                            select.setTransitioningToActive();
                            Toast.makeText(mContext, R.string.goal_selected, Toast.LENGTH_SHORT).show();
                        }
                        mListener.onGoalAddClicked(goal);
                        break;

                    case STATE_ADDED:
                        goalStates[getAdapterPosition() - 2] = STATE_REMOVING;
                        select.setTransitioningToInactive();
                        mListener.onGoalDeleteClicked(goal);
                        break;
                }
            }
            else{
                mListener.onGoalOkClicked(goal);
            }
        }
    }

    public void filter(CharSequence constraint){
        if (mFilter != null){
            mFilter.filter(constraint);
        }
    }


    /**
     * Listener interface for the adapter.
     *
     * @author Ismael Alonso
     * @version 1.0.1
     */
    public interface ChooseGoalsListener{
        /**
         * Called when the add button is tapped.
         *
         * @param goal the goal whose add was tapped.
         */
        void onGoalAddClicked(Goal goal);

        /**
         * Called when the delete button is tapped.
         *
         * @param goal the goal whose delete was tapped.
         */
        void onGoalDeleteClicked(Goal goal);

        /**
         * Called when the goal is not removable and the tick is clicked.
         *
         * @param goal the goal whose tick was tapped.
         */
        void onGoalOkClicked(Goal goal);

        /**
         * Called when the RecyclerView scrolls.
         *
         * @param percentage the scroll percentage.
         * @param offset the scroll offset.
         */
        void onScroll(float percentage, float offset);
    }
}
