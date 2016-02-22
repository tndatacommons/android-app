package org.tndata.android.compass.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.CustomGoal;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * UI component that displays goals and behaviors as requested.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ContentContainer extends LinearLayout implements Animation.AnimationListener{
    private static int sCustomGoalCount = 0;


    //Goal list and listener
    private List<ContentHolder> mDisplayedContent;
    private ContentContainerListener mListener;

    //Animation stuff
    private boolean mAnimate;
    private Queue<ContainerDisplayable> mContentQueue;
    private int mOutAnimation;


    public ContentContainer(Context context){
        super(context);
        init();
    }

    public ContentContainer(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    public ContentContainer(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initializes the container.
     */
    private void init(){
        setOrientation(VERTICAL);
        mDisplayedContent = new ArrayList<>();
        mAnimate = false;
        mContentQueue = new LinkedList<>();
        mOutAnimation = -1;
    }

    /**
     * Sets the goal listener.
     *
     * @param listener the new listener.
     */
    public void setGoalListener(@NonNull ContentContainerListener listener){
        mListener = listener;
    }

    /**
     * Enables or disables animations.
     *
     * @param enabled true to enable animations, false to disable them.
     */
    public void setAnimationsEnabled(boolean enabled){
        mAnimate = enabled;
    }

    /**
     * Gets the number of items either displayed or queued to be displayed.
     *
     * @return the number of goals added to this container.
     */
    public int getCount(){
        return mDisplayedContent.size() + (mContentQueue.isEmpty() ? 0 : mContentQueue.size()-1);
    }

    /**
     * Adds a goal to the container.
     *
     * @param content the goal to be added.
     */
    public void addGoal(@NonNull ContainerDisplayable content){
        if (mAnimate){
            mContentQueue.add(content);
            if (mContentQueue.size() == 1){
                inAnimation();
            }
        }
        else{
            mDisplayedContent.add(new ContentHolder(content));
        }
    }

    /**
     * Refreshes the list of goals that are currently being displayed. Removes the goals
     * that the user has removed, adds the goals that the user has added prior to the last
     * goal being displayed, and updates the goals modified by the user.
     *
     * @param feedData a reference to the feed data bundle.
     */
    public void updateContent(@NonNull FeedData feedData){
        //First off, find the stopping point in the updated list
        ContainerDisplayable stoppingPoint = null;
        //Start searching from the end of the list, it is more likely that the goal will be there
        for (int i = mDisplayedContent.size()-1; i > 0; i--){
            if (feedData.getGoals().contains(mDisplayedContent.get(i).mContent)){
                stoppingPoint = mDisplayedContent.get(i).mContent;
                break;
            }
        }

        //Next, update the list of displayed goals
        for (int i = 0; i < feedData.getGoals().size(); i++){
            //Update the existing holder or create a new one according to needs
            if (i < mDisplayedContent.size()){
                mDisplayedContent.get(i).update(feedData.getGoals().get(i));
            }
            else{
                mDisplayedContent.add(new ContentHolder(feedData.getGoals().get(i)));
            }
            //If the stopping point has been reached
            if (stoppingPoint != null && stoppingPoint.equals(mDisplayedContent.get(i).mContent)){
                //Remove all the holders after it, if any
                i++;
                while (i < mDisplayedContent.size()){
                    mDisplayedContent.remove(i);
                    removeViewAt(i);
                }
                break;
            }
        }
    }

    /**
     * Removes a goal from the container.
     *
     * @param goal the goal to be removed.
     */
    public void removeGoal(@NonNull ContainerDisplayable goal){
        for (int i = 0; i < mDisplayedContent.size(); i++){
            if (mDisplayedContent.get(i).contains(goal)){
                if (mAnimate){
                    outAnimation(i);
                }
                else{
                    mDisplayedContent.remove(i);
                    removeViewAt(i);
                }
                break;
            }
        }
    }

    /**
     * Fires the in animation for the next goal in the queue and adds it to the container.
     */
    private void inAnimation(){
        mDisplayedContent.add(new ContentHolder(mContentQueue.peek()));

        View view = getChildAt(getChildCount() - 1);
        view.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        int targetHeight = view.getMeasuredHeight();
        view.getLayoutParams().height = 1;

        Animation animation = new ExpandCollapseAnimation(view, targetHeight, true);

        //2ms/dp
        int length = (int)(2*targetHeight/getContext().getResources().getDisplayMetrics().density);
        animation.setDuration(length);
        animation.setAnimationListener(this);
        view.startAnimation(animation);
    }

    /**
     * Fires the out animation for a goal in the container. Removal is performed when
     * the animation is done.
     *
     * @param position the position of the goal to be removed.
     */
    private void outAnimation(int position){
        mOutAnimation = position;

        View view = getChildAt(position);
        int initialHeight = view.getMeasuredHeight();

        Animation animation = new ExpandCollapseAnimation(view, initialHeight, false);

        //1dp/ms
        int length = (int)(initialHeight/getContext().getResources().getDisplayMetrics().density);
        animation.setDuration(length);
        animation.setAnimationListener(this);
        view.startAnimation(animation);
    }

    @Override
    public void onAnimationStart(Animation animation){
        //Unused
    }

    @Override
    public void onAnimationEnd(Animation animation){
        if (mOutAnimation != -1){
            mDisplayedContent.remove(mOutAnimation);
            removeViewAt(mOutAnimation);
            mOutAnimation = -1;
        }
        else{
            mContentQueue.remove();
            if (!mContentQueue.isEmpty()){
                inAnimation();
            }
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation){
        //Unused
    }


    /**
     * Holder for a goal being displayed in the container. The existence of this class
     * facilitates operations on the data set and updating single elements.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class ContentHolder implements OnClickListener{
        //The goal being displayed
        private ContainerDisplayable mContent;

        //UI components
        private RelativeLayout mIconContainer;
        private ImageView mIcon;
        private TextView mTitle;


        /**
         * Constructor.
         *
         * @param goal the goal to be bound
         */
        public ContentHolder(@NonNull ContainerDisplayable goal){
            //Inflate the layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View rootView = inflater.inflate(R.layout.item_feed_goal, ContentContainer.this, false);

            //Grab the UI components
            mIconContainer = (RelativeLayout)rootView.findViewById(R.id.goal_icon_container);
            mIcon = (ImageView)rootView.findViewById(R.id.goal_icon);
            mTitle = (TextView)rootView.findViewById(R.id.goal_title);

            //Update the goal
            update(goal);

            //Add the view to the container and set listeners
            addView(rootView);
            rootView.setOnClickListener(this);
        }

        /**
         * Replace the goal contained by this holder.
         *
         * @param goal the new goal to be displayed.
         */
        @SuppressWarnings("deprecation")
        private void update(@NonNull ContainerDisplayable goal){
            mContent = goal;

            mTitle.setText(mContent.getTitle());

            GradientDrawable gradientDrawable = (GradientDrawable)mIconContainer.getBackground();

            if (goal instanceof CustomGoal){
                gradientDrawable.setColor(Color.TRANSPARENT);
                ((RelativeLayout.LayoutParams)mIcon.getLayoutParams()).setMargins(0, 0, 0, 0);
                if (sCustomGoalCount++ % 2 == 0){
                    mIcon.setImageResource(R.drawable.ic_lady);
                }
                else{
                    mIcon.setImageResource(R.drawable.ic_guy);
                }
            }
            else{
                gradientDrawable.setColor(Color.parseColor(mContent.getColor(getContext())));
                int margin = CompassUtil.getPixels(getContext(), 20);
                ((RelativeLayout.LayoutParams)mIcon.getLayoutParams()).setMargins(margin, margin, margin, margin);
                if (mContent.getIconUrl() != null && !mContent.getIconUrl().isEmpty()){
                    ImageLoader.loadBitmap(mIcon, mContent.getIconUrl());
                }
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
                mIcon.setBackgroundDrawable(gradientDrawable);
            }
            else{
                mIconContainer.setBackground(gradientDrawable);
            }
        }

        @Override
        public void onClick(View v){
            mListener.onContentClick(mContent);
        }

        /**
         * Checks if this holder contains a particular goal.
         *
         * @param goal the goal to be compared.
         * @return true is the goals are the same, false otherwise.
         */
        public boolean contains(@NonNull ContainerDisplayable goal){
            return mContent.equals(goal);
        }
    }


    /**
     * Allows the retrieval of data from different kinds of content objects
     * in an homogeneous way to be displayed in a container.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface ContainerDisplayable{
        /**
         * Getter for titles.
         *
         * @return the title of the goal.
         */
        String getTitle();

        /**
         * Getter for the icon url.
         *
         * @return the icon url of the goal or the empty string if a default icon is to be used.
         */
        String getIconUrl();

        /**
         * Returns the background color of the icon container for the goal.
         *
         * @param context a reference to the context.
         * @return a background color as a hex value string.
         */
        String getColor(Context context);
    }


    /**
     * Listener interface for ContentContainer.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface ContentContainerListener{
        /**
         * Called when a piece of content is selected.
         *
         * @param content the selected piece of content.
         */
        void onContentClick(@NonNull ContainerDisplayable content);
    }
}
