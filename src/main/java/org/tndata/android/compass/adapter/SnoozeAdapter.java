package org.tndata.android.compass.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.tndata.android.compass.R;


/**
 * A basic adapter for the snooze menu. A base adapter is used because this is a
 * fixed element list, there are no performance concerns.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class SnoozeAdapter extends BaseAdapter{
    private static final int OPTION_NUMBER = 3;

    public Context mContext;

    /**
     * Constructor.
     *
     * @param context the context.
     */
    public SnoozeAdapter(Context context){
        mContext = context;
    }

    @Override
    public int getCount(){
        return OPTION_NUMBER;
    }

    @Override
    public Object getItem(int position){
        return null;
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_snooze, parent, false);
        }

        TextView title = (TextView)convertView;
        if (position == 0){
            title.setText(R.string.later_in_an_hour);
        }
        else if (position == 1){
            title.setText(R.string.later_tomorrow);
        }
        else if (position == 2){
            title.setText(R.string.later_pick);
        }

        return convertView;
    }
}
