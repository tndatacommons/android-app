package org.tndata.android.compass.util;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.RelativeLayout;

import java.util.Stack;


/**
 * Creates a parallax effect on the provided view.
 *
 * TODO this is a class I've been adapting to new needs every time something new needed to
 * TODO be done, therefore it's gotten real messy over the months. It needs proper design
 * TODO and extended scrollable types support.
 *
 * @author Ismael Alonso
 * @version 2.0.0
 */
public final class ParallaxEffect extends RecyclerView.OnScrollListener{
    //The view to which the parallax should be applied and the parallax factor
    private View mParallaxView;
    private float mParallaxFactor;

    //A condition to the parallax
    private Condition mCondition;

    //The decoration specification, to calculate margins
    private RecyclerView.ItemDecoration mItemDecoration;

    //A stack of heights needs to be kept
    private Stack<Integer> mHeightStack;
    private Stack<Integer> mTopMarginStack;
    private Stack<Integer> mBottomMarginStack;

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

        mCondition = new Condition(0);
        mCondition.setParallaxEffect(this);

        mHeightStack = new Stack<>();
        mTopMarginStack = new Stack<>();
        mBottomMarginStack = new Stack<>();

        mPreviousMargin = 0;
        mCurrent = 0;
        mLastRecorded = 0;

        //Retrieve the top margin of the view being parallaxed
        mParallaxViewInitialMargin = ((MarginLayoutParams)mParallaxView.getLayoutParams()).topMargin;

        Thread.dumpStack();
        Log.d("ParallaxEffect", "Initial margin: " + mParallaxViewInitialMargin);
    }

    public void setCondition(@NonNull Condition condition){
        condition.setParallaxEffect(this);
        mCondition = condition;
    }

    public void attachToRecyclerView(@NonNull RecyclerView recyclerView,
                                     @Nullable RecyclerView.ItemDecoration itemDecoration){
        //TODO
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
            if (mItemDecoration != null){
                Rect rect = new Rect();
                mItemDecoration.getItemOffsets(rect, itemView, recyclerView, null);
                mTopMarginStack.push(rect.top);
                mBottomMarginStack.push(rect.bottom);
            }
            else{
                mTopMarginStack.push(0);
                mBottomMarginStack.push(0);
            }
            mLastRecorded++;
        }

        //While there are views above the current (scrolling up)
        while (mCurrent > 0 && recyclerView.findViewHolderForLayoutPosition(mCurrent-1) != null){
            mCurrent -= 1;
            mPreviousMargin += mHeightStack.get(mCurrent);
            mPreviousMargin += mTopMarginStack.get(mCurrent);
            mPreviousMargin += mBottomMarginStack.get(mCurrent);
        }

        //While there are no views below the current (scrolling down)
        while (recyclerView.findViewHolderForLayoutPosition(mCurrent) == null){
            mPreviousMargin -= mHeightStack.get(mCurrent);
            mPreviousMargin -= mTopMarginStack.get(mCurrent);
            mPreviousMargin -= mBottomMarginStack.get(mCurrent);
            mCurrent += 1;
        }

        //Retrieve the margin state of the current view
        mTopState = recyclerView.findViewHolderForLayoutPosition(mCurrent).itemView.getTop();

        RelativeLayout.LayoutParams params;
        params = (RelativeLayout.LayoutParams)mParallaxView.getLayoutParams();
        if (!mCondition.scrolls()){
            params.topMargin = mParallaxViewInitialMargin;// mCondition.getFixedState();
        }
        else{
            params.topMargin = getParallaxViewOffset();
            Log.d("ParallaxEffect", "Top margin: " + params.topMargin);
            /*if (mParallaxCondition != null){
                params.topMargin = mParallaxCondition.getParallaxViewOffset();
            }*/
        }
        mParallaxView.setLayoutParams(params);
    }

    private int getParallaxViewOffset(){
        return (int)((mPreviousMargin + mTopState + mCondition.mStartState - mInitialMargin) * mParallaxFactor) + mParallaxViewInitialMargin;
    }

    private int getScrollableOffset(){
        return -(mPreviousMargin + mTopState);
    }


    public static class Condition{
        //The start state is the amount of scrolling needed to be done by the scrollable view
        //  triggering the effect before the view on which the effect acts upon starts scrolling.
        private final int mStartState;

        private ParallaxEffect mParallaxEffect;


        /**
         * Constructor. Defaults the fixed state and the start state both to 0.
         */
        public Condition(int startScrollingAt){
            mStartState = startScrollingAt;
        }

        private void setParallaxEffect(@NonNull ParallaxEffect parallaxEffect){
            mParallaxEffect = parallaxEffect;
        }

        private boolean scrolls(){
            return mParallaxEffect.getScrollableOffset() >= mStartState;
        }
    }
}
