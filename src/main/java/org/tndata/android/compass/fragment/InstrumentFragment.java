package org.tndata.android.compass.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import org.tndata.android.compass.ui.SurveyView;

import java.util.ArrayList;
import java.util.List;


public class InstrumentFragment
        extends Fragment
        implements
                InstrumentLoaderTask.InstrumentLoaderListener,
                SurveyResponseTask.SurveyResponseListener,
                SurveyDialogFragment.SurveyDialogListener,
                SurveyView.SurveyViewListener{

    private static final String TAG = "InstrumentFragment";

    public static final String INSTRUMENT_ID_KEY = "org.tndata.compass.Instrument.Id";
    public static final String PAGE_QUESTIONS_KEY = "org.tndata.compass.Instrument.PageQuestions";


    //UI components
    private TextView mInstructions;
    private ProgressBar mProgress;
    private ProgressBar mLoading;
    private LinearLayout mSurveyContainer;
    private Button mNext;

    //Survey related attributes
    private int mInstrumentId;
    private int mPageQuestions;
    private ArrayList<Survey> mSurveys;
    private int mCurrentSurvey;

    //Ready array
    private boolean mQuestionReady[];
    private Survey mCurrentSurveys[];

    //Callback interface
    private InstrumentFragmentCallback mCallback;


    public static InstrumentFragment newInstance(int instrumentId, int pageQuestions){
        InstrumentFragment fragment = new InstrumentFragment();
        Bundle args = new Bundle();
        args.putInt(INSTRUMENT_ID_KEY, instrumentId);
        args.putInt(PAGE_QUESTIONS_KEY, pageQuestions);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mInstrumentId = getArguments() != null ? getArguments().getInt(INSTRUMENT_ID_KEY, -1) : -1;
        mPageQuestions = getArguments() != null ? getArguments().getInt(PAGE_QUESTIONS_KEY, 1) : 1;
        mCurrentSurvey = 0;

        mQuestionReady = new boolean[mPageQuestions];
        mCurrentSurveys = new Survey[mPageQuestions];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_instrument, container, false);

        mInstructions = (TextView)rootView.findViewById(R.id.instrument_info);
        mProgress = (ProgressBar)rootView.findViewById(R.id.instrument_progress);
        mLoading = (ProgressBar)rootView.findViewById(R.id.instrument_loading);
        mSurveyContainer = (LinearLayout)rootView.findViewById(R.id.instrument_survey_container);
        mNext = (Button)rootView.findViewById(R.id.instrument_next);

        mNext.setEnabled(false);
        mNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mProgress.setProgress(mCurrentSurvey);
                saveCurrentSurvey();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mSurveys = new ArrayList<>();
        loadSurveys();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        // This makes sure that the container activity has implemented the callback
        // interface. If not, it throws an exception
        try{
            mCallback = (InstrumentFragmentCallback)activity;
        }
        catch (ClassCastException ccx){
            throw new ClassCastException(activity.toString()
                    + " must implement InstrumentFragmentListener");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mCallback = null;
    }

    private void loadSurveys(){
        mLoading.setVisibility(View.VISIBLE);
        new InstrumentLoaderTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                ((CompassApplication)getActivity().getApplication()).getToken(),
                String.valueOf(mInstrumentId));
    }

    private void saveCurrentSurvey(){
        for (int i = 0; i < mSurveyContainer.getChildCount(); i++){
            ((SurveyView)mSurveyContainer.getChildAt(i)).disable();
        }
        mNext.setEnabled(false);
        new SurveyResponseTask(getActivity(), this).executeOnExecutor(AsyncTask
                .THREAD_POOL_EXECUTOR, mCurrentSurveys);
        //showNextSurveySet();
    }

    private void showNextSurveySet(){
        mSurveyContainer.removeAllViews();
        int lastSurvey = mCurrentSurvey + mPageQuestions;
        while (mCurrentSurvey < mSurveys.size() && mCurrentSurvey < lastSurvey){
            mProgress.setProgress(mCurrentSurvey);
            Survey survey = mSurveys.get(mCurrentSurvey);
            Log.d(TAG, survey.toString());

            SurveyView surveyView;
            if (mInstrumentId == 6 && survey.getId() == 3){
                surveyView = new SurveyView(getActivity(), survey, this, true);
            }
            else{
                surveyView = new SurveyView(getActivity(), survey, this);
            }

            mSurveyContainer.addView(surveyView);
            mQuestionReady[mCurrentSurvey%mPageQuestions] = false;
            mCurrentSurveys[mCurrentSurvey%mPageQuestions] = survey;
            mCurrentSurvey++;
        }
    }

    @Override
    public void instrumentsLoaded(ArrayList<Instrument> instruments) {
        mLoading.setVisibility(View.GONE);
        if (instruments != null && !instruments.isEmpty()) {
            if (!instruments.get(0).getInstructions().isEmpty()) {
                mInstructions.setText(instruments.get(0).getInstructions());
            }
            mSurveys.addAll(instruments.get(0).getQuestions());
            mProgress.setMax(mSurveys.size());
            showNextSurveySet();
        }
        else{
            Log.d("InstrumentFragment", "no instruments loaded");
        }
    }

    @Override
    public void onSurveyResponseRecorded(List<Survey> survey){
        if (mCurrentSurvey >= mSurveys.size()){
            mCallback.instrumentFinished(mInstrumentId);
        }
        else{
            showNextSurveySet();
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
    public void setNextButtonEnabled(boolean enabled){
        mNext.setEnabled(enabled);
    }

    @Override
    public void onInputReady(Survey survey){
        mQuestionReady[(survey.getOrder()-1)%mPageQuestions] = true;
        mNext.setEnabled(true);
        for (boolean ready:mQuestionReady){
            if (!ready){
                mNext.setEnabled(false);
                break;
            }
        }
    }

    @Override
    public void onInputCleared(Survey survey){
        mNext.setEnabled(false);
        mQuestionReady[(survey.getOrder()-1)%mPageQuestions] = false;
    }

    /**
     *
     */
    public interface InstrumentFragmentCallback{
        void instrumentFinished(int instrumentId);
    }
}
