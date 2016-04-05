package org.tndata.android.compass.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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


    public SurveyView(Context context){
        super(context);
    }

    public SurveyView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public SurveyView(Context context, AttributeSet attrs, int defStyleRes){
        super(context, attrs, defStyleRes);
    }

    /**
     * Sets a survey to this view.
     *
     * @param survey the survey to be displayed.
     * @param listener the listener object.
     */
    public void setSurvey(Survey survey, @Nullable SurveyViewListener listener){
        mSurvey = survey;
        mListener = listener;
        init();
    }

    /**
     * Creates the view, initializes all the components.
     */
    private void init(){
        setOrientation(VERTICAL);

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
            case Survey.BINARY:
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

            case Survey.MULTIPLE_CHOICE:
                Log.d(TAG, "Multiple choice survey");

                //Create the spinner
                mMultipleChoice = new Spinner(getContext());
                params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                params.setMargins(CompassUtil.getPixels(getContext(), MARGIN), 0, 0, 0);
                mMultipleChoice.setLayoutParams(params);

                //Add a default option to the list
                String defaultOptionString = getContext().getString(R.string.survey_default_option);
                SurveyOption defaultOption = new SurveyOption(0, defaultOptionString);
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

            case Survey.LIKERT:
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

            case Survey.OPEN_ENDED:
                Log.d(TAG, "Open ended survey");

                if (mSurvey.getInputType().equalsIgnoreCase(Survey.OPEN_ENDED_DATE_TYPE)){
                    Log.d(TAG, "Date");
                    LayoutInflater dInflater = LayoutInflater.from(getContext());
                    mOpenEndedDate = (DatePicker)dInflater.inflate(R.layout.survey_date_picker, this, false);

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

                    if (mSurvey.getInputType().equalsIgnoreCase(Survey.OPEN_ENDED_NUMBER_TYPE)){
                        mOpenEnded.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                    }

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
            case Survey.BINARY:
                mBinary1.setEnabled(false);
                mBinary2.setEnabled(false);
                break;

            case Survey.MULTIPLE_CHOICE:
                mMultipleChoice.setEnabled(false);
                break;

            case Survey.LIKERT:
                mLikert.setEnabled(false);
                break;

            case Survey.OPEN_ENDED:
                if (mSurvey.getInputType().equalsIgnoreCase(Survey.OPEN_ENDED_DATE_TYPE)){
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
            case Survey.BINARY:
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
        if (mSurvey.getQuestionType().equalsIgnoreCase(Survey.LIKERT)){
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
            case Survey.MULTIPLE_CHOICE:
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
        if (mSurvey.getInputType().equalsIgnoreCase(Survey.OPEN_ENDED_DATE_TYPE)){
            Calendar fourteen = Calendar.getInstance();
            fourteen.set(fourteen.get(Calendar.YEAR)-14, fourteen.get(Calendar.MONTH),
                    fourteen.get(Calendar.DAY_OF_MONTH)+1, 0, 0, 0);
            Calendar birthday = Calendar.getInstance();
            birthday.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
            if (birthday.before(fourteen)){
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                mSurvey.setResponse(formatter.format(birthday.getTime()));
                if (mListener != null){
                    mListener.onInputReady(mSurvey);
                }
            }
            else{
                if (mListener != null){
                    mListener.onInputCleared(mSurvey);
                }
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
