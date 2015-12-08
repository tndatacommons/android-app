package org.tndata.android.compass.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.TourPagerAdapter;
import org.tndata.android.compass.ui.CustomViewPager;

import me.relex.circleindicator.CircleIndicator;


/**
 * Fragment that displays the tour.
 *
 * @author Edited and documented by Ismael Alonso.
 * @version 1.0.0
 */
public class TourFragment
        extends Fragment
        implements
                View.OnClickListener,
                CustomViewPager.OnSwipeOutListener{

    //Callback interface
    private TourFragmentCallback mCallback;

    //UI components
    private CustomViewPager mPager;


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        //This makes sure that the container activity has implemented the callback interface,
        //  if not, it throws an exception.
        try{
            mCallback = (TourFragmentCallback)context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement TourFragmentListener");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mCallback = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //TODO the layout file needs some serious fixin'
        View rootView = inflater.inflate(R.layout.fragment_tour, container, false);

        //Fetch UI components
        mPager = (CustomViewPager)rootView.findViewById(R.id.tour_pager);
        CircleIndicator indicator = (CircleIndicator)rootView.findViewById(R.id.tour_indicator);

        //Listeners and adapters
        rootView.findViewById(R.id.tour_skip).setOnClickListener(this);

        mPager.setAdapter(new TourPagerAdapter(getActivity()));
        mPager.setOnSwipeOutListener(this);
        indicator.setViewPager(mPager);

        return rootView;
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tour_skip:
                mCallback.onTourComplete();
                break;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        mPager.setCurrentItem(0);
    }

    @Override
    public void onSwipeOutAtEnd(){
        mCallback.onTourComplete();
    }


    /**
     * Callback interface for the tour fragment.
     *
     * @author Edited and documented by Ismael Alonso
     * @version 1.0.0
     */
    public interface TourFragmentCallback{
        /**
         * Called when the user either completes or skips the tour.
         */
        void onTourComplete();
    }
}
