package org.tndata.android.compass.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ShareEvent;

import org.tndata.android.compass.R;
import org.tndata.android.compass.databinding.ActivityRewardBinding;
import org.tndata.compass.model.ResultSet;
import org.tndata.compass.model.Reward;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.CompassUtil;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * Activity used to display a piece of reward content.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class RewardActivity
        extends AppCompatActivity
        implements
                HttpRequest.RequestCallback,
                Parser.ParserCallback,
                View.OnClickListener{

    private static final String CONTENT_KEY = "org.tndata.compass.RewardActivity.Content";


    /**
     * Creates an intent to correctly launch the Activity.
     *
     * @param context a reference to the context.
     * @param content the reward to be displayed, null to fetch a random one.
     * @return the intent that launches RewardActivity.
     */
    public static Intent getIntent(@NonNull Context context, @Nullable Reward content){
        return new Intent(context, RewardActivity.class)
                .putExtra(CONTENT_KEY, content);
    }


    private ActivityRewardBinding mBinding;
    private Reward mReward;

    private int mGetRewardUrl;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_reward);

        setTitle("");
        mBinding.rewardToolbar.toolbar.setBackgroundColor(Color.TRANSPARENT);
        setSupportActionBar(mBinding.rewardToolbar.toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mBinding.rewardProgress.getIndeterminateDrawable().setColorFilter(
                Color.WHITE, PorterDuff.Mode.MULTIPLY
        );

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, CompassUtil.getScreenWidth(this)*2/3
        );
        mBinding.rewardIconContainer.setLayoutParams(params);

        Reward reward = getIntent().getParcelableExtra(CONTENT_KEY);
        if (reward == null){
            mGetRewardUrl = HttpRequest.get(this, API.URL.getRandomReward());
        }
        else{
            setReward(reward);
        }

        mBinding.rewardRefresh.setOnClickListener(this);
        mBinding.rewardShare.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetRewardUrl){
            Parser.parse(result, ParserModels.RewardResultSet.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        if (requestCode == mGetRewardUrl){
            Log.e("RewardActivity", "Reward couldn't be fetched");
        }
    }

    @Override
    public void onProcessResult(int requestCode, ResultSet result){

    }

    @Override
    public void onParseSuccess(int requestCode, ResultSet result){
        if (result instanceof ParserModels.RewardResultSet){
            setReward(((ParserModels.RewardResultSet)result).results.get(0));
        }
    }

    private void setReward(@Nullable Reward reward){
        mReward = reward;

        if (mReward == null){
            mBinding.rewardProgress.setVisibility(View.VISIBLE);
            mBinding.rewardContentContainer.setVisibility(View.GONE);
        }
        else {
            mBinding.rewardIcon.setImageResource(mReward.getIcon());
            mBinding.rewardCard.detailTitle.setText(mReward.getHeader());
            mBinding.rewardCard.detailContent.setText(mReward.format());

            mBinding.rewardProgress.setVisibility(View.GONE);
            mBinding.rewardContentContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onParseFailed(int requestCode){

    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.reward_refresh:
                setReward(null);
                mGetRewardUrl = HttpRequest.get(this, API.URL.getRandomReward());
                break;

            case R.id.reward_share:
                mReward.share(this);

                //Log the attempt to share this reward content.
                Answers.getInstance().logShare(new ShareEvent()
                        .putContentType("Reward")
                        .putContentName(mReward.getMessageType())
                        .putContentId(mReward.getId() + ""));
                break;
        }
    }
}
