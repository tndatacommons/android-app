package org.tndata.android.compass.holder;

import android.graphics.drawable.GradientDrawable;
import android.os.Build;
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

    @SuppressWarnings("deprecation")
    public void setIconBackgroundColor(int color){
        GradientDrawable gradientDrawable = (GradientDrawable)mBinding.baseContent.baseIconContainer.getBackground();
        gradientDrawable.setColor(color);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
            mBinding.baseContent.baseIconContainer.setBackgroundDrawable(gradientDrawable);
        }
        else{
            mBinding.baseContent.baseIconContainer.setBackground(gradientDrawable);
        }
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

    public void hideSubtitle(){
        mBinding.baseContent.baseSubtitle.setVisibility(View.GONE);
    }

    public void setOnClickListener(View.OnClickListener listener, @IdRes int resId){
        itemView.setId(resId);
        itemView.setOnClickListener(listener);
    }
}
