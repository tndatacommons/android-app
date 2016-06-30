package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Badge;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.ImageLoader;

import java.util.ArrayList;
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
     * @param listener the listener.
     */
    public AwardsAdapter(Context context, BadgeAdapterListener listener){
        mContext = context;
        mListener = listener;
        mBadges = new ArrayList<>();
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
            return new BadgeHolder(inflater.inflate(R.layout.card_badge, parent, false));
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
     * Sets the backing badge list. Does not update the data set.
     *
     * @param badges the lust of badges to be displayed.
     */
    public void setBadges(List<Badge> badges){
        mBadges = badges;
    }

    /**
     * Lets the listener know a badge has been tapped.
     *
     * @param position the position of the badge in the list.
     */
    private void onBadgeTapped(int position){
        mListener.onBadgeSelected(mBadges.get(position));
    }


    /**
     * View holder for a Badge.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    class BadgeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mImage;
        private TextView mName;
        private TextView mDescription;


        /**
         * Constructor.
         *
         * @param rootView the root of the view hierarchy associated with the holder.
         */
        public BadgeHolder(View rootView){
            super(rootView);

            mImage = (ImageView)rootView.findViewById(R.id.award_badge_image);
            mName = (TextView)rootView.findViewById(R.id.award_badge_name);
            mDescription = (TextView)rootView.findViewById(R.id.award_badge_description);

            rootView.setOnClickListener(this);
        }

        /**
         * Binds a badge to the holder.
         *
         * @param badge the badge to be bound.
         */
        public void bind(Badge badge){
            ImageLoader.Options options = new ImageLoader.Options().setUseDefaultPlaceholder(false);
            ImageLoader.loadBitmap(mImage, badge.getImageUrl(), options);

            mName.setText(badge.getName());
            mDescription.setText(badge.getDescription());
        }

        @Override
        public void onClick(View v){
            onBadgeTapped(getAdapterPosition()-1);
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
