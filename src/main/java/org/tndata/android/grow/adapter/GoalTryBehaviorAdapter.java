package org.tndata.android.grow.adapter;

import java.util.List;

import org.tndata.android.grow.R;
import org.tndata.android.grow.model.Behavior;
import org.tndata.android.grow.util.ImageCache;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GoalTryBehaviorAdapter extends ArrayAdapter<Behavior> {

    private Context mContext;
    private List<Behavior> mItems;

    static class ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        ImageView iconImageView;
    }

    public GoalTryBehaviorAdapter(Context context, int resource,
            List<Behavior> objects) {
        super(context, resource, objects);
        this.mItems = objects;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    public void updateEntries(List<Behavior> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_item_behavior, null);
            viewHolder = new ViewHolder();
            viewHolder.iconImageView = (ImageView) convertView
                    .findViewById(R.id.list_item_behavior_imageview);
            viewHolder.titleTextView = (TextView) convertView
                    .findViewById(R.id.list_item_behavior_title_textview);
            viewHolder.descriptionTextView = (TextView) convertView
                    .findViewById(R.id.list_item_behavior_description_textview);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();

        }
        final Behavior behavior = mItems.get(position);
        if (behavior.getIconUrl() != null && !behavior.getIconUrl().isEmpty()) {
            ImageCache.instance(mContext).loadBitmap(viewHolder.iconImageView,
                    behavior.getIconUrl(), false);
        } else {
            viewHolder.iconImageView.setImageResource(R.drawable.default_image);
        }
        viewHolder.titleTextView.setText(behavior.getTitle());
        viewHolder.descriptionTextView.setText(behavior.getNarrativeBlock());

        return convertView;
    }
}
