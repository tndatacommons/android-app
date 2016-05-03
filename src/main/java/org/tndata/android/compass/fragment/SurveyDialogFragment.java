package org.tndata.android.compass.fragment;


import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Survey;
import org.tndata.android.compass.ui.SurveyView;


/**
 * Dialog to display a single survey question.
 *
 * @author Edited by Ismael Alonso.
 * @version 2.0.0
 */
public class SurveyDialogFragment
        extends DialogFragment
        implements
                SurveyView.SurveyViewListener,
                View.OnClickListener{

    private static final String SURVEY_KEY = "org.tndata.compass.SurveyDialogFragment.Survey";


    private Survey mSurvey;
    private Button mPositiveButton;
    private SurveyDialogListener mListener;


    /**
     * Instance creator.
     *
     * @param survey the survey to be displayed in the dialog.
     * @return the dialog instance.
     */
    public static SurveyDialogFragment newInstance(Survey survey){
        SurveyDialogFragment surveyDialogFragment = new SurveyDialogFragment();

        Bundle args = new Bundle();
        args.putParcelable(SURVEY_KEY, survey);
        surveyDialogFragment.setArguments(args);

        return surveyDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mSurvey = getArguments().getParcelable(SURVEY_KEY);
    }

    /*
     * MUST call set listener as the callback - we need to have a fragment,
     * not just an activity as a callback
     */
    public void setListener(SurveyDialogListener listener){
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.dialog_survey, container);
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState){
        ((SurveyView)rootView.findViewById(R.id.survey_survey_view)).setSurvey(mSurvey, null);

        mPositiveButton = (Button)rootView.findViewById(R.id.survey_positive);

        rootView.findViewById(R.id.survey_negative).setOnClickListener(this);
        mPositiveButton.setOnClickListener(this);
    }

    @Override
    public void onCancel(DialogInterface dialog){
        if (mListener != null){
            mListener.onDialogCanceled();
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.survey_negative:
                mListener.onDialogNegativeClick(mSurvey);
                break;

            case R.id.survey_positive:
                mListener.onDialogPositiveClick(mSurvey);
                break;
        }
    }

    @Override
    public void onInputReady(Survey survey){
        mPositiveButton.setEnabled(true);
    }

    @Override
    public void onInputCleared(Survey survey){
        mPositiveButton.setEnabled(false);
    }


    public interface SurveyDialogListener{
        void onDialogPositiveClick(Survey survey);

        void onDialogNegativeClick(Survey survey);

        void onDialogCanceled();
    }
}
