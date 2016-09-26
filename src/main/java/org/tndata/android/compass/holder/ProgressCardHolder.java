package org.tndata.android.compass.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.databinding.CardProgressBinding;


/**
 * Holder for the progress widget card.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ProgressCardHolder extends RecyclerView.ViewHolder{
    private CardProgressBinding mBinding;


    /**
     * Constructor.
     *
     * @param binding the binding object associated with the card's layout.
     */
    public ProgressCardHolder(CardProgressBinding binding){
        super(binding.getRoot());
        mBinding = binding;
    }

    /**
     * Sets the progress displayed by this card.
     *
     * @param progress the progress to be displayed.
     */
    public void setProgress(int progress){
        Context context = itemView.getContext();
        String tips = context.getString(R.string.card_progress_caption_tips, progress);
        mBinding.progressCaptionTips.setText(tips);
        mBinding.progressMeter.setProgressValue(progress);
    }
}
