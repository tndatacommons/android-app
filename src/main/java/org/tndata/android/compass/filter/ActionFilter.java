package org.tndata.android.compass.filter;

import android.widget.Filter;

import org.tndata.android.compass.adapter.ChooseActionsAdapter;
import org.tndata.android.compass.model.Action;

import java.util.ArrayList;
import java.util.List;


/**
 * Filters behaviors by title and description.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ActionFilter extends Filter{
    private ChooseActionsAdapter mAdapter;
    private List<Action> mList;


    /**
     * Constructor.
     *
     * @param adapter the adapter to be filtered.
     * @param list the original list of the adapter.
     */
    public ActionFilter(ChooseActionsAdapter adapter, List<Action> list){
        mAdapter = adapter;
        mList = list;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint){
        //The constraint is split into words
        String constraintArray[] = constraint.toString().toLowerCase().split(" ");
        List<Action> output = new ArrayList<>();
        int matchCount;
        //For each goal
        for (Action action:mList){
            //The match count is reset
            matchCount = 0;
            //For each item in the constraint array
            for (String constraintItem:constraintArray){
                //If either title or description match, increase the match count
                if (action.getTitle().toLowerCase().contains(constraintItem) ||
                        action.getDescription().toLowerCase().contains(constraintItem)){
                    matchCount++;
                }
            }
            //If all the words were present, add the goal to the output
            if (matchCount == constraintArray.length){
                output.add(action);
            }
        }

        return new BehaviorFilterResults(output);
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results){
        if (results instanceof BehaviorFilterResults){
            mAdapter.setActions(((BehaviorFilterResults)results).mOutput);
        }
    }


    /**
     * An extension of the FilterResults class that contains a list of goals.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class BehaviorFilterResults extends FilterResults{
        private final List<Action> mOutput;

        /**
         * Constructor.
         *
         * @param output the result of the filtering.
         */
        BehaviorFilterResults(List<Action> output){
            mOutput = output;
        }
    }
}