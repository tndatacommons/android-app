package org.tndata.android.compass.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import org.tndata.android.compass.databinding.CardDetailBinding;


/**
 * Created by isma on 9/12/16.
 */
public class DetailCardHolder extends RecyclerView.ViewHolder{
    private CardDetailBinding mBinding;


    public DetailCardHolder(CardDetailBinding binding){
        super(binding.getRoot());
        mBinding = binding;
    }

    public void setTitle(@NonNull String title){
        mBinding.detailTitle.setText(title);
    }

    public void setContent(@NonNull String content){
        mBinding.detailContent.setText(content);
    }
}
