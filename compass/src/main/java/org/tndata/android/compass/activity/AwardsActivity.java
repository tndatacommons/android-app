package org.tndata.android.compass.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.AwardsAdapter;
import org.tndata.android.compass.databinding.ActivityAwardsBinding;
import org.tndata.compass.model.Badge;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.ParallaxEffect;
import org.tndata.compass.model.ResultSet;

import java.util.ArrayList;
import java.util.List;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * Activity that displays the list of awards earned by the user.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class AwardsActivity
        extends AppCompatActivity
        implements
                HttpRequest.RequestCallback,
                Parser.ParserCallback,
                View.OnClickListener,
                AwardsAdapter.BadgeAdapterListener{

    private static final String TAG = "AwardsActivity";

    private ActivityAwardsBinding mBinding;

    private int mGetBadgesUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_awards);

        mBinding.awardsList.setLayoutManager(new LinearLayoutManager(this));
        new ParallaxEffect(mBinding.awardsHeader, 0.5f).attachToRecyclerView(mBinding.awardsList);

        mGetBadgesUrl = HttpRequest.get(this, API.URL.getBadges());
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetBadgesUrl){
            Log.d(TAG, result);
            Parser.parse(result, ParserModels.AwardsResultSet.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        Log.e(TAG, "GET badges failed");
        requestFailed();
    }

    @Override
    public void onProcessResult(int requestCode, ResultSet result){

    }

    @Override
    public void onParseSuccess(int requestCode, ResultSet result){
        if (result instanceof ParserModels.AwardsResultSet){
            List<Award> awards = ((ParserModels.AwardsResultSet)result).results;
            if (awards.size() == 0){
                mBinding.awardsMessage.setText(R.string.awards_no_badges);
                mBinding.awardsMessage.setVisibility(View.VISIBLE);
            }
            else{
                List<Badge> badges = new ArrayList<>();
                for (Award award : awards){
                    badges.add(award.mBadge);
                }
                AwardsAdapter adapter = new AwardsAdapter(this, badges, this);
                mBinding.awardsList.setAdapter(adapter);
            }
            mBinding.awardsProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onParseFailed(int requestCode){
        Log.e(TAG, "Parsing badges failed");
        requestFailed();
    }

    /**
     * Called when there is an error in the process of retrieving the user's awards.
     */
    private void requestFailed(){
        mBinding.awardsMessage.setText(R.string.awards_error);
        mBinding.awardsMessage.setOnClickListener(this);
        mBinding.awardsMessage.setVisibility(View.VISIBLE);
        mBinding.awardsProgress.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view){
        view.setOnClickListener(null);
        mBinding.awardsMessage.setVisibility(View.GONE);
        mBinding.awardsProgress.setVisibility(View.VISIBLE);

        mGetBadgesUrl = HttpRequest.get(this, API.URL.getBadges());
    }

    @Override
    public void onBadgeSelected(Badge badge){
        startActivity(new Intent(this, BadgeActivity.class)
                .putExtra(BadgeActivity.BADGE_KEY, badge));
    }

    public class Award implements ResultSet{
        public static final String API_TYPE = "award";


        @SerializedName("badge")
        private Badge mBadge;
    }
}
