package org.tndata.android.compass.util;

import android.widget.Filter;

import org.tndata.android.compass.adapter.ChooseGoalsAdapter;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.TDCBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Filters goals by title.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class GoalFilter<T extends TDCBase> extends Filter{
    private ChooseGoalsAdapter mAdapter;
    private List<Goal> mList;

    /**
     * Constructor.
     *
     * @param adapter the adapter
     * @param list
     */
    public GoalFilter(ChooseGoalsAdapter adapter, List<Goal> list){
        mAdapter = adapter;
        mList = list;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint){
        FilterResults results = new FilterResults();

        List<Goal> output = new ArrayList<>();
        for (Goal goal:mList){
            if (goal.getTitle().contains(constraint)){
                output.add(goal);
            }
        }

        results.count = output.size();
        results.values = output;

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results){
        if (results.values instanceof List){
            mAdapter.addGoals((List<Goal>)results.values);
        }
    }
}
