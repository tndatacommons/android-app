package org.tndata.android.compass.ui;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.view.View;


public class SpacingItemDecoration extends ItemDecoration {
    private int space;

    public SpacingItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
    }
}