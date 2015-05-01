package org.tndata.android.grow.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.tndata.android.grow.GrowApplication;
import org.tndata.android.grow.R;
import org.tndata.android.grow.adapter.UserProfileAdapter;
import org.tndata.android.grow.fragment.SurveyDialogFragment;
import org.tndata.android.grow.model.Survey;
import org.tndata.android.grow.task.GetUserProfileTask;
import org.tndata.android.grow.task.GetUserProfileTask.UserProfileTaskInterface;
import org.tndata.android.grow.task.SurveyFinderTask;
import org.tndata.android.grow.task.SurveyResponseTask;

import java.util.ArrayList;

public class UserProfileActivity extends ActionBarActivity implements UserProfileTaskInterface,
        SurveyFinderTask.SurveyFinderInterface, SurveyDialogFragment.SurveyDialogListener,
        SurveyResponseTask.SurveyResponseListener {
    private Toolbar mToolbar;
    private ListView mListView;
    private ProgressBar mProgressBar;
    private ArrayList<Survey> mProfileSurveyItems = new ArrayList<Survey>();
    private UserProfileAdapter mAdapter;
    private boolean mSurveyShown, mSurveyLoading = false;
    private SurveyDialogFragment mSurveyDialog;

    private AdapterView.OnItemClickListener mProfileItemClickListener = new AdapterView
            .OnItemClickListener() {


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!mSurveyLoading && !mSurveyShown) {
                mProgressBar.setVisibility(View.VISIBLE);
                Survey survey = mProfileSurveyItems.get(position);
                String surveyUrlExtra = survey.getQuestionType() + "-" + String.valueOf(survey
                        .getId());
                new SurveyFinderTask(UserProfileActivity.this).executeOnExecutor(AsyncTask
                                .THREAD_POOL_EXECUTOR, ((GrowApplication) getApplication())
                                .getToken(),
                        surveyUrlExtra);
                mSurveyLoading = true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle(R.string.action_myself);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListView = (ListView) findViewById(R.id.myself_listview);
        mProgressBar = (ProgressBar) findViewById(R.id.myself_load_progress);

        mAdapter = new UserProfileAdapter(this, R.id.list_item_user_profile_question_textview,
                mProfileSurveyItems);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mProfileItemClickListener);

        loadUserProfile();
    }

    private void loadUserProfile() {
        mProgressBar.setVisibility(View.VISIBLE);
        new GetUserProfileTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                ((GrowApplication) getApplication()).getToken());
    }

    @Override
    public void userProfileFound(ArrayList<Survey> surveys) {
        mProgressBar.setVisibility(View.GONE);
        if (surveys == null) {
            return;
        }
        mProfileSurveyItems.clear();
        mProfileSurveyItems.addAll(surveys);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void surveyFound(Survey survey) {
        mSurveyLoading = false;
        mProgressBar.setVisibility(View.GONE);
        showSurvey(survey);
    }

    private void showSurvey(Survey survey) {
        mSurveyShown = true;
        mSurveyDialog = SurveyDialogFragment.newInstance(survey);
        mSurveyDialog.setListener(this);
        mSurveyDialog.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onDialogPositiveClick(Survey survey) {
        new SurveyResponseTask(UserProfileActivity.this, this).executeOnExecutor(AsyncTask
                .THREAD_POOL_EXECUTOR, survey);
        mSurveyDialog.dismiss();
        mSurveyShown = false;
    }

    @Override
    public void onDialogNegativeClick(Survey survey) {
        mSurveyDialog.dismiss();
        mSurveyShown = false;
    }

    @Override
    public void onDialogCanceled() {
        mSurveyShown = false;
    }

    @Override
    public void surveyResponseRecorded(Survey survey) {
        for (int i = 0; i < mProfileSurveyItems.size(); i++) {
            Survey s = mProfileSurveyItems.get(i);
            if (s.getId() == survey.getId() && s.getQuestionType().equalsIgnoreCase(survey
                    .getQuestionType())) {
                mProfileSurveyItems.set(i, survey);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setNextButtonEnabled(boolean enabled) {
        //not needed in this activity
    }

}
