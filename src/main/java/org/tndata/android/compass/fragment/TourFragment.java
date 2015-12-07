package org.tndata.android.compass.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.TourPagerAdapter;
import org.tndata.android.compass.ui.CustomViewPager;

import me.relex.circleindicator.CircleIndicator;

public class TourFragment extends Fragment {
    private CustomViewPager defaultViewpager;
    private TextView skipTextView;
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

        defaultViewpager = (CustomViewPager) v.findViewById(R.id.viewpager_default);
        CircleIndicator defaultIndicator = (CircleIndicator) v.findViewById(R.id.indicator_default);
        final TourPagerAdapter defaultPagerAdapter = new TourPagerAdapter(getActivity());
        defaultViewpager.setAdapter(defaultPagerAdapter);
        defaultIndicator.setViewPager(defaultViewpager);
        defaultViewpager.setOnSwipeOutListener(new CustomViewPager.OnSwipeOutListener() {
            @Override
            public void onSwipeOutAtEnd() {
                mCallback.tourFinish();
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
