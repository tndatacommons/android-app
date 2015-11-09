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
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Reward;
import org.tndata.android.compass.task.GetContentTask;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.Parser;


/**
 * Fragment used to display rewards.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CheckInRewardFragment
        extends Fragment
        implements
                View.OnClickListener,
                GetContentTask.GetContentListener{


    public static final String REWARD_KEY = "org.tndata.compass.Reward.Reward";


    private CheckInRewardListener mListener;
    private Reward mReward;

    private TextView mPreface;
    private TextView mContent;
    private TextView mAuthor;
    private ViewSwitcher mMoreSwitcher;


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

        mPreface = (TextView)rootView.findViewById(R.id.check_in_reward_preface);
        mContent = (TextView)rootView.findViewById(R.id.check_in_reward_content);
        mAuthor = (TextView)rootView.findViewById(R.id.check_in_reward_author);
        mMoreSwitcher = (ViewSwitcher)rootView.findViewById(R.id.check_in_reward_switcher);
        rootView.findViewById(R.id.check_in_reward_more).setOnClickListener(this);
        rootView.findViewById(R.id.check_in_reward_review).setOnClickListener(this);

        populateUI();
    }

    /**
     * Populates the UI with the available reward.
     */
    private void populateUI(){
        mContent.setText(mReward.getMessage());
        if (mReward.isQuote()){
            mAuthor.setVisibility(View.VISIBLE);
            mPreface.setVisibility(View.GONE);
            mContent.setPadding(mContent.getPaddingLeft(), CompassUtil.getPixels(getActivity(), 30),
                    mContent.getPaddingRight(), 0);

            mAuthor.setText(getResources().getString(R.string.check_in_reward_author,
                    mReward.getAuthor()));
        }
        else{
            mAuthor.setVisibility(View.GONE);
            mPreface.setVisibility(View.VISIBLE);
            mContent.setPadding(mContent.getPaddingLeft(), CompassUtil.getPixels(getActivity(), 15),
                    mContent.getPaddingRight(), CompassUtil.getPixels(getActivity(), 15));

            if (mReward.isFortune()){
                mPreface.setText(R.string.check_in_reward_cookie);
            }
            else if (mReward.isFunFact()){
                mPreface.setText(R.string.check_in_reward_fun_fact);
            }
            else if (mReward.isJoke()){
                mPreface.setText(R.string.check_in_reward_joke);
            }
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.check_in_reward_review:
                mListener.onReviewClick();
                break;

            case R.id.check_in_reward_more:
                mMoreSwitcher.showNext();
                fetchReward();
                break;
        }
    }

    /**
     * Fires the request to get a new reward.
     */
    private void fetchReward(){
        String token = ((CompassApplication)getActivity().getApplication()).getToken();
        new GetContentTask(this, 0).execute(Constants.BASE_URL+"rewards/?random=1", token);
    }

    @Override
    public void onContentRetrieved(int requestCode, String content){
        mReward = new Parser().parseRewards(content).get(0);
    }

    @Override
    public void onRequestComplete(int requestCode){
        populateUI();
        mMoreSwitcher.showPrevious();
    }

    @Override
    public void onRequestFailed(int requestCode){
        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
        mMoreSwitcher.showPrevious();
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
