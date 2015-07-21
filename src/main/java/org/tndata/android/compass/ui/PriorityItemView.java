package org.tndata.android.compass.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.tndata.android.compass.adapter.MyPrioritiesGoalAdapter;


/**
 * Created by isma on 7/17/15.
 */
public class PriorityItemView extends LinearLayout{
    private ImageView mImageView;
    private TextView mTextView;

    private MyPrioritiesGoalAdapter.ItemHierarchy mItemHierarchy;


    public PriorityItemView(Context context){
        this(context, null, 0);
    }

    public PriorityItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PriorityItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    private void initViews(){
        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getPixels(35)));

        setGravity(Gravity.CENTER_VERTICAL);

        mImageView = new ImageView(getContext());
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(getPixels(25), getPixels(25)));
        addView(mImageView);

        mTextView = new TextView(getContext());
        mTextView.setPadding(getPixels(20), 0, 0, 0);
        mTextView.setTextColor(0xFF666666);
        addView(mTextView);
    }

    public void setItemHierarchy(MyPrioritiesGoalAdapter.ItemHierarchy itemHierarchy){
        mItemHierarchy = itemHierarchy;
    }

    public void setLeftPadding(int densityPixels){
        setPadding(getPixels(densityPixels), getPixels(5), 0, getPixels(5));
    }

    public ImageView getImageView(){
        return mImageView;
    }

    public TextView getTextView(){
        return mTextView;
    }

    public MyPrioritiesGoalAdapter.ItemHierarchy getItemHierarchy(){
        return mItemHierarchy;
    }

    private int getPixels(int densityPixels){
        return (int)Math.ceil(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, densityPixels,
                getContext().getResources().getDisplayMetrics()));
    }
}
