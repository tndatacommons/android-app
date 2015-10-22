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
import org.tndata.android.compass.task.SaveSurveyResponseTask;
import org.tndata.android.compass.ui.SurveyView;

import java.util.ArrayList;
import java.util.List;


/**
 * Fragment that displays an entire instrument arranged in pages.
 *
 * @author Edited by Ismael Alonso
 * @version 2.0.0
 */
public class InstrumentFragment
        extends Fragment
        implements
                View.OnClickListener,
                InstrumentLoaderTask.InstrumentLoaderListener,
                SaveSurveyResponseTask.SaveSurveyResponseListener,
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
    private List<Survey> mSurveys;
    private int mCurrentSurvey;

    //Ready array
    private boolean mQuestionReady[];
    private Survey mCurrentSurveys[];

    //Callback interface
    private InstrumentFragmentCallback mCallback;


    /**
     * Creates a new instance of the fragment with the specified parameters.
     *
     * @param instrumentId the id of the instrument to be loaded.
     * @param pageQuestions the number of questions to be displayed per page.
     * @return an InstrumentFragment.
     */
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
        mNext.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
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

    @Override
    public void onClick(View view){
        if (view.getId() == R.id.instrument_next){
            mProgress.setProgress(mCurrentSurvey);
            saveCurrentSurveySet();
        }
    }

    /**
     * Loads the survey list in the requested instrument.
     */
    private void loadSurveys(){
        mLoading.setVisibility(View.VISIBLE);
        new InstrumentLoaderTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                ((CompassApplication)getActivity().getApplication()).getToken(),
                String.valueOf(mInstrumentId));
    }

    @Override
    public void instrumentsLoaded(ArrayList<Instrument> instruments){
        mLoading.setVisibility(View.GONE);
        if (instruments != null && !instruments.isEmpty()){
            if (!instruments.get(0).getInstructions().isEmpty()){
                mInstructions.setText(instruments.get(0).getInstructions());
            }
            mSurveys = new ArrayList<>();
            mSurveys.addAll(instruments.get(0).getQuestions());
            mProgress.setMax(mSurveys.size());
            showNextSurveySet();
        }
        else{
            Log.d(TAG, "No instrument loaded");
        }
    }

    /**
     * Saves the responses to the current survey set being displayed.
     */
    private void saveCurrentSurveySet(){
        mNext.setEnabled(false);
        for (int i = 0; i < mSurveyContainer.getChildCount(); i++){
            ((SurveyView)mSurveyContainer.getChildAt(i)).disable();
        }
        new SaveSurveyResponseTask(getActivity(), this).execute(mCurrentSurveys);
    }

    @Override
    public void onSurveySetResponseRecorded(List<Survey> survey){
        if (mCurrentSurvey >= mSurveys.size()){
            mCallback.onInstrumentFinished(mInstrumentId);
        }
        else{
            for (int i = 0; i < mPageQuestions; i++){
                mCurrentSurveys[i] = null;
            }
            showNextSurveySet();
        }
    }

    /**
     * Displays the next set of surveys.
     */
    private void showNextSurveySet(){
        mSurveyContainer.removeAllViews();
        int lastSurvey = mCurrentSurvey + mPageQuestions;
        while (mCurrentSurvey < mSurveys.size() && mCurrentSurvey < lastSurvey){
            Survey survey = mSurveys.get(mCurrentSurvey);
            Log.d(TAG, survey.toString());

            mSurveyContainer.addView(new SurveyView(getActivity(), survey, this));
            mQuestionReady[mCurrentSurvey%mPageQuestions] = false;
            mCurrentSurveys[mCurrentSurvey%mPageQuestions] = survey;
            mCurrentSurvey++;
        }
    }

    @Override
    public void onInputReady(Survey survey){
        mQuestionReady[mSurveys.indexOf(survey)%mPageQuestions] = true;
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
        mQuestionReady[mSurveys.indexOf(survey)%mPageQuestions] = false;
    }


    /**
     * Callback interface for the InstrumentFragment.
     *
     * @author Edited by Ismael Alonso
     * @version 1.0.0
     */
    public interface InstrumentFragmentCallback{
        /**
         * Called when the fragment is done displaying surveys.
         *
         * @param instrumentId the id of the instrument delivered by the fragment.
         */
        void onInstrumentFinished(int instrumentId);
    }
}
