package org.tndata.android.compass.adapter.feed;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.tndata.android.compass.util.CompassUtil;


/**
 * Decoration class to establish the feed card margin.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public final class MainFeedPadding extends RecyclerView.ItemDecoration{
    private int mMargin;


    /**
     * Constructor.
     *
     * @param context a reference to the context.
     */
    MainFeedPadding(Context context){
        mMargin = CompassUtil.getPixels(context, 12);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
        outRect.top = mMargin / 2;
        outRect.left = mMargin;
        outRect.bottom = mMargin;
        if (parent.getChildLayoutPosition(view) != CardTypes.getItemCount()-1){
            outRect.bottom /= 2;
        }
        outRect.right = mMargin;
    }
}
