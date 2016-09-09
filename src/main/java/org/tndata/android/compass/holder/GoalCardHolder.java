package org.tndata.android.compass.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.tndata.android.compass.databinding.CardGoalBinding;


/**
 * Created by isma on 9/9/16.
 */
public class GoalCardHolder extends RecyclerView.ViewHolder{
    private CardGoalBinding mBinding;


    public GoalCardHolder(CardGoalBinding binding){
        super(binding.getRoot());
        mBinding = binding;
    }
}
