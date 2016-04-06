package org.tndata.android.compass.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.UserProfileAdapter;
import org.tndata.android.compass.fragment.SurveyDialogFragment;
import org.tndata.android.compass.model.Survey;
import org.tndata.android.compass.model.Profile;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


public class UserProfileActivity
        extends AppCompatActivity
        implements
                HttpRequest.RequestCallback,
                Parser.ParserCallback,
                UserProfileAdapter.UserProfileAdapterListener,
                SurveyDialogFragment.SurveyDialogListener{

    private ProgressBar mProgressBar;
    private RecyclerView mList;
    private SurveyDialogFragment mSurveyDialog;

    private Profile mProfile;

    private UserProfileAdapter mAdapter;

    //Request codes
    private int mGetProfileRC;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(R.string.action_my_information);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mProgressBar = (ProgressBar)findViewById(R.id.profile_progress);
        mList = (RecyclerView)findViewById(R.id.profile_list);
        mList.setLayoutManager(new LinearLayoutManager(this));

        mProgressBar.setVisibility(View.VISIBLE);
        mGetProfileRC = HttpRequest.get(this, API.getUserProfileUrl());
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetProfileRC){
            Parser.parse(result, ParserModels.ProfileResultSet.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        if (requestCode == mGetProfileRC){
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){

    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.ProfileResultSet){
            mProfile = ((ParserModels.ProfileResultSet)result).results.get(0);
            mProgressBar.setVisibility(View.GONE);
            mAdapter = new UserProfileAdapter(this, mProfile, this);
            mList.setAdapter(mAdapter);
        }
    }

    private void showSurvey(Survey survey){
        mSurveyDialog = SurveyDialogFragment.newInstance(survey);
        mSurveyDialog.setListener(this);
        mSurveyDialog.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onDialogPositiveClick(Survey survey){
        mProfile.postSurvey(survey);
        mAdapter.notifyItemChanged((int)survey.getId());

        HttpRequest.put(null, API.getPutUserProfileUrl(mProfile), API.getPutUserProfileBody(mProfile));
        mSurveyDialog.dismiss();
    }

    @Override
    public void onDialogNegativeClick(Survey survey){
        mSurveyDialog.dismiss();
    }

    @Override
    public void onDialogCanceled(){
        //Unused
    }

    @Override
    public void onQuestionSelected(int index){
        showSurvey(mProfile.generateSurvey(this, index));
    }
}
