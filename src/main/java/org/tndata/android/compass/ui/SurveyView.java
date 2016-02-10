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
import org.tndata.android.compass.model.SurveyOption;
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
                SeekBar.OnSeekBarChangeListener,
                DatePicker.OnDateChangedListener,
                TextWatcher{

    private static final String TAG = "SurveyView";
    private static final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;
    private static final int MARGIN = 5;


    private Survey mSurvey;
    private SurveyViewListener mListener;

    private RadioButton mBinary1;
    private RadioButton mBinary2;

    private Spinner mMultipleChoice;

    private SeekBar mLikert;
    private TextView mLikertMin;
    private TextView mLikertChoice;
    private TextView mLikertMax;

    private EditText mOpenEnded;
    private DatePicker mOpenEndedDate;
    private boolean mForceDate;


    /**
     * Constructor.
     *
     * @param context the context.
     * @param survey the survey to be displayed.
     * @param listener the listener object.
     */
    public SurveyView(Context context, Survey survey, SurveyViewListener listener){
        this(context, survey, listener, false);
    }

    /**
     * Constructor.
     *
     * @param context the context.
     * @param survey the survey to be displayed.
     * @param listener the listener object.
     * @param forceDate true if a date picker should be shown instead of the text input.
     */
    public SurveyView(Context context, Survey survey, SurveyViewListener listener, boolean forceDate){
        super(context);
        mSurvey = survey;
        mListener = listener;
        mForceDate = forceDate;
        setOrientation(VERTICAL);
        init();
    }

    /**
     * Creates the view, initializes all the components.
     */
    private void init(){
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
                params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                params.setMargins(CompassUtil.getPixels(getContext(), MARGIN), 0, 0, 0);
                group.setLayoutParams(params);

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
                params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                params.setMargins(CompassUtil.getPixels(getContext(), MARGIN), 0, 0, 0);
                mMultipleChoice.setLayoutParams(params);

                //Add a default option to the list
                SurveyOption defaultOption = new SurveyOption();
                defaultOption.setText(getContext().getString(R.string.survey_default_option));
                List<SurveyOption> optionsList = mSurvey.getOptions();
                optionsList.add(0, defaultOption);

                //Create and set the adapter
                ArrayAdapter<SurveyOption> adapter;
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

                LayoutInflater lInflater = LayoutInflater.from(getContext());
                View rootView = lInflater.inflate(R.layout.survey_likert, this, false);

                mLikert = (SeekBar)rootView.findViewById(R.id.survey_likert_seek_bar);
                mLikertMin = (TextView)rootView.findViewById(R.id.survey_likert_min);
                mLikertChoice = (TextView)rootView.findViewById(R.id.survey_likert_choice);
                mLikertMax = (TextView)rootView.findViewById(R.id.survey_likert_max);

                mLikert.setMax(mSurvey.getOptions().size() - 1);
                mLikertMin.setText(mSurvey.getOptions().get(0).getText());
                mLikertMax.setText(mSurvey.getOptions().get(mSurvey.getOptions().size() - 1).getText());

                if (mSurvey.getSelectedOption() != null){
                    mLikert.setProgress((int)mSurvey.getSelectedOption().getId());
                    mLikertChoice.setText(mSurvey.getSelectedOption().getText());
                    if (mListener != null){
                        mListener.onInputReady(mSurvey);
                    }
                }
                else{
                    mLikert.setProgress(0);
                    mLikertChoice.setText("");
                    mSurvey.setSelectedOption(mSurvey.getOptions().get(0));
                }

                mLikert.setOnSeekBarChangeListener(this);

                addView(rootView);
                break;

            case Constants.SURVEY_OPENENDED:
                Log.d(TAG, "Open ended survey");

                boolean date = mForceDate;
                date |= mSurvey.getInputType().equalsIgnoreCase(Constants.SURVEY_OPENENDED_DATE_TYPE);
                if (date){
                    Log.d(TAG, "Date");
                    LayoutInflater dInflater = LayoutInflater.from(getContext());
                    mOpenEndedDate = (DatePicker)dInflater.inflate(R.layout.survey_spinner_picker, this, false);

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

    /**
     * Disables this survey question.
     */
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
                mLikert.setEnabled(false);
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
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
        if (mSurvey.getQuestionType().equalsIgnoreCase(Constants.SURVEY_LIKERT)){
            mLikertMin.setVisibility(View.GONE);
            mLikertMax.setVisibility(View.GONE);
            for (SurveyOption option:mSurvey.getOptions()){
                if ((option.getId()-1) == progress){
                    mSurvey.setSelectedOption(option);
                    mLikertChoice.setText(option.getText());
                    break;
                }
            }
            if (mListener != null){
                mListener.onInputReady(mSurvey);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar){
        //Unused
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar){
        //Unused
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
        switch (mSurvey.getQuestionType()){
            case Constants.SURVEY_MULTICHOICE:
                SurveyOption option = (SurveyOption)mMultipleChoice.getSelectedItem();
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


    /**
     * The listener interface for the SurveyView.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface SurveyViewListener{
        /**
         * Called once the input to the question is ready to be processed.
         *
         * @param survey the survey represented by the view.
         */
        void onInputReady(Survey survey);

        /**
         * Called when the input to the question is cleared or invalidated.
         *
         * @param survey the survey represented by the view.
         */
        void onInputCleared(Survey survey);
    }
}
