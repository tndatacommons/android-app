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
import org.tndata.android.compass.parser.LegacyParser;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserCallback;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.NetworkRequest;

import java.util.ArrayList;
import java.util.List;


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
    private List<Survey> mProfileSurveyItems = new ArrayList<>();
    private UserProfileAdapter mAdapter;
    private boolean mSurveyShown, mSurveyLoading = false;
    private SurveyDialogFragment mSurveyDialog;
    private Survey mSelectedSurvey;

    //Request codes
    private int mGetBioRequestCode;
    private int mGetSurveyRequestCode;
    private int mPostSurveyRequestCode;


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

        ListView listView = (ListView) findViewById(R.id.myself_listview);
        mProgressBar = (ProgressBar) findViewById(R.id.myself_load_progress);

        mAdapter = new UserProfileAdapter(this, R.id.list_item_user_profile_question_textview,
                mProfileSurveyItems);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        loadUserProfile();
    }

    private void loadUserProfile(){
        mProgressBar.setVisibility(View.VISIBLE);
        mGetBioRequestCode = NetworkRequest.get(this, this, API.getUserProfileUrl(), mApp.getToken());
    }

    private void showSurvey(Survey survey){
        mSurveyShown = true;
        mSurveyDialog = SurveyDialogFragment.newInstance(survey);
        mSurveyDialog.setListener(this);
        mSurveyDialog.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onDialogPositiveClick(Survey survey){
        for (int i = 0; i < mProfileSurveyItems.size(); i++){
            Survey s = mProfileSurveyItems.get(i);
            if (s.getId() == survey.getId() && s.getQuestionType().equalsIgnoreCase(survey
                    .getQuestionType())){
                mProfileSurveyItems.set(i, survey);
                break;
            }
        }
        mPostSurveyRequestCode = NetworkRequest.post(this, this, API.getPostSurveyUrl(survey),
                mApp.getToken(), API.getPostSurveyBody(survey));
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
        if (requestCode == mGetBioRequestCode){
            List<Survey> surveys = LegacyParser.parseProfileBio(result);
            mProgressBar.setVisibility(View.GONE);
            mProfileSurveyItems.clear();
            mProfileSurveyItems.addAll(surveys);
            mAdapter.notifyDataSetChanged();
        }
        else if (requestCode == mGetSurveyRequestCode){
            Parser.parse(result, Survey.class, this);
        }
        else if (requestCode == mPostSurveyRequestCode){
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestFailed(int requestCode, String message){
        if (requestCode == mGetBioRequestCode){
            mProgressBar.setVisibility(View.GONE);
        }
        else if (requestCode == mGetSurveyRequestCode){
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof Survey){
            Survey survey = (Survey)result;
            if (survey.getId() == mSelectedSurvey.getId()
                    && survey.getQuestionType().equalsIgnoreCase(mSelectedSurvey.getQuestionType())){

                survey.setSelectedOption(mSelectedSurvey.getSelectedOption());
                survey.setResponse(mSelectedSurvey.getResponse());
            }
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof Survey){
            mSurveyLoading = false;
            mProgressBar.setVisibility(View.GONE);
            showSurvey((Survey)result);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        if (!mSurveyLoading && !mSurveyShown){
            mProgressBar.setVisibility(View.VISIBLE);
            mSelectedSurvey = mProfileSurveyItems.get(position);
            String surveyUrl = mSelectedSurvey.getQuestionType() + "-" + mSelectedSurvey.getId() + "/";
            mGetSurveyRequestCode = NetworkRequest.get(this, this, API.getSurveyUrl(surveyUrl),
                    mApp.getToken());
            mSurveyLoading = true;
        }
    }
}
