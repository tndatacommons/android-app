package org.tndata.android.compass.holder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.tndata.android.compass.databinding.ItemProgressFooterBinding;


/**
 * Holder for a progress footer item. It is able to display a progress widget and a
 * tappable message.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ProgressFooterHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private ItemProgressFooterBinding mBinding;
    private Listener mListener;


    /**
     * Constructor. Doesn't make the message tappable.
     *
     * @param binding the binding object associated with this holder's layout.
     */
    public ProgressFooterHolder(@NonNull ItemProgressFooterBinding binding){
        this(binding, null);
    }

    /**
     * Constructor. Makes the message tappable.
     *
     * @param binding the binding object associated with this holder's layout.
     * @param listener listener class for the tappable message.
     */
    public ProgressFooterHolder(@NonNull ItemProgressFooterBinding binding,
                                @Nullable Listener listener){

        super(binding.getRoot());

        mBinding = binding;
        mListener = listener;
        if (mListener != null){
            mBinding.footerMessage.setOnClickListener(this);
        }
    }

    /**
     * Displays a message.
     *
     * @param message the message to display.
     */
    public void displayMessage(String message){
        mBinding.footerMessage.setText(message);
        mBinding.footerMessage.setVisibility(View.VISIBLE);
        mBinding.footerProgress.setVisibility(View.GONE);
    }

    /**
     * Displays a message.
     *
     * @param messageId the id of the resource of the message to be displayed.
     */
    public void displayMessage(@StringRes int messageId){
        mBinding.footerMessage.setText(messageId);
        mBinding.footerMessage.setVisibility(View.VISIBLE);
        mBinding.footerProgress.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view){
        mListener.onMessageClick();
    }


    /**
     * Listener interface.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface Listener{
        /**
         * Called when the message is tapped, if it is tappable.
         */
        void onMessageClick();
    }
}
