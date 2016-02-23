package org.tndata.android.compass.filter;

import android.widget.Filter;

import org.tndata.android.compass.adapter.ChooseGoalsAdapter;
import org.tndata.android.compass.model.GoalContent;

import java.util.ArrayList;
import java.util.List;


/**
 * Filters goals by title and description.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class GoalFilter extends Filter{
    private ChooseGoalsAdapter mAdapter;
    private List<GoalContent> mGoalList;


    /**
     * Constructor.
     *
     * @param adapter the adapter to be filtered.
     */
    public GoalFilter(ChooseGoalsAdapter adapter){
        mAdapter = adapter;
        mGoalList = new ArrayList<>();
    }

    public void setGoalList(List<GoalContent> goalList){
        mGoalList = goalList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint){
        //The constraint is split into words
        String constraintArray[] = constraint.toString().toLowerCase().split(" ");
        List<GoalContent> output = new ArrayList<>();
        int matchCount;
        //For each goal
        for (GoalContent goal: mGoalList){
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
            //mAdapter.addGoals(((GoalFilterResults)results).mOutput);
        }
    }


    /**
     * An extension of the FilterResults class that contains a list of goals.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class GoalFilterResults extends FilterResults{
        private final List<GoalContent> mOutput;

        /**
         * Constructor.
         *
         * @param output the result of the filtering.
         */
        GoalFilterResults(List<GoalContent> output){
            mOutput = output;
        }
    }
}
