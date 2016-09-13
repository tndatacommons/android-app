package org.tndata.android.compass.holder;

import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
public class GoalCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private CardGoalBinding mBinding;
    private Listener mListener;


    /**
     * Constructor.
     *
     * @param binding the binding object
     */
    public GoalCardHolder(@NonNull CardGoalBinding binding, @Nullable Listener listener){
        super(binding.getRoot());
        mBinding = binding;
        mListener = listener;
        itemView.setOnClickListener(this);
    }

    /**
     * Sets the color of the goal's icon container.
     *
     * @param color the color of the icon container.
     */
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

    /**
     * Loads the icon in the provided URL into the goal icon view.
     *
     * @param url the URL to fetch the icon from.
     */
    public void setIcon(@NonNull String url){
        ImageLoader.loadBitmap(mBinding.goalIcon, url);
    }

    /**
     * Sets the title of the goal.
     *
     * @param title the title of the goal.
     */
    public void setTitle(@NonNull String title){
        mBinding.goalTitle.setText(title);
    }

    @Override
    public void onClick(View v){
        mListener.onGoalCardClick();
    }


    /**
     * Listener interface for GoalCardHolder.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface Listener{
        /**
         * Called when the card is tapped.
         */
        void onGoalCardClick();
    }
}
