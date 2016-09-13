package org.tndata.android.compass.holder;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import org.tndata.android.compass.R;
import org.tndata.android.compass.databinding.CardContentBinding;


/**
 * Holder to display a card with the card_content.xml layout.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ContentCardHolder extends RecyclerView.ViewHolder{
    private CardContentBinding mBinding;


    /**
     * Constructor.
     *
     * @param binding the binding object associated with the card's layout.
     */
    public ContentCardHolder(@NonNull CardContentBinding binding){
        super(binding.getRoot());
        mBinding = binding;
    }

    public void setColor(int color){
        mBinding.contentTitle.setBackgroundColor(color);
    }

    /**
     * Binds a title to the holder.
     *
     * @param title the title to be displayed in the card.
     */
    public void setTitle(CharSequence title){
        mBinding.contentTitle.setText(title);
    }

    /**
     * Binds a title to the holder.
     *
     * @param titleId the resource id of the title to be displayed in the card.
     */
    public void setTitle(@StringRes int titleId){
        mBinding.contentTitle.setText(titleId);
    }

    /**
     * Sets the font of the title as Roboto-Medium.
     */
    public void setTitleWeightMedium(){
        AssetManager assets = itemView.getContext().getAssets();
        mBinding.contentTitle.setTypeface(
                Typeface.createFromAsset(assets, "fonts/Roboto-Medium.ttf")
        );
    }

    /**
     * Binds a content to the holder.
     *
     * @param content the description to be displayed in the card.
     */
    public void setContent(CharSequence content){
        mBinding.contentContent.setText(content);
    }

    /**
     * Adds a button to the card.
     *
     * @param id the id of the new button.
     * @param caption the resource of the caption of the new button.
     *
     * @return the newly created button.
     */
    public Button addButton(@IdRes int id, @StringRes int caption){
        LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
        AssetManager assets = itemView.getContext().getAssets();
        View view = inflater.inflate(R.layout.button_flat, mBinding.contentButtonContainer);
        Button button = (Button)view.findViewById(R.id.button_flat);
        button.setId(id);
        button.setText(caption);
        button.setTypeface(Typeface.createFromAsset(assets, "fonts/Roboto-Medium.ttf"));
        mBinding.contentButtonContainer.setVisibility(View.VISIBLE);

        return button;
    }
}
