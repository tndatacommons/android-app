package org.tndata.android.compass.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.TourPagerAdapter;

import me.relex.circleindicator.CircleIndicator;

public class TourFragment extends Fragment {
    private ViewPager defaultViewpager;
    private TextView skipTextView;
    private TextView finishTextView;
    private TourFragmentListener mCallback;

    public interface TourFragmentListener {
        public void tourFinish();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_tour, container, false);

        skipTextView = (TextView) v.findViewById(R.id.skipTextView);
        skipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.tourFinish();
            }
        });

        finishTextView = (TextView) v.findViewById(R.id.finishTextView);
        finishTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.tourFinish();
            }
        });

        defaultViewpager = (ViewPager) v.findViewById(R.id.viewpager_default);
        CircleIndicator defaultIndicator = (CircleIndicator) v.findViewById(R.id.indicator_default);
        final TourPagerAdapter defaultPagerAdapter = new TourPagerAdapter(getActivity());
        defaultViewpager.setAdapter(defaultPagerAdapter);
        defaultIndicator.setViewPager(defaultViewpager);
        defaultViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(position == (defaultPagerAdapter.getCount()-1)){
                    skipTextView.setVisibility(View.GONE);
                    finishTextView.setVisibility(View.VISIBLE);
                }else{
                    finishTextView.setVisibility(View.GONE);
                    skipTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity); // This makes sure that the container activity
        // has implemented the callback interface. If not, it throws an
        // exception
        try {
            mCallback = (TourFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TourFragmentListener");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        defaultViewpager.setCurrentItem(0);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

}
