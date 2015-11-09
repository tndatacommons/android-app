package org.tndata.android.compass.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Reward;
import org.tndata.android.compass.util.CompassUtil;


/**
 * Created by isma on 11/2/15.
 */
public class CheckInRewardFragment extends Fragment implements View.OnClickListener{
    public static final String REWARD_KEY = "org.tndata.compass.Reward.Reward";


    private CheckInRewardListener mListener;
    private Reward mReward;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mReward = (Reward)getArguments().getSerializable(REWARD_KEY);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try{
            mListener = (CheckInRewardListener)activity;
        }
        catch (ClassCastException ccx){
            throw new ClassCastException(activity.toString()
                    + " must implement " + CheckInRewardListener.class);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_check_in_reward, container, false);
    }

    @Override
    public void onViewCreated(View rootView, @Nullable Bundle savedInstanceState){
        TextView header = (TextView)rootView.findViewById(R.id.check_in_reward_header);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)header.getLayoutParams();
        params.height = CompassUtil.getScreenWidth(getActivity())*2/3;
        header.setLayoutParams(params);

        TextView preface = (TextView)rootView.findViewById(R.id.check_in_reward_preface);
        TextView reward = (TextView)rootView.findViewById(R.id.check_in_reward_content);
        TextView author = (TextView)rootView.findViewById(R.id.check_in_reward_author);
        rootView.findViewById(R.id.check_in_reward_review).setOnClickListener(this);

        reward.setText(mReward.getMessage());
        if (mReward.isQuote()){
            preface.setVisibility(View.GONE);
            author.setText(getResources().getString(R.string.check_in_reward_author, mReward.getAuthor()));
            reward.setPadding(reward.getPaddingLeft(), CompassUtil.getPixels(getActivity(), 30),
                    reward.getPaddingRight(), reward.getPaddingBottom());
        }
        else if (mReward.isFortune()){
            preface.setText(R.string.check_in_reward_cookie);
        }
        else if (mReward.isFunFact()){
            preface.setText(R.string.check_in_reward_fun_fact);
        }
        else if (mReward.isJoke()){
            preface.setText(R.string.check_in_reward_joke);
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.check_in_reward_review:
                mListener.onReviewClick();
                break;
        }
    }


    /**
     * Listener interface for the reward fragment.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface CheckInRewardListener{
        /**
         * Called when the review button is clicked.
         */
        void onReviewClick();
    }
}
