package org.tndata.android.compass.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.tndata.android.compass.fragment.CheckInFeedbackFragment;
import org.tndata.android.compass.fragment.CheckInRewardFragment;
import org.tndata.android.compass.model.GoalContent;
import org.tndata.android.compass.model.Reward;
import org.tndata.android.compass.model.UserAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Adapter used for CheckInActivity's ViewPager.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CheckInPagerAdapter extends FragmentPagerAdapter{
    private List<GoalContent> mGoals;

    private CheckInRewardFragment mRewardFragment;
    private List<Fragment> mFragments;


    /**
     * Constructor.
     *
     * @param fm the fragment manager.
     * @param dataSet the data to be displayed by the adapter.
     */
    public CheckInPagerAdapter(FragmentManager fm, Map<GoalContent, List<UserAction>> dataSet,
                               Reward reward){
        super(fm);

        //Populate the lists with the data in the set
        mGoals = new ArrayList<>();
        for (Map.Entry<GoalContent, List<UserAction>> entry:dataSet.entrySet()){
            mGoals.add(entry.getKey());
        }

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
