package org.tndata.android.compass.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.UserProfileAdapter;
import org.tndata.android.compass.fragment.SurveyDialogFragment;
import org.tndata.android.compass.model.Survey;
import org.tndata.android.compass.model.UserProfile;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserCallback;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.NetworkRequest;


//TODO this class needs code fixin'
public class UserProfileActivity
        extends AppCompatActivity
        implements
                AdapterView.OnItemClickListener,
                NetworkRequest.RequestCallback,
                ParserCallback,
                SurveyDialogFragment.SurveyDialogListener{

    private CompassApplication mApp;

    private ProgressBar mProgressBar;
    private ListView mListView;
    private SurveyDialogFragment mSurveyDialog;

    private UserProfile mUserProfile;
    private UserProfile.SurveyResponse mSelectedSurveyResponse;
    private Survey mSelectedSurvey;

    private UserProfileAdapter mAdapter;


    private boolean mSurveyShown, mSurveyLoading = false;

    //Request codes
    private int mGetProfileRequestCode;
    private int mGetSurveyRequestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself);

        mApp = (CompassApplication)getApplication();

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
        mGetProfileRequestCode = NetworkRequest.get(this, this, API.getUserProfileUrl(),
                mApp.getToken());
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

        NetworkRequest.post(this, null, API.getPostSurveyUrl(survey), mApp.getToken(),
                API.getPostSurveyBody(survey));
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
        if (requestCode == mGetProfileRequestCode){
            Parser.parse(result, ParserModels.UserProfileResultSet.class, this);
        }
        else if (requestCode == mGetSurveyRequestCode){
            Parser.parse(result, Survey.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, String message){
        if (requestCode == mGetProfileRequestCode){
            mProgressBar.setVisibility(View.GONE);
        }
        else if (requestCode == mGetSurveyRequestCode){
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
            mGetSurveyRequestCode = NetworkRequest.get(this, this, API.getSurveyUrl(surveyUrl),
                    mApp.getToken());
            mSurveyLoading = true;
        }
    }
}
