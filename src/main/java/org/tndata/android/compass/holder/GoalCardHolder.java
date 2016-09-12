package org.tndata.android.compass.holder;

import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.tndata.android.compass.databinding.CardGoalBinding;
import org.tndata.android.compass.util.ImageLoader;


/**
 * Single goa view holder.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class GoalCardHolder extends RecyclerView.ViewHolder{
    private CardGoalBinding mBinding;


    /**
     * Constructor.
     *
     * @param binding the binding object
     */
    public GoalCardHolder(CardGoalBinding binding){
        super(binding.getRoot());
        mBinding = binding;
    }

    @SuppressWarnings("deprecation")
    public void setColor(int color){
        GradientDrawable gradientDrawable = (GradientDrawable)mBinding.goalIconContainer.getBackground();
        gradientDrawable.setColor(color);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
            mBinding.goalIconContainer.setBackgroundDrawable(gradientDrawable);
        }
        else{
            mBinding.goalIconContainer.setBackground(gradientDrawable);
        }
    }

    public void setIcon(@NonNull String url){
        ImageLoader.loadBitmap(mBinding.goalIcon, url);
    }

    public void setTitle(@NonNull String title){
        mBinding.goalTitle.setText(title);
    }

    public void setOnClickListener(View.OnClickListener listener){
        itemView.setOnClickListener(listener);
    }
}
