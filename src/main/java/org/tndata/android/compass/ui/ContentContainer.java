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
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * UI component that displays content as requested.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ContentContainer<T extends ContentContainer.ContainerDisplayable>
        extends LinearLayout
        implements Animation.AnimationListener{

    private static int sCustomGoalCount = 0;


    //Content list and listener
    private List<ContentHolder> mDisplayedContent;
    private ContentContainerListener<T> mListener;

    //Animation stuff
    private boolean mAnimate;
    private Queue<T> mContentQueue;
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
     * Sets the content container listener.
     *
     * @param listener the new listener.
     */
    public void setListener(@NonNull ContentContainerListener<T> listener){
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
     * Tells whether this container is empty.
     *
     * @return true if it's empty, false otherwise.
     */
    public boolean isEmpty(){
        return mDisplayedContent.isEmpty() && mContentQueue.isEmpty();
    }

    /**
     * Adds a piece of content to the container.
     *
     * @param content the piece of content to be added.
     */
    public void addContent(@NonNull T content){
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
     * Refreshes the content list currently being displayed. Removes the content removed by
     * the user, adds the content added by the user prior to the last displayed piece of
     * content, and updates the content modified by the user.
     *
     * @param dataSet the new data set.
     */
    public void updateContent(@NonNull List<T> dataSet){
        //First off, find the stopping point in the updated list
        T stoppingPoint = null;
        //Start searching from the end of the list, it is more likely that the piece
        //  of content will be there
        for (int i = mDisplayedContent.size()-1; i > 0; i--){
            if (dataSet.contains(mDisplayedContent.get(i).mContent)){
                stoppingPoint = mDisplayedContent.get(i).mContent;
                break;
            }
        }

        //Next, update the list of displayed content
        for (int i = 0; i < dataSet.size(); i++){
            //Update the existing holder or create a new one according to needs
            if (i < mDisplayedContent.size()){
                mDisplayedContent.get(i).update(dataSet.get(i));
            }
            else{
                mDisplayedContent.add(new ContentHolder(dataSet.get(i)));
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
     * Removes a piece of content from the container.
     *
     * @param content the piece of content to be removed.
     */
    public void removeContent(@NonNull T content){
        for (int i = 0; i < mDisplayedContent.size(); i++){
            if (mDisplayedContent.get(i).contains(content)){
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
     * Fires the in animation for the next piece of content in the queue and adds
     * it to the container.
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
     * Fires the out animation for a piece of content in the container. Removal is
     * performed when the animation is done.
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
     * Holder for a piece of content being displayed in the container. The existence of
     * this class facilitates operations on the data set and updating single elements.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class ContentHolder implements OnClickListener{
        //The goal being displayed
        private T mContent;

        //UI components
        private RelativeLayout mIconContainer;
        private ImageView mIcon;
        private TextView mTitle;


        /**
         * Constructor.
         *
         * @param content the piece of content to be bound.
         */
        public ContentHolder(@NonNull T content){
            //Inflate the layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            int layoutResId = R.layout.item_container_content;
            View rootView = inflater.inflate(layoutResId, ContentContainer.this, false);

            //Grab the UI components
            mIconContainer = (RelativeLayout)rootView.findViewById(R.id.content_icon_container);
            mIcon = (ImageView)rootView.findViewById(R.id.content_icon);
            mTitle = (TextView)rootView.findViewById(R.id.content_title);

            //Update the goal
            update(content);

            //Add the view to the container and set listeners
            addView(rootView);
            rootView.setOnClickListener(this);
        }

        /**
         * Replace the piece of content contained by this holder.
         *
         * @param content the new piece of content to be displayed.
         */
        @SuppressWarnings("deprecation")
        private void update(@NonNull T content){
            mContent = content;

            mTitle.setText(mContent.getTitle());

            GradientDrawable gradientDrawable = (GradientDrawable)mIconContainer.getBackground();

            if (content instanceof CustomGoal){
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
                if (content instanceof ContainerGoal){
                    ContainerGoal cg = (ContainerGoal)mContent;
                    gradientDrawable.setColor(Color.parseColor(cg.getColor(getContext())));

                    int margin = CompassUtil.getPixels(getContext(), 20);
                    ((RelativeLayout.LayoutParams)mIcon.getLayoutParams())
                            .setMargins(margin, margin, margin, margin);
                    if (cg.getIconUrl() != null && !cg.getIconUrl().isEmpty()){
                        ImageLoader.loadBitmap(mIcon, cg.getIconUrl());
                    }
                }
                else{
                    gradientDrawable.setColor(Color.TRANSPARENT);
                    ((RelativeLayout.LayoutParams)mIcon.getLayoutParams()).setMargins(0, 0, 0, 0);

                    if (content instanceof ContainerBehavior){
                        ContainerBehavior cb = (ContainerBehavior)mContent;
                        if (cb.getIconUrl() != null && !cb.getIconUrl().isEmpty()){
                            ImageLoader.loadBitmap(mIcon, cb.getIconUrl());
                        }
                    }
                    else if (content instanceof ContainerAction){
                        ContainerAction ca = (ContainerAction)mContent;
                    }
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
            if (mListener != null){
                mListener.onContentClick(mContent);
            }
        }

        /**
         * Checks if this holder contains a particular piece of content.
         *
         * @param content the piece of content to be compared.
         * @return true is the pieces of content are the same, false otherwise.
         */
        public boolean contains(@NonNull T content){
            return mContent.equals(content);
        }
    }


    /**
     * Allows the retrieval of data from different kinds of content objects. The access
     * modifier is package protected because the compiler won't allow private access,
     * however, this interface is not to be extended anywhere outside this class in
     * order to avoid inherently different kinds of model objects to be put in the same
     * list.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    interface ContainerDisplayable{
        /**
         * Getter for titles.
         *
         * @return the title of the piece of content.
         */
        String getTitle();
    }


    /**
     * Used to display any kind of goal in a container.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface ContainerGoal extends ContainerDisplayable{
        /**
         * Returns the background color of the icon container for the goal.
         *
         * @param context a reference to the context.
         * @return a background color as a hex value string.
         */
        String getColor(Context context);

        /**
         * Getter for the icon url.
         *
         * @return the icon url of the piece of content.
         */
        String getIconUrl();
    }


    /**
     * Used to display behaviors in a container.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface ContainerBehavior extends ContainerDisplayable{
        /**
         * Getter for the icon url.
         *
         * @return the icon url of the piece of content.
         */
        String getIconUrl();
    }


    /**
     * Used to display actions in a container.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface ContainerAction extends ContainerDisplayable{
        /**
         * Tells whether the trigger of a particular action is enabled.
         *
         * @return true if the trigger is enabled.
         */
        boolean isTriggerEnabled();
    }


    /**
     * Listener interface for ContentContainer.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface ContentContainerListener<T>{
        /**
         * Called when a piece of content is selected.
         *
         * @param content the selected piece of content.
         */
        void onContentClick(@NonNull T content);
    }
}
