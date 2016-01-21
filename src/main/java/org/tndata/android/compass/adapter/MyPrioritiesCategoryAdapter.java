package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.UserCategory;

import java.util.List;


/**
 * Adapter for the category list at MyPrioritiesActivity.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class MyPrioritiesCategoryAdapter extends BaseAdapter{
    private Context mContext;
    private List<UserCategory> mUserCategories;


    /**
     * Constructor.
     *
     * @param context the application context.
     * @param userCategories the list of  categories selected by the user.
     */
    public MyPrioritiesCategoryAdapter(@NonNull Context context, @NonNull List<UserCategory> userCategories){
        mContext = context;
        mUserCategories = userCategories;
    }

    @Override
    public int getCount(){
        return mUserCategories.size();
    }

    @Override
    public UserCategory getItem(int position){
        return mUserCategories.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    //@Override
    public View getView(int position, View convertView, ViewGroup parent){
        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_my_priorities_category, parent, false);
            attachViewHolder(convertView);
        }
        convertView.setTag(R.id.view_item_position_tag);

        ViewHolder holder = (ViewHolder)convertView.getTag(R.id.view_holder_tag);
        holder.name.setText(getItem(position).getTitle());

        return convertView;
    }

    /**
     * Creates a new view holder, populates it, and attaches it to the provided view.
     *
     * @param view the view from which extraction the widgets and to which attach the holder.
     */
    private void attachViewHolder(@NonNull View view){
        ViewHolder holder = new ViewHolder();
        holder.name = (TextView)view.findViewById(R.id.my_priorities_category_name);
        view.setTag(R.id.view_holder_tag, holder);
    }


    /**
     * The item view holder. Also contains a pool of resources.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public static class ViewHolder{
        //Components
        private TextView name;
    }
}
