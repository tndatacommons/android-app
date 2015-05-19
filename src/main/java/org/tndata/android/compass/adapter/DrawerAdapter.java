package org.tndata.android.compass.adapter;

import java.util.ArrayList;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.DrawerItem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DrawerAdapter extends ArrayAdapter<DrawerItem> {
    private ArrayList<DrawerItem> mItems;
    private Context mContext = null;

    public DrawerAdapter(Context context, int textViewResourceId,
            ArrayList<DrawerItem> items) {
        super(context, textViewResourceId, items);
        this.mItems = items;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    public void updateEntries(ArrayList<DrawerItem> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item_nav_drawer, null);
        }
        String text = mItems.get(position).text;
        Drawable drawable = mItems.get(position).drawable;
        TextView description = (TextView) v.findViewById(R.id.drawer_list_text);
        if (!text.isEmpty()) {
            description.setText(text);
        }
        if (drawable != null) {
            description.setCompoundDrawablesWithIntrinsicBounds(drawable, null,
                    null, null);
        }

        return v;

    }
}
