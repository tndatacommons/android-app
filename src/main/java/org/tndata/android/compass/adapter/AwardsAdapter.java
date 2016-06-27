package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Badge;
import org.tndata.android.compass.util.ImageLoader;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by isma on 6/27/16.
 */
public class AwardsAdapter extends RecyclerView.Adapter<AwardsAdapter.BadgeHolder>{
    private Context mContext;
    private BadgeAdapterListener mListener;
    private List<Badge> mBadges;


    public AwardsAdapter(Context context, BadgeAdapterListener listener){
        mContext = context;
        mListener = listener;
        mBadges = new ArrayList<>();
    }

    @Override
    public int getItemCount(){
        return mBadges.size();
    }

    @Override
    public BadgeHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new BadgeHolder(inflater.inflate(R.layout.card_badge, parent, false));
    }

    @Override
    public void onBindViewHolder(BadgeHolder holder, int position){
        holder.bind(mBadges.get(position));
    }

    public void setBadges(List<Badge> badges){
        mBadges = badges;
    }

    private void onBadgeTapped(int position){
        mListener.onBadgeSelected(mBadges.get(position));
    }


    class BadgeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mImage;
        private TextView mName;
        private TextView mDescription;


        public BadgeHolder(View rootView){
            super(rootView);

            mImage = (ImageView)rootView.findViewById(R.id.award_badge_image);
            mName = (TextView)rootView.findViewById(R.id.award_badge_name);
            mDescription = (TextView)rootView.findViewById(R.id.award_badge_description);

            rootView.setOnClickListener(this);
        }

        public void bind(Badge badge){
            ImageLoader.Options options = new ImageLoader.Options().setUseDefaultPlaceholder(false);
            ImageLoader.loadBitmap(mImage, badge.getImageUrl(), options);

            mName.setText(badge.getName());
            mDescription.setText(badge.getDescription());
        }

        @Override
        public void onClick(View v){
            onBadgeTapped(getAdapterPosition());
        }
    }


    public interface BadgeAdapterListener{
        void onBadgeSelected(Badge badge);
    }
}
