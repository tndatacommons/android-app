package org.tndata.android.compass.adapter;

import java.util.ArrayList;
import java.util.List;

import org.tndata.android.compass.BuildConfig;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.DrawerItem;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.util.CompassUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public static final int IMPORTANT_TO_ME = 0;
    public static final int MY_PRIORITIES = 1;
    public static final int MYSELF = 2;
    public static final int PLACES = 3;
    public static final int MY_PRIVACY = 4;
    public static final int TOUR = 5;
    public static final int SETTINGS = 6;
    public static final int DRAWER_COUNT = 7;


    private final Context mContext;
    private final OnItemClickListener mListener;
    private final List<DrawerItem> mItems;


    /**
     * Constructor.
     *
     * @param context the application context.
     * @param listener the class object who will listen to click events.
     */
    public DrawerAdapter(Context context, OnItemClickListener listener){
        mContext = context;
        mListener = listener;
        mItems = getItemList();

        if (BuildConfig.DEBUG){
            mItems.add(new DrawerItem("Debug"));
        }
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
        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (viewType == VIEW_TYPE_HEADER){
            return new HeaderViewHolder(inflater.inflate(R.layout.item_drawer_header, parent, false));
        }

        View root = inflater.inflate(R.layout.item_drawer_item, parent, false);
        ItemViewHolder holder = new ItemViewHolder((TextView)root);

        holder.mItem.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Medium.ttf"));

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
        if (getItemViewType(position) == VIEW_TYPE_HEADER){
            User user = ((CompassApplication)((Activity)mContext).getApplication()).getUser();

            HeaderViewHolder holder = (HeaderViewHolder)rawHolder;
            holder.mName.setText(user.getFullName());
            holder.mAddress.setText(user.getEmail());
        }
        else{
            DrawerItem item = getItem(position);

            TextView itemView = ((ItemViewHolder)rawHolder).mItem;
            itemView.setText(item.getCaption());
            itemView.setCompoundDrawablesWithIntrinsicBounds(item.getIconResId(), 0, 0, 0);
            if (item.getIconResId() == 0){
                itemView.setCompoundDrawablePadding(0);
            }
            else{
                itemView.setCompoundDrawablePadding(CompassUtil.getPixels(mContext, 32));
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
     * ItemPadding specification getter.
     *
     * @param context the application context.
     * @return the item decoration object that specify the drawer item padding.
     */
    public static ItemPadding getItemPadding(Context context){
        return new ItemPadding(context);
    }

    /**
     * Creates the list of drawer items.
     *
     * @return the list of drawer items.
     */
    private List<DrawerItem> getItemList(){
        List<DrawerItem> items = new ArrayList<>();
        for (int i = 0; i < DRAWER_COUNT; i++){
            switch (i){
                case IMPORTANT_TO_ME:
                    items.add(new DrawerItem(mContext.getString(R.string.action_my_progress),
                            R.drawable.ic_clipboard));
                    break;
                case MY_PRIORITIES:
                    items.add(new DrawerItem(mContext.getString(R.string.action_my_priorities),
                            R.drawable.ic_list_bullet));
                    break;
                case MYSELF:
                    items.add(new DrawerItem(mContext.getString(R.string.action_my_information),
                            R.drawable.ic_profile));
                    break;
                case PLACES:
                    items.add(new DrawerItem(mContext.getString(R.string.action_my_places),
                            R.drawable.ic_place));
                    break;
                case MY_PRIVACY:
                    items.add(new DrawerItem(mContext.getString(R.string.action_my_privacy),
                            R.drawable.ic_info));
                    break;
                case SETTINGS:
                    items.add(new DrawerItem(mContext.getString(R.string.action_settings),
                            R.drawable.ic_settings));
                    break;
                case TOUR:
                    items.add(new DrawerItem(mContext.getString(R.string.action_tour),
                            R.drawable.ic_tour));
                    break;
            }
        }

        return items;
    }


    /**
     * Holder for the header item.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class HeaderViewHolder extends RecyclerView.ViewHolder{
        private TextView mName;
        private TextView mAddress;


        /**
         * Constructor.
         *
         * @param itemView the header view.
         */
        public HeaderViewHolder(View itemView){
            super(itemView);

            mName = (TextView)itemView.findViewById(R.id.drawer_header_name);
            mAddress = (TextView)itemView.findViewById(R.id.drawer_header_address);

            mName.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Medium.ttf"));
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
     * Item decoration class that specifies the padding of drawer items.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public static class ItemPadding extends RecyclerView.ItemDecoration{
        private int firstItemPadding;


        /**
         * Constructor.
         *
         * @param context the application context.
         */
        private ItemPadding(Context context){
            firstItemPadding = CompassUtil.getPixels(context, 8);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state){
            if (parent.getChildLayoutPosition(view) == 1){
                outRect.top = firstItemPadding;
            }
            else{
                outRect.top = 0;
            }
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
