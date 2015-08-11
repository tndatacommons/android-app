package org.tndata.android.compass.ui;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.util.TypedValue;
import android.view.View;


public class SpacingItemDecoration extends ItemDecoration {
    private Context mContext;
    private int space;


    public SpacingItemDecoration(Context context, int space){
        mContext = context;
        this.space = getPixels(space);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
    }

    /**
     * Converts density pixels to pixels.
     *
     * @param densityPixels the amount of dp to be converted.
     * @return the converter number of pixels.
     */
    private int getPixels(int densityPixels){
        return (int)Math.ceil(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, densityPixels,
                mContext.getResources().getDisplayMetrics()));
    }
}