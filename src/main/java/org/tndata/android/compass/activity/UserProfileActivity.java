package org.tndata.android.compass.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.UserProfileAdapter;
import org.tndata.android.compass.fragment.SurveyDialogFragment;
import org.tndata.android.compass.model.Survey;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.util.API;

import es.sandwatch.httprequests.HttpRequest;


public class UserProfileActivity
        extends AppCompatActivity
        implements
                UserProfileAdapter.UserProfileAdapterListener,
                SurveyDialogFragment.SurveyDialogListener{

    private CompassApplication mApplication;
    private UserProfileAdapter mAdapter;
    private SurveyDialogFragment mSurveyDialog;


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

        mApplication = (CompassApplication)getApplication();

        mAdapter = new UserProfileAdapter(this, mApplication.getUser(), this);
        RecyclerView list = (RecyclerView)findViewById(R.id.profile_list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(mAdapter);
    }

    private void showSurvey(Survey survey){
        mSurveyDialog = SurveyDialogFragment.newInstance(survey);
        mSurveyDialog.setListener(this);
        mSurveyDialog.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onDialogPositiveClick(Survey survey){
        User user = mApplication.getUser();

        user.postSurvey(survey);
        mAdapter.notifyItemChanged((int)survey.getId());

        HttpRequest.put(null, API.getPutUserProfileUrl(user), API.getPutUserProfileBody(user));
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
        showSurvey(mApplication.getUser().generateSurvey(this, index));
    }
}
