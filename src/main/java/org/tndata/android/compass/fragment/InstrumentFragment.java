package org.tndata.android.compass.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Instrument;
import org.tndata.android.compass.model.Survey;
import org.tndata.android.compass.task.InstrumentLoaderTask;
import org.tndata.android.compass.task.SurveyResponseTask;

import java.util.ArrayList;

public class InstrumentFragment extends Fragment implements InstrumentLoaderTask
        .InstrumentLoaderListener,
        SurveyResponseTask.SurveyResponseListener, SurveyDialogFragment.SurveyDialogListener {
    private Button mNextButton;
    private ProgressBar mStatusProgressBar;
    private ProgressBar mLoadProgressBar;
    private TextView mInstructionsTextView;
    private LinearLayout mSurveyContainer;
    private ArrayList<Survey> mSurveys;
    private int mCurrentSurvey = -1;
    private int mInstrumentId = -1;
    private InstrumentFragmentListener mCallback;

    public interface InstrumentFragmentListener {
        public void instrumentFinished(int instrumentId);
    }

    public static InstrumentFragment newInstance(int instrumentId) {
        InstrumentFragment fragment = new InstrumentFragment();
        Bundle args = new Bundle();
        args.putInt("instrumentId", instrumentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstrumentId = getArguments() != null ? getArguments().getInt("instrumentId", -1) : -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_instrument, container, false);
        mStatusProgressBar = (ProgressBar) v
                .findViewById(R.id.instrument_fragment_status_progress);
        mLoadProgressBar = (ProgressBar) v
                .findViewById(R.id.instrument_fragment_load_progress);
        mSurveyContainer = (LinearLayout) v.findViewById(R.id.instrument_fragment_survey_container);
        mInstructionsTextView = (TextView) v.findViewById(R.id
                .instrument_fragment_info_label_textview);
        mNextButton = (Button) v
                .findViewById(R.id.instrument_next_button);
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
            mCallback = (InstrumentFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement InstrumentFragmentListener");
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
                ((CompassApplication) getActivity().getApplication()).getToken(),
                String.valueOf(mInstrumentId));
    }

    private void saveCurrentSurvey() {
        Survey survey = mSurveys.get(mCurrentSurvey);
        mNextButton.setEnabled(false);
        new SurveyResponseTask(getActivity(), this).executeOnExecutor(AsyncTask
                .THREAD_POOL_EXECUTOR, survey);
        showNextSurvey();
    }

    private void showNextSurvey() {
        mCurrentSurvey++;
        if (mCurrentSurvey < mSurveys.size()) {
            mStatusProgressBar.setProgress(mCurrentSurvey);
            Survey survey = mSurveys.get(mCurrentSurvey);
            mSurveyContainer.removeAllViews();

            SurveyDialogFragment fragment = SurveyDialogFragment.newInstance(survey, false, false);
            fragment.setListener(this);
            getFragmentManager().beginTransaction().add(mSurveyContainer.getId(), fragment,
                    "survey").commit();
        }
    }

    @Override
    public void instrumentsLoaded(ArrayList<Instrument> instruments) {
        mLoadProgressBar.setVisibility(View.GONE);
        if (instruments != null && !instruments.isEmpty()) {
            if (!instruments.get(0).getInstructions().isEmpty()) {
                mInstructionsTextView.setText(instruments.get(0).getInstructions());
            }
            mSurveys.addAll(instruments.get(0).getQuestions());
            mStatusProgressBar.setMax(mSurveys.size());
            showNextSurvey();
        }
    }

    @Override
    public void surveyResponseRecorded(Survey survey) {
        if (mCurrentSurvey >= mSurveys.size()) {
            try {
                mCallback.instrumentFinished(mInstrumentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDialogPositiveClick(Survey survey) {
        //not needed in this fragment
    }

    @Override
    public void onDialogNegativeClick(Survey survey) {
        //not needed in this fragment
    }

    @Override
    public void onDialogCanceled() {
        //not needed in this fragment
    }


    @Override
    public void setNextButtonEnabled(boolean enabled) {
        mNextButton.setEnabled(enabled);
    }

}
