package org.tndata.android.compass.adapter;

import java.util.List;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.DrawerItem;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * The adapter for the list of options in the navigation drawer.
 *
 * @author Edited by Ismael Alonso
 * @version 2.0.0
 */
public class DrawerAdapter extends RecyclerView.Adapter{
    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_ITEM = 2;


    private final Context mContext;
    private final OnItemClickListener mListener;
    private final List<DrawerItem> mItems;


    /**
     * Constructor.
     *
     * @param context the application context.
     * @param listener the class object who will listen to click events.
     * @param items the list of items to be displayed in the drawer list.
     */
    public DrawerAdapter(Context context, OnItemClickListener listener, List<DrawerItem> items){
        mContext = context;
        mListener = listener;
        mItems = items;
    }

    /**
     * Gets the item belonging to the requested layout position.
     *
     * @param position the position in the layout, not in the list.
     * @return the item belonging to that position.
     */
    public DrawerItem getItem(int position){
        return mItems.get(position-1);
    }

    @Override
    @SuppressWarnings("deprecation")
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (viewType == VIEW_TYPE_HEADER){
            ImageView header = new ImageView(mContext);
            header.setLayoutParams(new LinearLayout.LayoutParams(getPixels(240), getPixels(160)));
            header.setScaleType(ImageView.ScaleType.FIT_CENTER);

            return new HeaderViewHolder(header);
        }

        LinearLayout.LayoutParams params;
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView item = new TextView(mContext);
        item.setLayoutParams(params);
        item.setGravity(Gravity.CENTER_VERTICAL);
        item.setPadding(getPixels(16), getPixels(10), getPixels(16), getPixels(10));
        item.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        int[] attrs = new int[]{android.R.attr.selectableItemBackground};
        TypedArray array = mContext.obtainStyledAttributes(attrs);
        Drawable feedback = array.getDrawable(0);
        array.recycle();
        if (Build.VERSION.SDK_INT < 16){
            item.setBackgroundDrawable(feedback);
        }
        else{
            item.setBackground(feedback);
        }

        return new ItemViewHolder(item);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        if (getItemViewType(position) == VIEW_TYPE_HEADER){
            ((HeaderViewHolder)holder).mHeader.setImageResource(R.drawable.compass_master_illustration);
        }
        else{
            DrawerItem item = getItem(position);

            TextView itemView = ((ItemViewHolder)holder).mItem;
            itemView.setText(item.text);
            if (item.drawable != null){
                itemView.setCompoundDrawablesWithIntrinsicBounds(item.drawable, null, null, null);
                itemView.setCompoundDrawablePadding(getPixels(10));
            }
            else{
                itemView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                itemView.setCompoundDrawablePadding(0);
            }
        }
    }

    @Override
    public int getItemCount(){
        return mItems.size()+1;
    }

    @Override
    public int getItemViewType(int position){
        if (position == 0){
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_ITEM;
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


    /**
     * Holder for the header item.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class HeaderViewHolder extends RecyclerView.ViewHolder{
        private ImageView mHeader;


        /**
         * Constructor.
         *
         * @param itemView the header view.
         */
        public HeaderViewHolder(ImageView itemView){
            super(itemView);
            mHeader = itemView;
        }
    }


    /**
     * Holder for a regular item.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mItem;


        /**
         * Constructor.
         *
         * @param itemView the item view.
         */
        public ItemViewHolder(TextView itemView){
            super(itemView);
            mItem = itemView;
            mItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            mListener.onItemClick(getLayoutPosition()-1);
        }
    }


    /**
     * Listener interface for touch events.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface OnItemClickListener{
        /**
         * Called when a clickable element is tapped.
         *
         * @param position the position in the list.
         */
        void onItemClick(int position);
    }
}
