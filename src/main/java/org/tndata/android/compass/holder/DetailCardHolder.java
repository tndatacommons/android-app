package org.tndata.android.compass.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.tndata.android.compass.databinding.CardDetailBinding;


/**
 * Created by isma on 9/12/16.
 */
public class DetailCardHolder extends RecyclerView.ViewHolder{
    private CardDetailBinding mBinding;


    public DetailCardHolder(CardDetailBinding binding){
        super(binding.getRoot());
    }
}
