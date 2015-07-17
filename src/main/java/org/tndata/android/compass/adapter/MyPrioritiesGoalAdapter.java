package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Goal;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by isma on 7/16/15.
 */
public class MyPrioritiesGoalAdapter extends RecyclerView.Adapter{

    private Context mContext;
    private List<Goal> mGoals;

    private int mExpandedViewPosition;


    /**
     * Constructor.
     *
     * @param context the application context.
     * @param goals the list of goals in a given category.
     */
    public MyPrioritiesGoalAdapter(@NonNull Context context, @NonNull List<Goal> goals){
        mContext = context;
        mGoals = goals;

        ViewHolder.textViewPool = new LinkedList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_my_priorities_goal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        Log.d("Position", position+"");
        ((ViewHolder)holder).name.setText(mGoals.get(position).getTitle());
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int getItemCount(){
        return mGoals.size();
    }

    private void expand(View view, int position){

    }

    private void collapse(View view, int position){

    }

    /**
     * The item view holder. Also contains a pool of resources.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    static class ViewHolder extends RecyclerView.ViewHolder{
        //This is a pool of TextViews to be reused. Any unused TextViews should be placed
        //  here. Before creating new ones, the list should be checked to see if there are
        //  any of them available.
        private static LinkedList<TextView> textViewPool;

        //Components
        private TextView name;
        private LinearLayout offspring;

        public ViewHolder(View itemView){
            super(itemView);

            name = (TextView)itemView.findViewById(R.id.my_priorities_goal_name);
            offspring = (LinearLayout)itemView.findViewById(R.id.my_priorities_goal_offspring);
        }
    }
}
