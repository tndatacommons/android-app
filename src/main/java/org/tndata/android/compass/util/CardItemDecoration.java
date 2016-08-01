package org.tndata.android.compass.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 * Class to achieve equal card spacing.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CardItemDecoration extends RecyclerView.ItemDecoration{
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
        outRect.top = CompassUtil.getPixels(parent.getContext(), 6);
        outRect.left = CompassUtil.getPixels(parent.getContext(), 12);
        outRect.bottom = CompassUtil.getPixels(parent.getContext(), 6);
        outRect.right = CompassUtil.getPixels(parent.getContext(), 12);
        if (parent.getChildAdapterPosition(view) == 0){
            outRect.top = CompassUtil.getPixels(parent.getContext(), 12);
        }
        else if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount()-1){
            outRect.bottom = CompassUtil.getPixels(parent.getContext(), 6);
        }
    }
}
