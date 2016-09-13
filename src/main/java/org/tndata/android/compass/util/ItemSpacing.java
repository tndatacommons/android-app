package org.tndata.android.compass.util;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 * Class to achieve equal item spacing in RecyclerViews.
 *
 * @author Ismael Alonso
 * @version 1.1.0
 */
public class ItemSpacing extends RecyclerView.ItemDecoration{
    private int mMargin;


    /**
     * Constructor.
     *
     * @param context a reference to the context.
     * @param marginInDp the maximum spacing between items, items, and edges.
     */
    public ItemSpacing(Context context, int marginInDp){
        mMargin = CompassUtil.getPixels(context, marginInDp);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
        outRect.top = mMargin/2;
        outRect.left = mMargin;
        outRect.bottom = mMargin/2;
        outRect.right = mMargin;
        if (parent.getChildAdapterPosition(view) == 0){
            outRect.top = mMargin;
        }
        else if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount()-1){
            outRect.bottom = mMargin;
        }
    }
}
