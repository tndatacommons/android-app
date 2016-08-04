package org.tndata.android.compass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.AwardsAdapter;
import org.tndata.android.compass.model.Badge;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.ParallaxEffect;

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
                AwardsAdapter.BadgeAdapterListener{

    private static final String TAG = "AwardsActivity";

    private AwardsAdapter mAdapter;
    private TextView mDefaultTextView;

    private int mGetBadgesUrl;

    private void hideDefaultText() {
        // If we have any awards, hide the default text view.
        // NOTE: getItemCount() returns number of badges + 1, so we need to ensure it's value > 1
        if(mAdapter.getItemCount() > 1 && mDefaultTextView != null) {
            mDefaultTextView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awards);

        View header = findViewById(R.id.awards_illustration); assert header != null;
        mDefaultTextView = (TextView) findViewById(R.id.awards_default_text);

        mAdapter = new AwardsAdapter(this, this);

        RecyclerView list = (RecyclerView)findViewById(R.id.awards_list); assert list != null;
        list.setAdapter(mAdapter);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.addOnScrollListener(new ParallaxEffect(header, 0.5f));

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
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.AwardsResultSet){
            List<Award> awards = ((ParserModels.AwardsResultSet)result).results;
            List<Badge> badges = new ArrayList<>();
            for (Award award:awards){
                badges.add(award.mBadge);
            }
            mAdapter.setBadges(badges);
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.AwardsResultSet){
            mAdapter.notifyDataSetChanged();
        }
        hideDefaultText();
    }

    @Override
    public void onBadgeSelected(Badge badge){
        startActivity(new Intent(this, BadgeActivity.class)
                .putExtra(BadgeActivity.BADGE_KEY, badge));
    }


    public class Award implements ParserModels.ResultSet{
        public static final String API_TYPE = "award";


        @SerializedName("badge")
        private Badge mBadge;
    }
}
