package org.tndata.android.compass.fragment;

import android.content.Context;
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

import org.tndata.android.compass.R;
import org.tndata.compass.model.ResultSet;
import org.tndata.compass.model.Reward;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.CompassUtil;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


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
                HttpRequest.RequestCallback,
                Parser.ParserCallback{

    public static final String REWARD_KEY = "org.tndata.compass.Reward.Reward";


    private CheckInRewardListener mListener;
    private Reward mReward;
    private boolean mBetter;

    private TextView mHeader;
    private TextView mPreface;
    private TextView mContent;
    private TextView mAuthor;
    private ViewSwitcher mMoreSwitcher;


    /**
     * Creates an instance of the fragment.
     *
     * @param reward the initial reward to be displayed.
     * @return an instance of the fragment.
     */
    public static CheckInRewardFragment newInstance(Reward reward){
        Bundle args = new Bundle();
        args.putParcelable(REWARD_KEY, reward);

        CheckInRewardFragment fragment = new CheckInRewardFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mReward = getArguments().getParcelable(REWARD_KEY);
        mBetter = false;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            mListener = (CheckInRewardListener)context;
        }
        catch (ClassCastException ccx){
            throw new ClassCastException(context.toString()
                    + " must implement " + CheckInRewardListener.class);
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_check_in_reward, container, false);
    }

    @Override
    public void onViewCreated(View rootView, @Nullable Bundle savedInstanceState){
        mHeader = (TextView)rootView.findViewById(R.id.check_in_reward_header);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)mHeader.getLayoutParams();
        params.height = CompassUtil.getScreenWidth(getActivity())*2/3;
        mHeader.setLayoutParams(params);

        mPreface = (TextView)rootView.findViewById(R.id.check_in_reward_preface);
        mContent = (TextView)rootView.findViewById(R.id.check_in_reward_content);
        mAuthor = (TextView)rootView.findViewById(R.id.check_in_reward_author);
        mMoreSwitcher = (ViewSwitcher)rootView.findViewById(R.id.check_in_reward_switcher);
        rootView.findViewById(R.id.check_in_reward_more).setOnClickListener(this);
        rootView.findViewById(R.id.check_in_reward_share).setOnClickListener(this);
        rootView.findViewById(R.id.check_in_reward_home).setOnClickListener(this);

        populateUI();
    }

    /**
     * Populates the UI with the available reward.
     */
    private void populateUI(){
        if (mBetter){
            mHeader.setText(R.string.check_in_reward_better);
        }
        else{
            mHeader.setText(R.string.check_in_reward_worse);
        }

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

    /**
     * Updates the header string.
     *
     * @param better true if the user is doing better than last week, false otherwise.
     */
    public void update(boolean better){
        mBetter = better;
        if (mHeader != null){
            if (better){
                mHeader.setText(R.string.check_in_reward_better);
            }
            else{
                mHeader.setText(R.string.check_in_reward_worse);
            }
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.check_in_reward_more:
                mMoreSwitcher.showNext();
                fetchReward();
                break;

            case R.id.check_in_reward_share:
                mListener.onShareClick(mReward);
                break;

            case R.id.check_in_reward_home:
                mListener.onHomeClick();
                break;
        }
    }

    /**
     * Fires the request to get a new reward.
     */
    private void fetchReward(){
        HttpRequest.get(this, API.URL.getRandomReward());
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        Parser.parse(result, ParserModels.RewardResultSet.class, this);
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        Toast.makeText(getActivity(), R.string.check_in_reward_error, Toast.LENGTH_SHORT).show();
        mMoreSwitcher.showPrevious();
    }

    @Override
    public void onProcessResult(int requestCode, ResultSet result){

    }

    @Override
    public void onParseSuccess(int requestCode, ResultSet result){
        if (result instanceof ParserModels.RewardResultSet){
            mReward = ((ParserModels.RewardResultSet)result).results.get(0);
            populateUI();
            mMoreSwitcher.showPrevious();
        }
    }

    @Override
    public void onParseFailed(int requestCode){

    }


    /**
     * Listener interface for the reward fragment.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface CheckInRewardListener{
        /**
         * Called when the home button is clicked.
         */
        void onHomeClick();

        /**
         * Called when the share button is clicked.
         *
         * @param reward the reward on which the share was clicked.
         */
        void onShareClick(Reward reward);
    }
}
