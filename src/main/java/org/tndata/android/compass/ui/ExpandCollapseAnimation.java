package org.tndata.android.compass.ui;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;


/**
 * Animation to create expand and collapse effects.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ExpandCollapseAnimation extends Animation{
    private View mView;
    private int mHeight;
    private boolean mExpand;


    /**
     * Constructor.
     *
     * @param view the target view.
     * @param height the target height in case of expand, the initial height in case of collapse.
     * @param expand true to expand, false to collapse.
     */
    public ExpandCollapseAnimation(View view, int height, boolean expand){
        mView = view;
        mHeight = height;
        mExpand = expand;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t){
        if (mExpand){
            mView.getLayoutParams().height = (interpolatedTime == 1)
                    ? LinearLayout.LayoutParams.WRAP_CONTENT
                    : (int)(mHeight*interpolatedTime);
            mView.requestLayout();
        }
        else{
            if (interpolatedTime != 1){
                mView.getLayoutParams().height = mHeight - (int)(mHeight * interpolatedTime);
                mView.requestLayout();
            }
        }
    }

    @Override
    public boolean willChangeBounds(){
        return true;
    }
}
