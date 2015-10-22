package org.tndata.android.compass.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Survey;
import org.tndata.android.compass.model.SurveyOptions;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.Constants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * A view Containing a survey question. This class is not meant to be used within XML layout
 * specification files.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class SurveyView
        extends LinearLayout
        implements
                View.OnClickListener,
                AdapterView.OnItemSelectedListener,
                DatePicker.OnDateChangedListener,
                TextWatcher{

    private static final String TAG = "SurveyView";

    private Survey mSurvey;
    private SurveyViewListener mListener;

    private RadioButton mBinary1;
    private RadioButton mBinary2;

    private Spinner mMultipleChoice;

    private SeekBar mLikert;

    private EditText mOpenEnded;
    private DatePicker mOpenEndedDate;
    private boolean mForceDate;

    public SurveyView(Context context, Survey survey, SurveyViewListener listener){
        this(context, survey, listener, false);
    }

    public SurveyView(Context context, Survey survey, SurveyViewListener listener, boolean forceDate){
        super(context);
        mSurvey = survey;
        mListener = listener;
        mForceDate = forceDate;
        setOrientation(VERTICAL);
        init();
    }

    void init(){
        //Needed in the future if reuse functionality is to be implemented
        removeAllViews();

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
            instructions.setText(mSurvey.getInstructions());
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
        question.setText(mSurvey.getText());
        addView(question);

        //Question type specific fields
        switch (mSurvey.getQuestionType()){
            case Constants.SURVEY_BINARY:
                Log.d(TAG, "Binary survey");

                //Create the group and the buttons
                RadioGroup group = new RadioGroup(getContext());
                group.setOrientation(HORIZONTAL);

                mBinary1 = new RadioButton(getContext());
                mBinary1.setText(mSurvey.getOptions().get(0).getText());
                mBinary1.setOnClickListener(this);

                mBinary2 = new RadioButton(getContext());
                mBinary2.setText(mSurvey.getOptions().get(1).getText());
                mBinary2.setOnClickListener(this);

                //Put everything together and add the group.
                group.addView(mBinary1);
                group.addView(mBinary2);
                addView(group);
                break;

            case Constants.SURVEY_MULTICHOICE:
                Log.d(TAG, "Multiple choice survey");

                //Create the spinner
                mMultipleChoice = new Spinner(getContext());

                //Add a default option to the list
                SurveyOptions defaultOption = new SurveyOptions();
                defaultOption.setText(getContext().getString(R.string.survey_default_option));
                List<SurveyOptions> optionsList = mSurvey.getOptions();
                optionsList.add(0, defaultOption);

                //Create and set the adapter
                ArrayAdapter<SurveyOptions> adapter;
                adapter = new ArrayAdapter<>(getContext(), R.layout.list_item_simple_spinner, optionsList);
                mMultipleChoice.setAdapter(adapter);

                //Select the default option
                if (mSurvey.getSelectedOption() != null){
                    for (int i = 0; i < optionsList.size(); i++){
                        if (mSurvey.getSelectedOption().getId() == optionsList.get(i).getId()){
                            mMultipleChoice.setSelection(i, false);
                            break;
                        }
                    }
                }
                else{
                    mMultipleChoice.setSelection(0, false);
                }

                //Set the listener
                mMultipleChoice.setOnItemSelectedListener(this);

                //Add the view
                addView(mMultipleChoice);
                break;

            case Constants.SURVEY_LIKERT:
                Log.d(TAG, "Likert survey");
                break;

            case Constants.SURVEY_OPENENDED:
                Log.d(TAG, "Open ended survey");

                boolean date = mForceDate;
                date |= mSurvey.getInputType().equalsIgnoreCase(Constants.SURVEY_OPENENDED_DATE_TYPE);
                if (date){
                    Log.d(TAG, "Date");
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    mOpenEndedDate = (DatePicker)inflater.inflate(R.layout.spinner_picker, this, false);

                    Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    if (!mSurvey.getResponse().isEmpty()){
                        DateFormat parser = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        try{
                            c.setTime(parser.parse(mSurvey.getResponse()));
                            year = c.get(Calendar.YEAR);
                            month = c.get(Calendar.MONTH);
                            day = c.get(Calendar.DAY_OF_MONTH);
                        }
                        catch (ParseException px){
                            px.printStackTrace();
                        }
                    }

                    mOpenEndedDate.init(year, month, day, this);

                    addView(mOpenEndedDate);
                }
                else{
                    Log.d(TAG, "Text");

                    mOpenEnded = new EditText(getContext());
                    params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    mOpenEnded.setLayoutParams(params);
                    mOpenEnded.addTextChangedListener(this);

                    addView(mOpenEnded);
                }
                break;
        }
    }

    public void disable(){
        switch (mSurvey.getQuestionType()){
            case Constants.SURVEY_BINARY:
                mBinary1.setEnabled(false);
                mBinary2.setEnabled(false);
                break;

            case Constants.SURVEY_MULTICHOICE:
                mMultipleChoice.setEnabled(false);
                break;

            case Constants.SURVEY_LIKERT:
                break;

            case Constants.SURVEY_OPENENDED:
                boolean date = mForceDate;
                date |= mSurvey.getInputType().equalsIgnoreCase(Constants.SURVEY_OPENENDED_DATE_TYPE);
                if (date){
                    mOpenEndedDate.setEnabled(false);
                }
                else{
                    mOpenEnded.setEnabled(false);
                }
                break;
        }
    }

    @Override
    public void onClick(View view){
        switch (mSurvey.getQuestionType()){
            case Constants.SURVEY_BINARY:
                //Mark the selected option
                mSurvey.setSelectedOption(mSurvey.getOptions().get(view == mBinary1 ? 0 : 1));
                if (mListener != null){
                    mListener.onInputReady(mSurvey);
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
        switch (mSurvey.getQuestionType()){
            case Constants.SURVEY_MULTICHOICE:
                SurveyOptions option = (SurveyOptions)mMultipleChoice.getSelectedItem();
                if (option.getId() == -1){
                    if (mListener != null){
                        mListener.onInputCleared(mSurvey);
                    }
                }
                else{
                    mSurvey.setSelectedOption(option);
                    if (mListener != null) {
                        mListener.onInputReady(mSurvey);
                    }
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent){
        if (mListener != null){
            mListener.onInputCleared(mSurvey);
        }
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth){
        boolean date = mForceDate;
        date |= mSurvey.getInputType().equalsIgnoreCase(Constants.SURVEY_OPENENDED_DATE_TYPE);
        if (mSurvey.getQuestionType().equalsIgnoreCase(Constants.SURVEY_OPENENDED) && date){
            Calendar cal = Calendar.getInstance();
            cal.set(year, monthOfYear, dayOfMonth);
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            mSurvey.setResponse(formatter.format(cal.getTime()));
            if (mListener != null) {
                mListener.onInputReady(mSurvey);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after){
        //Unused
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count){
        //Unused
    }

    @Override
    public void afterTextChanged(Editable s){
        mSurvey.setResponse(s.toString());
        if (mListener != null){
            if (s.length() > 0){
                mListener.onInputReady(mSurvey);
            }
            else{
                mListener.onInputCleared(mSurvey);
            }
        }
    }

    public interface SurveyViewListener{
        void onInputReady(Survey survey);
        void onInputCleared(Survey survey);
    }
}
