package org.tndata.android.compass.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Reward;
import org.tndata.android.compass.util.CompassUtil;


/**
 * Created by isma on 11/2/15.
 */
public class CheckInRewardFragment extends Fragment{
    public static final String REWARD_KEY = "org.tndata.compass.Reward.Reward";


    private Reward mReward;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mReward = (Reward)getArguments().getSerializable(REWARD_KEY);
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

        TextView reward = (TextView)rootView.findViewById(R.id.check_in_reward_content);
        reward.setText(mReward.getMessage());
    }
}
