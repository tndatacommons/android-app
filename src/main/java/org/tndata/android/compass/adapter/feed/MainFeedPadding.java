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
     * @param context the context.
     */
    MainFeedPadding(Context context){
        mMargin = CompassUtil.getPixels(context, 12);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state){

        int position = parent.getChildLayoutPosition(view);

        //Header: padding on top
        if (CardTypes.isTopCard(position)){
            //Log.d("MainFeedPadding", position + ": top");
            outRect.top = mMargin / 2;
            outRect.left = mMargin;
            outRect.bottom = 0;
            outRect.right = mMargin;
        }
        //Last item: padding on the bottom
        else if (CardTypes.isBottomCard(position)){
            //Log.d("MainFeedPadding", position + ": bottom");
            outRect.top = 0;
            outRect.left = mMargin;
            outRect.bottom = mMargin/2;
            outRect.right = mMargin;
        }
        //Inner item: no padding at either side
        else if (CardTypes.isMiddleCard(position)){
            //Log.d("MainFeedPadding", position + ": middle");
            outRect.top = 0;
            outRect.left = mMargin;
            outRect.bottom = 0;
            outRect.right = mMargin;
        }
        //Other cards: padding everywhere
        else{
            //Log.d("MainFeedPadding", position + ": free");
            outRect.top = mMargin / 2;
            outRect.left = mMargin;
            outRect.bottom = mMargin / 2;
            outRect.right = mMargin;
        }
    }
}
