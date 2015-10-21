package org.tndata.android.compass.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.tndata.android.compass.model.Survey;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.Constants;


/**
 * A view Containing a survey question. This class is not meant to be used within XML layout
 * specification files.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class SurveyView extends LinearLayout implements View.OnClickListener{
    private Survey mSurvey;
    private SurveyViewListener mListener;

    private RadioButton mYes;
    private RadioButton mNo;


    public SurveyView(Context context, Survey survey, SurveyViewListener listener){
        super(context);
        mSurvey = survey;
        mListener = listener;
        setOrientation(VERTICAL);

        //Instructions, add only if question has instructions
        if (!mSurvey.getInstructions().isEmpty()){
            TextView instructions = new TextView(getContext());
            LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            int vertical = CompassUtil.getPixels(getContext(), 15);
            int horizontal = CompassUtil.getPixels(getContext(), 5);
            params.setMargins(horizontal, vertical, horizontal, vertical);
            instructions.setLayoutParams(params);
            instructions.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
            instructions.setText(survey.getInstructions());
            addView(instructions);
        }

        //Question title
        TextView question = new TextView(getContext());
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int vertical = CompassUtil.getPixels(getContext(), 15);
        int horizontal = CompassUtil.getPixels(getContext(), 5);
        params.setMargins(horizontal, vertical, horizontal, vertical);
        question.setLayoutParams(params);
        question.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
        question.setText(survey.getText());
        addView(question);

        //Question type specific fields
        switch (survey.getQuestionType()){
            case Constants.SURVEY_BINARY:
                RadioGroup group = new RadioGroup(getContext());
                group.setOrientation(HORIZONTAL);

                mNo = new RadioButton(getContext());
                mNo.setText("No");
                mNo.setOnClickListener(this);

                mYes = new RadioButton(getContext());
                mYes.setText("Yes");
                mYes.setOnClickListener(this);

                group.addView(mNo);
                group.addView(mYes);
                addView(group);
                break;
        }
    }

    @Override
    public void onClick(View view){
        switch (mSurvey.getQuestionType()){
            case Constants.SURVEY_BINARY:
                //Mark the selected option: 0 -> no, 1 -> yes
                mSurvey.setSelectedOption(mSurvey.getOptions().get(view == mNo ? 0 : 1));
                if (mListener != null){
                    mListener.onInputReady(mSurvey);
                }
                break;
        }
    }


    public interface SurveyViewListener{
        void onInputReady(Survey survey);
        void onInputCleared(Survey survey);
    }
}
