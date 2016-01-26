package org.tndata.android.compass.filter;

import android.widget.Filter;

import org.tndata.android.compass.adapter.ChooseBehaviorsAdapter;
import org.tndata.android.compass.model.BehaviorContent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Filters behaviors by title and description.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class BehaviorFilter extends Filter{
    private ChooseBehaviorsAdapter mAdapter;
    private Collection<BehaviorContent> mList;


    /**
     * Constructor.
     *
     * @param adapter the adapter to be filtered.
     * @param list the original list of the adapter.
     */
    public BehaviorFilter(ChooseBehaviorsAdapter adapter, Collection<BehaviorContent> list){
        mAdapter = adapter;
        mList = list;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint){
        //The constraint is split into words
        String constraintArray[] = constraint.toString().toLowerCase().split(" ");
        List<BehaviorContent> output = new ArrayList<>();
        int matchCount;
        //For each goal
        for (BehaviorContent behavior:mList){
            //The match count is reset
            matchCount = 0;
            //For each item in the constraint array
            for (String constraintItem:constraintArray){
                //If either title or description match, increase the match count
                if (behavior.getTitle().toLowerCase().contains(constraintItem) ||
                        behavior.getDescription().toLowerCase().contains(constraintItem)){
                    matchCount++;
                }
            }
            //If all the words were present, add the goal to the output
            if (matchCount == constraintArray.length){
                output.add(behavior);
            }
        }

        return new BehaviorFilterResults(output);
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results){
        if (results instanceof BehaviorFilterResults){
            mAdapter.setBehaviors(((BehaviorFilterResults)results).mOutput);
        }
    }


    /**
     * An extension of the FilterResults class that contains a list of goals.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class BehaviorFilterResults extends FilterResults{
        private final List<BehaviorContent> mOutput;

        /**
         * Constructor.
         *
         * @param output the result of the filtering.
         */
        BehaviorFilterResults(List<BehaviorContent> output){
            mOutput = output;
        }
    }
}