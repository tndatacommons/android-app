package org.tndata.android.grow.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.tndata.android.grow.GrowApplication;
import org.tndata.android.grow.R;
import org.tndata.android.grow.model.Instrument;
import org.tndata.android.grow.model.Survey;
import org.tndata.android.grow.task.InstrumentLoaderTask;
import org.tndata.android.grow.task.SurveyResponseTask;
import org.tndata.android.grow.util.Constants;

import java.util.ArrayList;

public class QualityOfLifeFragment extends Fragment implements InstrumentLoaderTask.InstrumentLoaderListener,
        SurveyResponseTask.SurveyResponseListener {
    private Button mNextButton;
    private ProgressBar mStatusProgressBar;
    private ProgressBar mLoadProgressBar;
    private ArrayList<Survey> mSurveys;
    private int mCurrentSurvey = -1;
    private QualityOfLifeFragmentListener mCallback;

    public interface QualityOfLifeFragmentListener {
        public void qualityOfLifeFinished();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_quality_of_life, container, false);
        mStatusProgressBar = (ProgressBar) v
                .findViewById(R.id.qol_status_progress);
        mLoadProgressBar = (ProgressBar) v
                .findViewById(R.id.qol_load_progress);
        mNextButton = (Button) v
                .findViewById(R.id.choose_categories_done_button);
        mNextButton.setEnabled(false);
        mNextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                saveCurrentSurvey();
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSurveys = new ArrayList<Survey>();
        loadSurveys();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity); // This makes sure that the container activity
        // has implemented the callback interface. If not, it throws an
        // exception
        try {
            mCallback = (QualityOfLifeFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement QualityOfLifeFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private void loadSurveys() {
        mLoadProgressBar.setVisibility(View.VISIBLE);
        new InstrumentLoaderTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                ((GrowApplication) getActivity().getApplication()).getToken(), String.valueOf(Constants.QOL_INSTRUMENT_ID));
    }

    private void saveCurrentSurvey() {
        Survey survey = mSurveys.get(mCurrentSurvey);
        mNextButton.setEnabled(false);
        new SurveyResponseTask(getActivity(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, survey);
        showNextSurvey();
    }

    private void showNextSurvey() {
        mCurrentSurvey++;
        if (mCurrentSurvey < mSurveys.size()) {
            mStatusProgressBar.setProgress(mCurrentSurvey + 1);
            Survey survey = mSurveys.get(mCurrentSurvey);
            if (survey.getQuestionType().equalsIgnoreCase(Constants.BINARY)) {
                showBinarySurvey(survey);
            } else if (survey.getQuestionType().equalsIgnoreCase(Constants.LIKERT)) {
                showLikertSurvey(survey);
            } else if (survey.getQuestionType().equalsIgnoreCase(Constants.MULTICHOICE)) {
                showMultiChoiceSurvey(survey);
            }
        }
    }

    @Override
    public void instrumentsLoaded(ArrayList<Instrument> instruments) {
        mLoadProgressBar.setVisibility(View.GONE);
        if (instruments != null && !instruments.isEmpty()) {
            mSurveys.addAll(instruments.get(0).getQuestions());
            mStatusProgressBar.setMax(mSurveys.size());
            showNextSurvey();
        }
    }

    @Override
    public void surveyResponseRecorded(Survey survey) {
        if (mCurrentSurvey < mSurveys.size()) {
            mCallback.qualityOfLifeFinished();
        }
    }

    private void showBinarySurvey(Survey survey) {

    }

    private void showLikertSurvey(Survey survey) {

    }

    private void showMultiChoiceSurvey(Survey survey) {

    }
}
