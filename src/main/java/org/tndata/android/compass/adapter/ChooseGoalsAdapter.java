package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.filter.GoalFilter;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.GoalContent;
import org.tndata.android.compass.util.CompassTagHandler;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.ImageLoader;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter for the goal picker.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ChooseGoalsAdapter extends RecyclerView.Adapter{
    private static final int TYPE_BLANK = 0;
    private static final int TYPE_DESCRIPTION = TYPE_BLANK+1;
    private static final int TYPE_GOAL = TYPE_DESCRIPTION+1;


    private Context mContext;
    private ChooseGoalsListener mListener;
    private CategoryContent mCategory;

    private List<GoalContent> mGoals;
    private GoalFilter mFilter;


    /**
     * Constructor.
     *
     * @param context the application context.
     * @param listener the event listener.
     * @param category the category from where the goals are pulled.
     */
    public ChooseGoalsAdapter(@NonNull Context context, @NonNull ChooseGoalsListener listener,
                              @NonNull CategoryContent category){

        mContext = context;
        mListener = listener;
        mCategory = category;

        mGoals = new ArrayList<>();
        mFilter = new GoalFilter(this);
    }

    @Override
    public int getItemViewType(int position){
        if (position == 0){
            return TYPE_BLANK;
        }
        else if (position == 1){
            return TYPE_DESCRIPTION;
        }
        else{
            return TYPE_GOAL;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (viewType == TYPE_BLANK){
            return new RecyclerView.ViewHolder(new CardView(mContext)){};
        }
        else if (viewType == TYPE_DESCRIPTION){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View rootView = inflater.inflate(R.layout.card_library_description, parent, false);
            return new DescriptionViewHolder(mContext, rootView);
        }
        else{
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View rootView = inflater.inflate(R.layout.card_library_goal, parent, false);
            return new GoalViewHolder(rootView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
        //Blank space
        if (position == 0){
            int width = CompassUtil.getScreenWidth(mContext);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (int)((width*2/3)*0.8)
            );
            rawHolder.itemView.setLayoutParams(params);
            rawHolder.itemView.setVisibility(View.INVISIBLE);
        }
        //Otherwise, handle all other cards
        else if (position == 1){
            ((DescriptionViewHolder)rawHolder).bind(mCategory);
        }
        else{
            ((GoalViewHolder)rawHolder).bind(mGoals.get(position-2));
        }
    }

    @Override
    public int getItemCount(){
        return mGoals.size()+2;
    }

    /**
     * Adds the goals in the list to the backing array.
     *
     * @param goals the list of goals to be added.
     */
    public void addGoals(@NonNull List<GoalContent> goals){
        mGoals = goals;
    }

    public void update(){
        mFilter.setGoalList(mGoals);
        notifyDataSetChanged();
    }

    public GoalFilter getFilter(){
        return mFilter;
    }


    /**
     * View holder for a description card.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    static class DescriptionViewHolder extends RecyclerView.ViewHolder{
        private Context mContext;
        private TextView mCategoryTitle;
        private TextView mCategoryDescription;


        /**
         * Constructor.
         *
         * @param context a reference to the context.
         * @param rootView the view to be drawn.
         */
        public DescriptionViewHolder(@NonNull Context context, @NonNull View rootView){
            super(rootView);

            mContext = context;
            mCategoryTitle = (TextView)rootView.findViewById(R.id.library_description_title);
            mCategoryDescription = (TextView)rootView.findViewById(R.id.library_description_content);
        }

        /**
         * Binds a category to the holder.
         *
         * @param category the category whose description is to be drawn.
         */
        public void bind(@NonNull CategoryContent category){
            mCategoryTitle.setText(category.getTitle());
            if (!category.getHTMLDescription().isEmpty()){
                mCategoryDescription.setText(Html.fromHtml(category.getHTMLDescription(), null,
                        new CompassTagHandler(mContext)));
            }
            else{
                mCategoryDescription.setText(category.getDescription());
            }
        }
    }


    /**
     * The ViewHolder for a goal.
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
            mIconContainer = (RelativeLayout)rootView.findViewById(R.id.library_goal_icon_container);
            mIcon = (ImageView)rootView.findViewById(R.id.library_goal_icon);
            mTitle = (TextView)rootView.findViewById(R.id.library_goal_title);

            //Listeners
            itemView.setOnClickListener(this);
        }

        @SuppressWarnings("deprecation")
        public void bind(@NonNull GoalContent goal){
            String colorString = mCategory.getColor();
            if (colorString != null && !colorString.isEmpty()){
                GradientDrawable gradientDrawable = (GradientDrawable)mIconContainer.getBackground();
                gradientDrawable.setColor(Color.parseColor(colorString));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                    mIconContainer.setBackground(gradientDrawable);
                }
                else{
                    mIconContainer.setBackgroundDrawable(gradientDrawable);
                }
            }
            if (goal.getIconUrl() != null && !goal.getIconUrl().isEmpty()){
                ImageLoader.loadBitmap(mIcon, goal.getIconUrl());
            }

            mTitle.setText(goal.getTitle());
        }

        @Override
        public void onClick(View v){
            mListener.onGoalSelected(mGoals.get(getAdapterPosition()-2));
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
        void onGoalSelected(@NonNull GoalContent goal);
    }
}
