package org.tndata.android.compass.adapter;

import java.util.List;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.DrawerItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


/**
 * The adapter for the list of options in the navigation drawer.
 *
 * @author Edited by Ismael Alonso
 * @version 2.0.0
 */
public class DrawerAdapter extends BaseAdapter{
    private Context mContext;
    private List<DrawerItem> mItems;


    /**
     * Constructor.
     *
     * @param context the application context.
     * @param items the list of items to be displayed in the drawer list.
     */
    public DrawerAdapter(Context context, List<DrawerItem> items){
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public int getCount(){
        return mItems.size();
    }

    @Override
    public DrawerItem getItem(int position){
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    //TODO this method doesn't really serve any purpose, remove?
    public void updateEntries(List<DrawerItem> items){
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //If there is not a view available for recycling, create a new one
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.list_item_nav_drawer, parent, false);
        }

        DrawerItem item = getItem(position);

        //Populate the item with the item at the given position
        TextView description = (TextView) convertView.findViewById(R.id.drawer_list_text);
        description.setText(item.text);
        if (item.drawable != null) {
            description.setCompoundDrawablesWithIntrinsicBounds(item.drawable, null, null, null);
        }

        return convertView;
    }
}
