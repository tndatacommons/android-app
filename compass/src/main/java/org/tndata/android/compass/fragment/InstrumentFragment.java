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
import org.tndata.compass.model.Instrument;
import org.tndata.compass.model.Survey;
import org.tndata.android.compass.ui.SurveyView;


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
                SurveyView.SurveyViewListener{

    private static final String TAG = "InstrumentFragment";

    private static final String INSTRUMENT_KEY = "org.tndata.compass.Instrument.Instrument";
    private static final String PAGE_QUESTIONS_KEY = "org.tndata.compass.Instrument.PageQuestions";


    //UI components
    private ProgressBar mProgress;
    private LinearLayout mSurveyContainer;
    private ViewSwitcher mNextSwitcher;
    private Button mNext;

    //Survey related attributes
    private Instrument mInstrument;
    private int mPageQuestions;
    private int mCurrentSurvey;

    //Ready array
    private boolean mQuestionReady[];
    private Survey mCurrentSurveys[];

    //Callback interface
    private InstrumentFragmentCallback mCallback;


    /**
     * Creates a new instance of the fragment with the specified parameters.
     *
     * @param instrument the instrument to be displayed.
     * @param pageQuestions the number of questions to be displayed per page.
     * @return an InstrumentFragment.
     */
    public static InstrumentFragment newInstance(Instrument instrument, int pageQuestions){
        InstrumentFragment fragment = new InstrumentFragment();
        Bundle args = new Bundle();
        args.putParcelable(INSTRUMENT_KEY, instrument);
        args.putInt(PAGE_QUESTIONS_KEY, pageQuestions);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mInstrument = getArguments().getParcelable(INSTRUMENT_KEY);
        mPageQuestions = getArguments().getInt(PAGE_QUESTIONS_KEY, 1);
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
        TextView instructions = (TextView)rootView.findViewById(R.id.instrument_info);
        mProgress = (ProgressBar)rootView.findViewById(R.id.instrument_progress);
        mSurveyContainer = (LinearLayout)rootView.findViewById(R.id.instrument_survey_container);
        mNextSwitcher = (ViewSwitcher)rootView.findViewById(R.id.instrument_next_switcher);
        mNext = (Button)rootView.findViewById(R.id.instrument_next);

        instructions.setText(mInstrument.getInstructions());
        mProgress.setMax(mInstrument.size());

        mNext.setEnabled(false);
        mNext.setOnClickListener(this);

        showNextSurveySet();
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
            if (mCurrentSurvey >= mInstrument.size()){
                mNextSwitcher.showNext();
                mCallback.onInstrumentFinished(mInstrument);
            }
            else{
                showNextSurveySet();
            }
        }
    }

    /**
     * Displays the next set of surveys.
     */
    private void showNextSurveySet(){
        mNext.setEnabled(false);
        mSurveyContainer.removeAllViews();
        int lastSurvey = mCurrentSurvey + mPageQuestions;
        while (mCurrentSurvey < mInstrument.size() && mCurrentSurvey < lastSurvey){
            Survey survey = mInstrument.get(mCurrentSurvey);
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
        mQuestionReady[mInstrument.getQuestions().indexOf(survey)%mPageQuestions] = true;
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
        mQuestionReady[mInstrument.getQuestions().indexOf(survey)%mPageQuestions] = false;
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
         * @param instrument the complete instrument.
         */
        void onInstrumentFinished(Instrument instrument);
    }
}
