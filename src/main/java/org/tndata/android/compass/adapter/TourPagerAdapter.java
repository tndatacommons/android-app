package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;

public class TourPagerAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    int[] images = new int[]{R.drawable.tour1, R.drawable.tour2, R.drawable.tour3, R.drawable.tour4};
    int[] informations = new int[]{R.string.tour_page_1_information, R.string.tour_page_2_information, R.string.tour_page_3_information, R.string.tour_page_4_information};


    public TourPagerAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.tour_image_item, container, false);

        TextView textViewBottom = (TextView) itemView.findViewById(R.id.tour_information_bottom);
        TextView textViewTop = (TextView) itemView.findViewById(R.id.tour_information_top);
        if (position == 2) {
            textViewBottom.setVisibility(View.VISIBLE);
            textViewBottom.setText(mContext.getString(informations[position]));
            textViewTop.setVisibility(View.GONE);
        } else {
            textViewTop.setVisibility(View.VISIBLE);
            textViewTop.setText(mContext.getString(informations[position]));
            textViewBottom.setVisibility(View.GONE);
        }
        ImageView imageView = (ImageView) itemView.findViewById(R.id.tourImageView);
        imageView.setImageResource(images[position]);
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}