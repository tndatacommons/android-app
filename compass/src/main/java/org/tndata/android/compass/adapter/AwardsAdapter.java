package org.tndata.android.compass.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.tndata.android.compass.R;
import org.tndata.android.compass.databinding.CardBadgeBinding;
import org.tndata.compass.model.Badge;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.ImageLoader;

import java.util.List;


/**
 * Adapter for displaying a list of Awards in the form of Badges.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class AwardsAdapter extends RecyclerView.Adapter{
    private static final int TYPE_BLANK = 1;
    private static final int TYPE_AWARD = 2;


    private Context mContext;
    private BadgeAdapterListener mListener;
    private List<Badge> mBadges;


    /**
     * Constructor.
     *
     * @param context a reference to the context.
     * @param badges the list of badges earned by the user.
     * @param listener the listener.
     */
    public AwardsAdapter(Context context, List<Badge> badges, BadgeAdapterListener listener){
        mContext = context;
        mListener = listener;
        mBadges = badges;
    }

    @Override
    public int getItemViewType(int position){
        if (position == 0){
            return TYPE_BLANK;
        }
        return TYPE_AWARD;
    }

    @Override
    public int getItemCount(){
        return mBadges.size()+1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (viewType == TYPE_BLANK){
            return new RecyclerView.ViewHolder(new CardView(mContext)){};
        }
        else{ //if (viewType == TYPE_AWARD){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            return new BadgeHolder(
                    DataBindingUtil.<CardBadgeBinding>inflate(
                            inflater, R.layout.card_badge, parent, false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
        if (position == 0){
            int width = CompassUtil.getScreenWidth(mContext);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (int)((width*2/3)*0.85)
            );
            rawHolder.itemView.setLayoutParams(params);
            rawHolder.itemView.setVisibility(View.INVISIBLE);
        }
        else{
            ((BadgeHolder)rawHolder).bind(mBadges.get(position-1));
        }
    }


    /**
     * View holder for a Badge.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    class BadgeHolder extends RecyclerView.ViewHolder{
        private CardBadgeBinding mBinding;


        /**
         * Constructor.
         *
         * @param binding the binding object
         */
        public BadgeHolder(CardBadgeBinding binding){
            super(binding.getRoot());

            mBinding = binding;
            mBinding.setListener(mListener);
        }

        /**
         * Binds a badge to the holder.
         *
         * @param badge the badge to be bound.
         */
        public void bind(Badge badge){
            ImageLoader.Options options = new ImageLoader.Options().setUseDefaultPlaceholder(false);
            ImageLoader.loadBitmap(mBinding.awardBadgeImage, badge.getImageUrl(), options);
            mBinding.setBadge(badge);
        }
    }


    /**
     * Listener interface for the adapter.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface BadgeAdapterListener{
        /**
         * Called when a badge is tapped.
         *
         * @param badge the badge that was tapped.
         */
        void onBadgeSelected(Badge badge);
    }
}
