package org.tndata.android.compass.util;

import android.graphics.Rect;
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
 * @version 3.0.0
 */
public final class ParallaxEffect{
    //The view to which the parallax should be applied and the parallax factor
    private View mParallaxView;
    private float mParallaxFactor;

    //A condition to the parallax and decoration specification
    private Condition mCondition;
    private RecyclerView.ItemDecoration mItemDecoration;

    //The scroll listener
    private RvScrollListener mListener;

    //A stack of heights of previous elements. Needs to be kept because certain items won't be
    //  available if they are not being drawn by the RecyclerView.
    private Stack<Integer> mHeightStack;
    private Stack<Integer> mTopMarginStack;
    private Stack<Integer> mBottomMarginStack;

    //The combined height (plus margin) of the rows that are off-screen. NOTE: This number
    //  will either be 0 or negative because the origin of coordinates is the top left corner
    private int mCombinedDimension;
    //The top margin of the first visible view of the recycler view
    private int mCurrentTop;

    //The top margin of the view that's being parallaxed needs to be taken into account to
    //  scroll it to the right spot
    private int mViewTopMargin;

    //Current item and last recorded item
    private int mCurrent;
    private int mLastRecorded;


    /**
     * Constructor.
     *
     * @param parallaxView   the view to which the parallax should be applied.
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

        mCombinedDimension = 0;
        mCurrent = 0;
        mLastRecorded = 0;

        //Retrieve the top margin of the view being parallaxed
        mViewTopMargin = ((MarginLayoutParams)mParallaxView.getLayoutParams()).topMargin;
    }

    /**
     * Sets a condition that establishes whether the view should be parallaxed.
     *
     * @param condition the condition.
     */
    public void setCondition(@NonNull Condition condition){
        condition.setParallaxEffect(this);
        mCondition = condition;
    }

    /**
     * Attaches the effect to a particular RecyclerView.
     *
     * @param recyclerView the RecyclerView this effect is to be attached to.
     */
    public void attachToRecyclerView(@NonNull RecyclerView recyclerView){
        attachToRecyclerView(recyclerView, null);
    }

    /**
     * Attaches the effect to a particular RecyclerView.
     *
     * @param recyclerView the RecyclerView this effect is to be attached to.
     * @param itemDecoration the item decoration, in any needs to be taken into account.
     */
    public void attachToRecyclerView(@NonNull RecyclerView recyclerView,
                                     @Nullable RecyclerView.ItemDecoration itemDecoration){
        if (mListener != null){
            recyclerView.removeOnScrollListener(mListener);
        }
        mListener = new RvScrollListener();
        recyclerView.addOnScrollListener(mListener);
        mItemDecoration = itemDecoration;
    }

    /**
     * Called when the RecyclerView to which this effect is attached scrolls.
     *
     * @param recyclerView the RecyclerView this effect is attached to.
     */
    private void onScrolled(RecyclerView recyclerView){
        //Record all available heights
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
                MarginLayoutParams params = (MarginLayoutParams)itemView.getLayoutParams();
                mTopMarginStack.push(params.topMargin);
                mBottomMarginStack.push(params.bottomMargin);
            }
            mLastRecorded++;
        }

        //While there are views above the current (scrolling up)
        while (mCurrent > 0 && recyclerView.findViewHolderForLayoutPosition(mCurrent - 1) != null){
            mCurrent -= 1;
            mCombinedDimension += mHeightStack.get(mCurrent);
            mCombinedDimension += mTopMarginStack.get(mCurrent);
            mCombinedDimension += mBottomMarginStack.get(mCurrent);
        }

        //While there are no views below the current (scrolling down)
        while (recyclerView.findViewHolderForLayoutPosition(mCurrent) == null){
            mCombinedDimension -= mHeightStack.get(mCurrent);
            mCombinedDimension -= mTopMarginStack.get(mCurrent);
            mCombinedDimension -= mBottomMarginStack.get(mCurrent);
            mCurrent += 1;
        }

        //Retrieve the margin state of the current view
        mCurrentTop = recyclerView.findViewHolderForLayoutPosition(mCurrent).itemView.getTop();

        //Reposition the view which is subject to the parallax effect
        RelativeLayout.LayoutParams params;
        params = (RelativeLayout.LayoutParams)mParallaxView.getLayoutParams();
        if (!mCondition.scrolls()){
            params.topMargin = mViewTopMargin;
        }
        else{
            params.topMargin = getViewOffset();
        }
        mParallaxView.setLayoutParams(params);
    }

    /**
     * Returns the offset to be set as margin of the view subject to the parallax effect.
     *
     * @return the offset to be set as margin of the view subject to the parallax effect.
     */
    private int getViewOffset(){
        int totalScrollDistance = mCombinedDimension + mCurrentTop + mCondition.mStartState;
        return (int)(totalScrollDistance * mParallaxFactor) + mViewTopMargin;
    }

    /**
     * Returns the total distance scrolled by the scrollable.
     *
     * @return the total distance scrolled by the scrollable. This will be positive.
     */
    private int getScrollableOffset(){
        return -(mCombinedDimension + mCurrentTop);
    }


    /**
     * Listener class that acts as an interface between RecyclerView and ParallaxEffect instances.
     * The rationale of this class is to avoid having ParallaxEffect extend OnScrollListener
     * and have the programmer attach it to a RecyclerView by using the proper methods.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class RvScrollListener extends RecyclerView.OnScrollListener{
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy){
            ParallaxEffect.this.onScrolled(recyclerView);
        }
    }


    /**
     * A condition to scrolling. Evaluates whether the View subject to this effect should
     * scroll or not.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public static class Condition{
        //The start state is the amount of scrolling needed to be done by the scrollable view
        //  triggering the effect before the view on which the effect acts upon starts scrolling.
        private final int mStartState;
        //The effect this condition is associated to
        private ParallaxEffect mParallaxEffect;

        /**
         * Constructor.
         *
         * @param startScrollingAt the amount of scrolling done by the scrollable view before
         *                         the subject starts scrolling.
         */
        public Condition(int startScrollingAt){
            mStartState = startScrollingAt;
        }

        /**
         * Sets the parallax effect this condition is associated with.
         *
         * @param parallaxEffect the parallax effect this condition is associated with.
         */
        private void setParallaxEffect(@NonNull ParallaxEffect parallaxEffect){
            mParallaxEffect = parallaxEffect;
        }

        /**
         * Tells whether a view should scroll given a start state.
         *
         * @return true if it should scroll, false otherwise.
         */
        private boolean scrolls(){
            return mParallaxEffect.getScrollableOffset() >= mStartState;
        }
    }
}
