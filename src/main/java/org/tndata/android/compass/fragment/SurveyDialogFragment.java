package org.tndata.android.compass.fragment;


import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Survey;
import org.tndata.android.compass.model.SurveyOption;
import org.tndata.android.compass.util.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//TODO OMG. FIX.
public class SurveyDialogFragment extends DialogFragment {
    private Survey mSurvey;
    private boolean mShouldShowPositiveButton = true;
    private boolean mShouldShowNegativeButton = true;
    private boolean mForceDatePicker = false;
    private Button mNegativeButton;
    private Button mPositiveButton;
    private SurveyDialogListener mCallback = null;

    public interface SurveyDialogListener {
        public void onDialogPositiveClick(Survey survey);

        public void onDialogNegativeClick(Survey survey);

        public void onDialogCanceled();

        public void setNextButtonEnabled(boolean enabled);
    }

    public static SurveyDialogFragment newInstance(Survey survey, boolean showNegativeButton,
                                                   boolean showPositiveButton) {
        SurveyDialogFragment f = new SurveyDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable("survey", survey);
        args.putBoolean("showNegativeButton", showNegativeButton);
        args.putBoolean("showPositiveButton", showPositiveButton);
        f.setArguments(args);

        return f;
    }

    public static SurveyDialogFragment newInstance(Survey survey) {
        return newInstance(survey, true, true);
    }

    public static SurveyDialogFragment newInstanceFD(Survey survey){
        SurveyDialogFragment f = new SurveyDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable("survey", survey);
        args.putBoolean("showNegativeButton", false);
        args.putBoolean("showPositiveButton", false);
        args.putBoolean("forceDatePicker", true);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSurvey = (Survey) getArguments().getSerializable("survey");
        mShouldShowNegativeButton = getArguments().getBoolean("showNegativeButton");
        mShouldShowPositiveButton = getArguments().getBoolean("showPositiveButton");
        mForceDatePicker = getArguments().getBoolean("forceDatePicker", false);
    }

