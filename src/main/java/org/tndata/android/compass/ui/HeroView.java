package org.tndata.android.compass.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;


/**
 * A view with an aspect ratio of 3 by 2 able to hold a single child.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class HeroView extends FrameLayout{
    private Point size;


    /**
     * Constructor.
     *
     * @param context the context.
     */
    public HeroView(Context context){
        super(context);
        size = new Point();
    }

    /**
     * Constructor.
     *
     * @param context the context.
     * @param attrs the attribute set.
     */
    public HeroView(Context context, AttributeSet attrs){
        super(context, attrs);
        size = new Point();
    }

    /**
     * Constructor.
     *
     * @param context the context.
     * @param attrs the attribute set.
     * @param defStyleAttr the style definition id.
     */
    public HeroView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        size = new Point();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getSize(size);
        size.y = size.x*2/3;
        for (int i = 0; i < getChildCount(); i++){
            View child = getChildAt(i);
            child.measure(size.x, size.y);
        }
        setMeasuredDimension(size.x, size.y);
    }
}
