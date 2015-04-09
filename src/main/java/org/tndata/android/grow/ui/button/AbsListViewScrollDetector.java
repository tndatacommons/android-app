package org.tndata.android.grow.ui.button;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

abstract class AbsListViewScrollDetector implements
        OnScrollListener {
    private int mLastScrollY;
    private int mPreviousFirstVisibleItem;
    private AbsListView mListView;
    private int mScrollThreshold;
    private OnScrollListener mScrollListener;

    abstract void onScrollUp();

    abstract void onScrollDown();

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mScrollListener.onScrollStateChanged(view, scrollState);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        if (isSameRow(firstVisibleItem)) {
            int newScrollY = getTopItemScrollY();
            boolean isSignificantDelta = Math.abs(mLastScrollY - newScrollY) > mScrollThreshold;
            if (isSignificantDelta) {
                if (mLastScrollY > newScrollY) {
                    onScrollUp();
                } else {
                    onScrollDown();
                }
            }
            mLastScrollY = newScrollY;
        } else {
            if (firstVisibleItem > mPreviousFirstVisibleItem) {
                onScrollUp();
            } else {
                onScrollDown();
            }

            mLastScrollY = getTopItemScrollY();
            mPreviousFirstVisibleItem = firstVisibleItem;
        }
        mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
                totalItemCount);
    }

    public void setScrollThreshold(int scrollThreshold) {
        mScrollThreshold = scrollThreshold;
    }

    public void setListView(@NonNull AbsListView listView) {
        mListView = listView;
    }

    public void setOnScrollListener(OnScrollListener listener) {
        mScrollListener = listener;
    }

    private boolean isSameRow(int firstVisibleItem) {
        return firstVisibleItem == mPreviousFirstVisibleItem;
    }

    private int getTopItemScrollY() {
        if (mListView == null || mListView.getChildAt(0) == null)
            return 0;
        View topChild = mListView.getChildAt(0);
        return topChild.getTop();
    }
}