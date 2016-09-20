package org.tndata.android.compass.holder;

import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.tndata.android.compass.databinding.ItemBaseBinding;
import org.tndata.android.compass.util.ImageLoader;


/**
 * Holder for a base item.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class BaseItemHolder extends RecyclerView.ViewHolder{
    private ItemBaseBinding mBinding;


    public BaseItemHolder(ItemBaseBinding binding){
        super(binding.getRoot());
        mBinding = binding;
        mBinding.baseSeparator.separator.setVisibility(View.GONE);
    }

    public void showSeparator(boolean show){
        mBinding.baseSeparator.separator.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @SuppressWarnings("deprecation")
    public void setIconBackgroundColor(int color){
        GradientDrawable gradientDrawable = (GradientDrawable)mBinding.baseIconContainer.getBackground();
        gradientDrawable.setColor(color);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
            mBinding.baseIconContainer.setBackgroundDrawable(gradientDrawable);
        }
        else{
            mBinding.baseIconContainer.setBackground(gradientDrawable);
        }
    }

    public void setIcon(@DrawableRes int resId){
        mBinding.baseIcon.setImageResource(resId);
    }

    public void setIcon(@NonNull String iconUrl){
        ImageLoader.loadBitmap(mBinding.baseIcon, iconUrl);
    }

    public void setTitle(@StringRes int resId){
        mBinding.baseTitle.setText(resId);
    }

    public void setTitle(@NonNull CharSequence title){
        mBinding.baseTitle.setText(title);
    }

    public void setSubtitle(@StringRes int resId){
        mBinding.baseSubtitle.setText(resId);
        mBinding.baseSubtitle.setVisibility(View.VISIBLE);
    }

    public void setSubtitle(@NonNull CharSequence subtitle){
        mBinding.baseSubtitle.setText(subtitle);
        mBinding.baseSubtitle.setVisibility(View.VISIBLE);
    }

    //TODO add an id to the method and set it as itemView's id to be able to identify the event
    public void setOnClickListener(View.OnClickListener listener){
        itemView.setOnClickListener(listener);
    }
}
