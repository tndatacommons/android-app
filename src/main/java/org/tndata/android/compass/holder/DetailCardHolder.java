package org.tndata.android.compass.holder;

import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.PopupMenu;

import org.tndata.android.compass.databinding.CardDetailBinding;


/**
 * Detail card view holder.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class DetailCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private CardDetailBinding mBinding;
    private PopupMenu mPopupMenu;


    /**
     * Constructor.
     *
     * @param binding the binding object.
     */
    public DetailCardHolder(CardDetailBinding binding){
        super(binding.getRoot());
        mBinding = binding;
    }

    /**
     * Sets the title of the card.
     *
     * @param title the title to be set.
     */
    public void setTitle(@NonNull String title){
        mBinding.detailTitle.setText(title);
    }

    /**
     * Sets the content of the card.
     *
     * @param content the content to be set.
     */
    public void setContent(@NonNull String content){
        mBinding.detailContent.setText(content);
    }

    /**
     * Initializes the overflow menu and makes it visible.
     *
     * @param menu the resource containing the menu.
     * @param listener the menu's listener.
     */
    public void setOverflowMenu(@MenuRes int menu, PopupMenu.OnMenuItemClickListener listener){
        mPopupMenu = new PopupMenu(itemView.getContext(), mBinding.detailOverflow);
        mPopupMenu.getMenuInflater().inflate(menu, mPopupMenu.getMenu());
        mPopupMenu.setOnMenuItemClickListener(listener);
        mBinding.detailOverflow.setOnClickListener(this);
        mBinding.detailOverflow.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v){
        mPopupMenu.show();
    }
}
