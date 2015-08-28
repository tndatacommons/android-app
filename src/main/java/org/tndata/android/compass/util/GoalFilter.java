package org.tndata.android.compass.util;

import android.widget.Filter;

import org.tndata.android.compass.adapter.ChooseGoalsAdapter;
import org.tndata.android.compass.model.Goal;

import java.util.ArrayList;
import java.util.List;


/**
 * Filters goals by title.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class GoalFilter extends Filter{
    private ChooseGoalsAdapter mAdapter;
    private List<Goal> mList;


    /**
     * Constructor.
     *
     * @param adapter the adapter to be filtered.
     * @param list the original list of the adapter.
     */
    public GoalFilter(ChooseGoalsAdapter adapter, List<Goal> list){
        mAdapter = adapter;
        mList = list;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint){
        //The constraint is split into words
        String constraintArray[] = constraint.toString().toLowerCase().split(" ");
        List<Goal> output = new ArrayList<>();
        int matchCount;
        //For each goal
        for (Goal goal:mList){
            //The match count is reset
            matchCount = 0;
            //For each item in the constraint array
            for (String constraintItem:constraintArray){
                //If either title or description match, increase the match count
                if (goal.getTitle().toLowerCase().contains(constraintItem) ||
                        goal.getDescription().toLowerCase().contains(constraintItem)){
                    matchCount++;
                }
            }
            //If all the words were present, add the goal to the output
            if (matchCount == constraintArray.length){
                output.add(goal);
            }
        }

        return new GoalFilterResults(output);
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results){
        if (results instanceof GoalFilterResults){
            mAdapter.addGoals(((GoalFilterResults)results).mOutput);
        }
    }


    /**
     * An extension of the FilterResults class that contains a list of goals.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class GoalFilterResults extends FilterResults{
        private final List<Goal> mOutput;

        /**
         * Constructor.
         *
         * @param output the result of the filtering.
         */
        GoalFilterResults(List<Goal> output){
            mOutput = output;
        }
    }
}
