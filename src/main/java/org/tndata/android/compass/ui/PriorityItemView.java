package org.tndata.android.compass.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.MyPrioritiesGoalAdapter;


/**
 * A view containing a priority item in a linear layout.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class PriorityItemView extends LinearLayout{
    private ImageView mImageView;
    private TextView mTextView;

    private MyPrioritiesGoalAdapter.ItemHierarchy mItemHierarchy;


    /**
     * Constructor.
     *
     * @param context the context.
     */
    public PriorityItemView(Context context){
        this(context, null, 0);
    }

    /**
     * Constructor.
     *
     * @param context the context.
     * @param attrs the attribute set.
     */
    public PriorityItemView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    /**
     * Constructor.
     *
     *
     * @param context the context.
     * @param attrs the attribute set.
     * @param defStyle the style resource.
     */
    public PriorityItemView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        initViews();
    }

    /**
     * Creates the layout of the view.
     */
    private void initViews(){
        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int)getContext().getResources().getDimension(R.dimen.my_priorities_item_height)));

        setGravity(Gravity.CENTER_VERTICAL);

        mImageView = new ImageView(getContext());
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(getPixels(25), getPixels(25)));
        addView(mImageView);

        mTextView = new TextView(getContext());
        mTextView.setPadding(getPixels(20), 0, 0, 0);
        mTextView.setTextColor(0xFF666666);
        addView(mTextView);
    }

    /**
     * Attaches to the view the hierarchy of the item it represents.
     *
     * @param itemHierarchy the hierarchy of the item.
     */
    public void setItemHierarchy(MyPrioritiesGoalAdapter.ItemHierarchy itemHierarchy){
        mItemHierarchy = itemHierarchy;
        if (mItemHierarchy.hasAction()){
            setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    /**
     * Sets a left padding to the view.
     *
     * @param densityPixels the amount of padding in density pixels.
     */
    public void setLeftPadding(int densityPixels){
        setPadding(getPixels(densityPixels), getPixels(5), getPixels(12), getPixels(5));
    }

    /**
     * Getter for the ImageView.
     *
     * @return the ImageView of the priority item.
     */
    public ImageView getImageView(){
        return mImageView;
    }

    /**
     * Getter for the TextView.
     *
     * @return the TextView of the priority item.
     */
    public TextView getTextView(){
        return mTextView;
    }

    /**
     * Getter for the item hierarchy.
     *
     * @return the hierarchy of the item it represents.
     */
    public MyPrioritiesGoalAdapter.ItemHierarchy getItemHierarchy(){
        return mItemHierarchy;
    }

    /**
     * Converts density pixels to pixels.
     *
     * @param densityPixels the amount of dp to be converted.
     * @return the converter number of pixels.
     */
    private int getPixels(int densityPixels){
        return (int)Math.ceil(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, densityPixels,
                getContext().getResources().getDisplayMetrics()));
    }
}
