package org.tndata.android.compass.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.RelativeLayout;

import java.util.Stack;


/**
 * Creates a parallax effect on the provided view.
 *
 * @author Ismael Alonso
 * @version 2.0.0
 */
public final class ParallaxEffect extends RecyclerView.OnScrollListener{
    //The view to which the parallax should be applied and the parallax factor
    private View mParallaxView;
    private float mParallaxFactor;

    //A condition to the parallax
    private ParallaxCondition mParallaxCondition;

    //A stack of heights needs to be kept
    private Stack<Integer> mHeightStack;

    //The combined height of the rows that are off-screen. NOTE: This number will either
    //  be 0 or negative because the origin of coordinates is the top left corner
    private int mPreviousMargin;
    //The top margin of the view atop the recycler view
    private int mTopState;
    //The top margin of the first element is considered part of the top attribute, and needs
    //  to be taken out of the equation
    private int mInitialMargin;
    //The top margin of the view that's being parallaxed needs to be taken into account as well
    private int mParallaxViewInitialMargin;

    //Current item and last recorded item
    private int mCurrent;
    private int mLastRecorded;


    /**
     * Constructor.
     *
     * @param parallaxView the view to which the parallax should be applied.
     * @param parallaxFactor the parallax effect factor.
     */
    public ParallaxEffect(@NonNull View parallaxView, float parallaxFactor){
        mParallaxView = parallaxView;
        mParallaxFactor = parallaxFactor > 1 ? 1 : parallaxFactor;

        mParallaxCondition = null;

        mHeightStack = new Stack<>();
        mPreviousMargin = 0;
        mCurrent = 0;
        mLastRecorded = 0;

        //Retrieve the top margin of the view being parallaxed
        mParallaxViewInitialMargin = ((MarginLayoutParams)mParallaxView.getLayoutParams()).topMargin;
    }

    public void setParallaxCondition(@Nullable ParallaxCondition parallaxCondition){
        mParallaxCondition = parallaxCondition;
        if (mParallaxCondition != null){
            mParallaxCondition.mParallaxEffect = this;
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy){
        //If this is the first call
        if (mLastRecorded == 0){
            //The initial margin is recorded
            mInitialMargin = recyclerView.findViewHolderForLayoutPosition(mCurrent).itemView.getTop();
        }

        //Record all available heights.
        while (recyclerView.findViewHolderForLayoutPosition(mLastRecorded) != null){
            View itemView = recyclerView.findViewHolderForLayoutPosition(mLastRecorded).itemView;
            mHeightStack.push(itemView.getHeight());
            mLastRecorded++;
        }

        //While there are views above the current
        while (mCurrent > 0 && recyclerView.findViewHolderForLayoutPosition(mCurrent-1) != null){
            mCurrent -= 1;
            mPreviousMargin += mHeightStack.get(mCurrent);
        }

        //While there are not views below the current
        while (recyclerView.findViewHolderForLayoutPosition(mCurrent) == null){
            mPreviousMargin -= mHeightStack.get(mCurrent);
            mCurrent += 1;
        }

        //Retrieve the margin state of the current view
        mTopState = recyclerView.findViewHolderForLayoutPosition(mCurrent).itemView.getTop();

        RelativeLayout.LayoutParams params;
        params = (RelativeLayout.LayoutParams)mParallaxView.getLayoutParams();
        if (mParallaxCondition != null && !mParallaxCondition.doParallax()){
            params.topMargin = mParallaxCondition.getFixedState();
            mParallaxCondition.onStateChanged(params.topMargin);
        }
        else{
            params.topMargin = getParallaxViewOffset();
            if (mParallaxCondition != null){
                mParallaxCondition.onStateChanged(params.topMargin);
            }
        }
        mParallaxView.setLayoutParams(params);
    }

    private int getParallaxViewOffset(){
        return (int)((mPreviousMargin + mTopState - mInitialMargin) * mParallaxFactor) + mParallaxViewInitialMargin;
    }

    public static abstract class ParallaxCondition{
        private ParallaxEffect mParallaxEffect;

        protected final int getParallaxViewOffset(){
            return mParallaxEffect.getParallaxViewOffset();
        }

        protected final int getParallaxViewInitialOffset(){
            return mParallaxEffect.mParallaxViewInitialMargin;
        }

        protected final int getRecyclerViewOffset(){
            return mParallaxEffect.mPreviousMargin+mParallaxEffect.mTopState;
        }

        protected abstract boolean doParallax();

        protected int getFixedState(){
            return 0;
        }

        protected void onStateChanged(int newMargin){

        }

        protected View getParallaxView(){
            return mParallaxEffect.mParallaxView;
        }
    }
}
