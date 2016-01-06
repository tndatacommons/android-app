package org.tndata.android.compass.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * Custom ViewPager that triggers a swipe out event at the end.
 *
 * @author Damian Urbanczyk
 * @author Edited and documented by Ismael Alonso
 * @version 1.0.0
 */
public class SwipeOutViewPager extends ViewPager{
    private OnSwipeOutListener mListener;
    private float mStartDragX;


    /**
     * Constructor.
     *
     * @param context a reference to the context.
     */
    public SwipeOutViewPager(Context context){
        super(context);
    }

    /**
     * Constructor.
     *
     * @param context a reference to the context.
     * @param attrs the set of attributes of this view.
     */
    public SwipeOutViewPager(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    /**
     * OnSwipeOutListener setter.
     *
     * @param listener the listener.
     */
    public void setOnSwipeOutListener(@NonNull OnSwipeOutListener listener){
        mListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        switch (ev.getAction() & MotionEventCompat.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                //Reset the start position when a down event happens
                mStartDragX = ev.getX();
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev){
        if (getCurrentItem() == 0 || getCurrentItem() == getAdapter().getCount()-1){
            final int action = ev.getAction();
            float x = ev.getX();
            switch (action & MotionEventCompat.ACTION_MASK){
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    if (getCurrentItem() == getAdapter().getCount()-1 && x < mStartDragX){
                        mListener.onSwipeOutAtEnd();
                    }
                    break;
            }
        }
        else{
            mStartDragX = 0;
        }
        return super.onTouchEvent(ev);

    }

    /**
     * Swipe out event listener interface.
     *
     * @author Damian Urbanczyk
     * @author Documented by Ismael Alonso
     * @version 1.0.0
     */
    public interface OnSwipeOutListener{
        /**
         * Called when the user swipes out the last item in the pager.
         */
        void onSwipeOutAtEnd();
    }
}