    /*
     * MUST call set listener as the callback - we need to have a fragment,
     * not just an activity as a callback
     */
    public void setListener(SurveyDialogListener listener) {
        mCallback = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = null;
        String type = mSurvey.getQuestionType();
        if (type.equalsIgnoreCase(Constants.SURVEY_BINARY)) {
            v = inflater.inflate(R.layout.view_survey_binary, container, false);
            setupButtons(v);
            final TextView instructions = (TextView) v.findViewById(R.id
                    .view_survey_binary_instructions_textview);
            if (mSurvey.getInstructions().isEmpty()) {
                instructions.setVisibility(View.GONE);
            } else {
                instructions.setText(mSurvey.getInstructions());
            }
            TextView title = (TextView) v.findViewById(R.id.view_survey_binary_title_textview);
            title.setText(mSurvey.getText());
            final RadioButton negativeRadioButton = (RadioButton) v.findViewById(R.id
                    .view_survey_binary_no_radiobutton);
            final RadioButton positiveRadioButton = (RadioButton) v.findViewById(R.id
                    .view_survey_binary_yes_radiobutton);
            negativeRadioButton.setText(mSurvey.getOptions().get(0).getText());
            positiveRadioButton.setText(mSurvey.getOptions().get(1).getText());
            negativeRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    negativeRadioButton.setChecked(true);
                    mSurvey.setSelectedOption(mSurvey.getOptions().get(0));
                    if (mCallback != null) {
                        mCallback.setNextButtonEnabled(true);
                    }
                    mPositiveButton.setEnabled(true);
                }
            });
            positiveRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    positiveRadioButton.setChecked(true);
                    mSurvey.setSelectedOption(mSurvey.getOptions().get(1));
                    if (mCallback != null) {
                        mCallback.setNextButtonEnabled(true);
                    }
                    mPositiveButton.setEnabled(true);
                }
            });
        } else if (type.equalsIgnoreCase(Constants.SURVEY_MULTICHOICE)) {
            v = inflater.inflate(R.layout.view_survey_multichoice, container, false);
            setupButtons(v);
            final TextView instructions = (TextView) v.findViewById(R.id
                    .view_survey_multi_instructions_textview);
            if (mSurvey.getInstructions().isEmpty()) {
                instructions.setVisibility(View.GONE);
            } else {
                instructions.setText(mSurvey.getInstructions());
            }
            final TextView title = (TextView) v.findViewById(R.id
                    .view_survey_multi_title_textview);
            title.setText(mSurvey.getText());
            final Spinner spinner = (Spinner) v.findViewById(R.id.view_survey_multi_spinner);
            SurveyOption defaultOption = new SurveyOption();
            defaultOption.setText(getString(R.string.survey_default_option));
            List<SurveyOption> optionsList = mSurvey.getOptions();
            optionsList.add(0, defaultOption);
            ArrayAdapter<SurveyOption> adapter = new ArrayAdapter<SurveyOption>
                    (getActivity()
                            .getApplicationContext(), R.layout.list_item_simple_spinner,
                            mSurvey.getOptions());
            spinner.setAdapter(adapter);
            if (mSurvey.getSelectedOption() != null) {
                for (int i = 0; i < optionsList.size(); i++) {
                    if (mSurvey.getSelectedOption().getId() == optionsList.get(i).getId()) {
                        spinner.setSelection(i, false);
                        break;
                    }
                }
            } else {
                spinner.setSelection(0, false);
            }
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position,
                                           long id) {
                    SurveyOption option = (SurveyOption) spinner.getSelectedItem();
                    if (option.getId() == -1) {
                        return;
                    }
                    mSurvey.setSelectedOption(option);
                    if (mCallback != null) {
                        mCallback.setNextButtonEnabled(true);
                    }
                    mPositiveButton.setEnabled(true);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else if (type.equalsIgnoreCase(Constants.SURVEY_LIKERT)) {
            v = inflater.inflate(R.layout.view_survey_likert, container, false);
            setupButtons(v);
            final TextView instructions = (TextView) v.findViewById(R.id
                    .view_survey_likert_instructions_textview);
            if (mSurvey.getInstructions().isEmpty()) {
                instructions.setVisibility(View.GONE);
            } else {
                instructions.setText(mSurvey.getInstructions());
            }
            final TextView title = (TextView) v.findViewById(R.id
                    .view_survey_likert_title_textview);
            title.setText(mSurvey.getText());
            final TextView minTextView = (TextView) v.findViewById(R.id
                    .view_survey_likert_seekbar_min_textview);
            final TextView maxTextView = (TextView) v.findViewById(R.id
                    .view_survey_likert_seekbar_max_textview);
            final TextView choiceTextView = (TextView) v.findViewById(R.id
                    .view_survey_likert_choice_textview);
            minTextView.setText(mSurvey.getOptions().get(0).getText());
            maxTextView.setText(mSurvey.getOptions().get(mSurvey.getOptions().size() - 1)
                    .getText
                            ());
            final SeekBar seekBar = (SeekBar) v.findViewById(R.id.view_survey_likert_seekbar);
            seekBar.setMax(mSurvey.getOptions().size() - 1);
            if (mSurvey.getSelectedOption() != null) {
                seekBar.setProgress((int)mSurvey.getSelectedOption().getId());
                choiceTextView.setText(mSurvey.getSelectedOption().getText());
                if (mCallback != null) {
                    mCallback.setNextButtonEnabled(true);
                }
                mPositiveButton.setEnabled(true);
            } else {
                seekBar.setProgress(0);
                if (mCallback != null) {
                    mCallback.setNextButtonEnabled(false);
                }
                mPositiveButton.setEnabled(false);
                choiceTextView.setText("");
                mSurvey.setSelectedOption(mSurvey.getOptions().get(0));
            }
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    minTextView.setVisibility(View.GONE);
                    maxTextView.setVisibility(View.GONE);
                    for (SurveyOption option : mSurvey.getOptions()) {
                        if ((option.getId() - 1) == progress) {
                            mSurvey.setSelectedOption(option);
                            choiceTextView.setText(option.getText());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                int x = seekBar.getThumb().getBounds().centerX();
                                int width = choiceTextView.getMeasuredWidth();
                                if (progress == (mSurvey.getOptions().size() - 1)) {
                                    x = x - width;
                                } else if (progress != 0) {
                                    x = x - (width / 2);
                                }
                                choiceTextView.setX(x);
                            }
                            break;
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (mCallback != null) {
                        mCallback.setNextButtonEnabled(true);
                    }
                    mPositiveButton.setEnabled(true);
                }
            });

        } else if (type.equalsIgnoreCase(Constants.SURVEY_OPENENDED)) {
            v = inflater.inflate(R.layout.view_survey_openended, container, false);
            setupButtons(v);
            final TextView instructions = (TextView) v.findViewById(R.id
                    .view_survey_openended_instructions_textview);
            if (mSurvey.getInstructions().isEmpty()) {
                instructions.setVisibility(View.GONE);
            } else {
                instructions.setText(mSurvey.getInstructions());
            }
            final TextView title = (TextView) v.findViewById(R.id
                    .view_survey_openended_title_textview);
            title.setText(mSurvey.getText());

            if (mForceDatePicker || mSurvey.getInputType().equalsIgnoreCase(Constants.SURVEY_OPENENDED_DATE_TYPE)) {
                final DatePicker datePicker = (DatePicker) v.findViewById(R.id
                        .view_survey_openended_datepicker);
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                if (!mSurvey.getResponse().isEmpty()) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date date = formatter.parse(mSurvey.getResponse());
                        c.setTime(date);
                        year = c.get(Calendar.YEAR);
                        month = c.get(Calendar.MONTH);
                        day = c.get(Calendar.DAY_OF_MONTH);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(year, monthOfYear, dayOfMonth);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        String date = formatter.format(cal.getTime());
                        mSurvey.setResponse(date);
                        if (mCallback != null) {
                            mCallback.setNextButtonEnabled(true);
                        }
                        mPositiveButton.setEnabled(true);
                    }
                });
                datePicker.setVisibility(View.VISIBLE);
            } else {
                final EditText editText = (EditText) v.findViewById(R.id
                        .view_survey_openended_edittext);
                editText.addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        if (s.length() > 0) {
                            mSurvey.setResponse(s.toString());
                            mPositiveButton.setEnabled(true);
                            if (mCallback != null) {
                                mCallback.setNextButtonEnabled(true);
                            }
                        } else {
                            if (mCallback != null)
                                mCallback.setNextButtonEnabled(false);
                            mPositiveButton.setEnabled(false);
                        }
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before,
                                              int count) {
                    }
                });
                editText.setVisibility(View.VISIBLE);
            }
        }


        return v;
    }

    private void setupButtons(View v) {
        if (v != null) {
            mNegativeButton = (Button) v.findViewById(R.id.dialog_survey_negative_button);
            mNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onDialogNegativeClick(mSurvey);
                    }
                }
            });

            mPositiveButton = (Button) v.findViewById(R.id.dialog_survey_positive_button);
            mPositiveButton.setEnabled(false);
            mPositiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onDialogPositiveClick(mSurvey);
                    }
                }
            });

            if (mShouldShowPositiveButton) {
                mPositiveButton.setVisibility(View.VISIBLE);
            }
            if (mShouldShowNegativeButton) {
                mNegativeButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (mCallback != null) {
            mCallback.onDialogCanceled();
        }
    }
}
