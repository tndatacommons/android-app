package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Goal;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by isma on 7/16/15.
 */
public class MyPrioritiesGoalAdapter extends BaseAdapter{
    private Context mContext;
    private List<Goal> mGoals;


    public MyPrioritiesGoalAdapter(@NonNull Context context, @NonNull List<Goal> goals){
        mContext = context;
        mGoals = goals;

        ViewHolder.textViewPool = new LinkedList<>();
    }

    @Override
    public int getCount(){
        return mGoals.size();
    }

    @Override
    public Goal getItem(int position){
        return mGoals.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_my_priorities_goal, parent, false);
            attachViewHolder(convertView);
        }

        ViewHolder holder = (ViewHolder)convertView.getTag(R.id.view_holder_tag);

        holder.name.setText(getItem(position).getTitle());

        return convertView;
    }

    /**
     * Creates a new view holder, populates it, and attaches it to the provided view.
     *
     * @param view the view from which extraction the widgets and to which attach the holder.
     */
    private void attachViewHolder(View view){
        ViewHolder holder = new ViewHolder();
        holder.name = (TextView)view.findViewById(R.id.my_priorities_goal_name);
        holder.offspring = (LinearLayout)view.findViewById(R.id.my_priorities_goal_offspring);
        view.setTag(R.id.view_holder_tag, holder);
    }

    /**
     * The item view holder. Also contains a pool of resources.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    static class ViewHolder{
        //This is a pool of TextViews to be reused. Any unused TextViews should be placed
        //  here. Before creating new ones, the list should be checked to see if there are
        //  any of them available.
        private static LinkedList<TextView> textViewPool;

        //Components
        private TextView name;
        private LinearLayout offspring;
    }
}
