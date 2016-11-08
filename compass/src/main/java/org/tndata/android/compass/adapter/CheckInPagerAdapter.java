package org.tndata.android.compass.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.tndata.android.compass.fragment.CheckInFeedbackFragment;
import org.tndata.android.compass.fragment.CheckInRewardFragment;
import org.tndata.compass.model.Reward;
import org.tndata.compass.model.UserGoal;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter used for CheckInActivity's ViewPager.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CheckInPagerAdapter extends FragmentPagerAdapter{
    private List<UserGoal> mGoals;

    private CheckInRewardFragment mRewardFragment;
    private List<Fragment> mFragments;


    /**
     * Constructor.
     *
     * @param fm the fragment manager.
     * @param goals the data to be displayed by the adapter.
     * @param reward the reward to be displayed in its fragment.
     */
    public CheckInPagerAdapter(FragmentManager fm, List<UserGoal> goals, Reward reward){
        super(fm);

        //Populate the lists with the data in the set
        mGoals = goals;

        mRewardFragment = CheckInRewardFragment.newInstance(reward);
        mFragments = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position){
        //If the fragment has not been created yet, create it
        if (position == mFragments.size()){
            if (position == getCount()-1){
                mFragments.add(mRewardFragment);
            }
            else{
                mFragments.add(CheckInFeedbackFragment.newInstance(position, mGoals.get(position)));
            }
        }
        return mFragments.get(position);
    }

    @Override
    public int getCount(){
        //Account for the reward
        return mGoals.size()+1;
    }

    /**
     * Updates the reward fragment header string.
     *
     * @param better true if the user is doing better than last week, false otherwise.
     */
    public void updateRewardFragment(boolean better){
        mRewardFragment.update(better);
    }
}
