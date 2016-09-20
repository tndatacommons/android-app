package org.tndata.android.compass.holder;

import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.tndata.android.compass.databinding.CardBaseItemBinding;


/**
 * Generic holder for a base item card.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class BaseItemCardHolder extends RecyclerView.ViewHolder{
    private CardBaseItemBinding mBinding;


    public BaseItemCardHolder(CardBaseItemBinding binding){
        super(binding.getRoot());
        mBinding = binding;
        mBinding.baseContent.baseSeparator.separator.setVisibility(View.GONE);
    }

    public void showSeparator(boolean show){
        mBinding.baseContent.baseSeparator.separator.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setIcon(@DrawableRes int resId){
        mBinding.baseContent.baseIcon.setImageResource(resId);
    }

    public void setTitle(@StringRes int resId){
        mBinding.baseContent.baseTitle.setText(resId);
    }

    public void setTitle(@NonNull CharSequence title){
        mBinding.baseContent.baseTitle.setText(title);
    }

    public void setSubtitle(@StringRes int resId){
        mBinding.baseContent.baseSubtitle.setText(resId);
        mBinding.baseContent.baseSubtitle.setVisibility(View.VISIBLE);
    }

    public void setSubtitle(@NonNull CharSequence subtitle){
        mBinding.baseContent.baseSubtitle.setText(subtitle);
        mBinding.baseContent.baseSubtitle.setVisibility(View.VISIBLE);
    }

    public void setOnClickListener(View.OnClickListener listener, @IdRes int resId){
        itemView.setId(resId);
        itemView.setOnClickListener(listener);
    }
}
