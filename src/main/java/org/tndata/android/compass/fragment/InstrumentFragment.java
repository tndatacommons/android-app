package org.tndata.android.compass.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Instrument;
import org.tndata.android.compass.model.Survey;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.ui.SurveyView;
import org.tndata.android.compass.util.API;

import java.util.ArrayList;
import java.util.List;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


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
                SurveyView.SurveyViewListener,
                HttpRequest.RequestCallback,
                Parser.ParserCallback{

    private static final String TAG = "InstrumentFragment";

    public static final String INSTRUMENT_ID_KEY = "org.tndata.compass.Instrument.Id";
    public static final String PAGE_QUESTIONS_KEY = "org.tndata.compass.Instrument.PageQuestions";


    //UI components
    private TextView mInstructions;
    private ProgressBar mProgress;
    private ProgressBar mLoading;
    private LinearLayout mSurveyContainer;
    private ViewSwitcher mNextSwitcher;
    private Button mNext;

    //Survey related attributes
    private int mInstrumentId;
    private int mPageQuestions;
    private List<Survey> mSurveys;
    private int mCurrentSurvey;

    //Ready array
    private boolean mQuestionReady[];
    private Survey mCurrentSurveys[];

    //Request codes
    private int mGetInstrumentRC;
    private int mPostSurveyRC;

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
        return inflater.inflate(R.layout.fragment_instrument, container, false);
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState){
        mInstructions = (TextView)rootView.findViewById(R.id.instrument_info);
        mProgress = (ProgressBar)rootView.findViewById(R.id.instrument_progress);
        mLoading = (ProgressBar)rootView.findViewById(R.id.instrument_loading);
        mSurveyContainer = (LinearLayout)rootView.findViewById(R.id.instrument_survey_container);
        mNextSwitcher = (ViewSwitcher)rootView.findViewById(R.id.instrument_next_switcher);
        mNext = (Button)rootView.findViewById(R.id.instrument_next);

        mNext.setEnabled(false);
        mNext.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        loadSurveys();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        // This makes sure that the container activity has implemented the callback
        // interface. If not, it throws an exception
        try{
            mCallback = (InstrumentFragmentCallback)context;
        }
        catch (ClassCastException ccx){
            throw new ClassCastException(context.toString()
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
        mGetInstrumentRC = HttpRequest.get(this, API.getInstrumentUrl(mInstrumentId));
    }

    /**
     * Saves the responses to the current survey set being displayed.
     */
    private void saveCurrentSurveySet(){
        mNextSwitcher.showNext();
        for (int i = 0; i < mSurveyContainer.getChildCount(); i++){
            ((SurveyView)mSurveyContainer.getChildAt(i)).disable();
        }
        mPostSurveyRC = -1;
        for (int i = 0; i < mCurrentSurveys.length; i++){
            Survey survey = mCurrentSurveys[i];
            if (survey != null){
                if (i == mCurrentSurveys.length - 1 || mCurrentSurveys[i+1] == null){
                    mPostSurveyRC = HttpRequest.post(this, API.getPostSurveyUrl(survey),
                            API.getPostSurveyBody(survey));
                }
                else{
                    HttpRequest.post(this, API.getPostSurveyUrl(survey),
                            API.getPostSurveyBody(survey));
                }
            }
        }
    }

    /**
     * Displays the next set of surveys.
     */
    private void showNextSurveySet(){
        if (mSurveyContainer.getChildCount() > 0){
            mNextSwitcher.showPrevious();
        }
        mNext.setEnabled(false);
        mSurveyContainer.removeAllViews();
        int lastSurvey = mCurrentSurvey + mPageQuestions;
        while (mCurrentSurvey < mSurveys.size() && mCurrentSurvey < lastSurvey){
            Survey survey = mSurveys.get(mCurrentSurvey);
            Log.d(TAG, survey.toString());

            SurveyView surveyView = new SurveyView(getContext());
            surveyView.setSurvey(survey, this);
            mSurveyContainer.addView(surveyView);
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

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetInstrumentRC){
            Parser.parse(result, Instrument.class, this);
        }
        else if (requestCode == mPostSurveyRC){
            Log.d(TAG, "Survey set saved");
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
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        //TODO user feedback
        if (requestCode == mGetInstrumentRC){
            mLoading.setVisibility(View.GONE);
        }
        else if (requestCode == mPostSurveyRC){
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
    }

    /* no-op */
    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        //Unused
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof Instrument){
            Instrument instrument = (Instrument)result;
            if (!instrument.getInstructions().isEmpty()){
                mInstructions.setText(instrument.getInstructions());
            }
            mSurveys = new ArrayList<>();
            mSurveys.addAll(instrument.getQuestions());
            mProgress.setMax(mSurveys.size());
            showNextSurveySet();
            mLoading.setVisibility(View.GONE);
        }
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
