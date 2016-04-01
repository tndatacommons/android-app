package org.tndata.android.compass.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.UserProfileAdapter;
import org.tndata.android.compass.fragment.SurveyDialogFragment;
import org.tndata.android.compass.model.Survey;
import org.tndata.android.compass.model.UserProfile;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


//TODO this class needs code fixin'
public class UserProfileActivity
        extends AppCompatActivity
        implements
                AdapterView.OnItemClickListener,
                HttpRequest.RequestCallback,
                Parser.ParserCallback,
                SurveyDialogFragment.SurveyDialogListener{

    private ProgressBar mProgressBar;
    private ListView mListView;
    private SurveyDialogFragment mSurveyDialog;

    private UserProfile mUserProfile;
    private UserProfile.SurveyResponse mSelectedSurveyResponse;
    private Survey mSelectedSurvey;

    private UserProfileAdapter mAdapter;


    private boolean mSurveyShown, mSurveyLoading = false;

    //Request codes
    private int mGetProfileRC;
    private int mGetSurveyRC;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(R.string.action_my_information);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mProgressBar = (ProgressBar) findViewById(R.id.myself_load_progress);
        mListView = (ListView) findViewById(R.id.myself_listview);
        mListView.setOnItemClickListener(this);

        loadUserProfile();
    }

    private void loadUserProfile(){
        mProgressBar.setVisibility(View.VISIBLE);
        mGetProfileRC = HttpRequest.get(this, API.getUserProfileUrl());
    }

    private void showSurvey(Survey survey){
        mSurveyShown = true;
        mSurveyDialog = SurveyDialogFragment.newInstance(survey);
        mSurveyDialog.setListener(this);
        mSurveyDialog.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onDialogPositiveClick(Survey survey){
        if (mSelectedSurveyResponse.isOpenEnded()){
            mSelectedSurveyResponse.setResponse(survey.getResponse());
        }
        else{
            mSelectedSurveyResponse.setSelectedOption(survey.getSelectedOption().getId());
            mSelectedSurveyResponse.setSelectedOptionText(survey.getSelectedOption().getText());
        }
        mAdapter.notifyDataSetChanged();

        HttpRequest.post(null, API.getPostSurveyUrl(survey), API.getPostSurveyBody(survey));
        mSurveyDialog.dismiss();
        mSurveyShown = false;
    }

    @Override
    public void onDialogNegativeClick(Survey survey){
        mSurveyDialog.dismiss();
        mSurveyShown = false;
    }

    @Override
    public void onDialogCanceled(){
        mSurveyShown = false;
    }

    @Override
    public void setNextButtonEnabled(boolean enabled){
        //not needed in this activity
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetProfileRC){
            Parser.parse(result, ParserModels.UserProfileResultSet.class, this);
        }
        else if (requestCode == mGetSurveyRC){
            Parser.parse(result, Survey.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        if (requestCode == mGetProfileRC){
            mProgressBar.setVisibility(View.GONE);
        }
        else if (requestCode == mGetSurveyRC){
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof Survey){
            mSelectedSurvey = (Survey)result;
            mSelectedSurvey.setSelectedOption(mSelectedSurveyResponse.getSelectedOption());
            mSelectedSurvey.setResponse(mSelectedSurveyResponse.getResponse());
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.UserProfileResultSet){
            mUserProfile = ((ParserModels.UserProfileResultSet)result).results.get(0);
            mProgressBar.setVisibility(View.GONE);
            mAdapter = new UserProfileAdapter(this, mUserProfile.getSurveyResponses());
            mListView.setAdapter(mAdapter);
        }
        if (result instanceof Survey){
            mSurveyLoading = false;
            mProgressBar.setVisibility(View.GONE);
            showSurvey(mSelectedSurvey);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        if (!mSurveyLoading && !mSurveyShown){
            mProgressBar.setVisibility(View.VISIBLE);
            mSelectedSurveyResponse = mUserProfile.getSurveyResponses().get(position);
            String surveyUrl = mSelectedSurveyResponse.getQuestionType() + "-"
                    + mSelectedSurveyResponse.getQuestionId() + "/";
            mGetSurveyRC = HttpRequest.get(this, API.getSurveyUrl(surveyUrl));
            mSurveyLoading = true;
        }
    }
}
