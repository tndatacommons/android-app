package org.tndata.android.grow.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.tndata.android.grow.GrowApplication;
import org.tndata.android.grow.R;
import org.tndata.android.grow.model.Instrument;
import org.tndata.android.grow.model.Survey;
import org.tndata.android.grow.model.SurveyOptions;
import org.tndata.android.grow.task.InstrumentLoaderTask;
import org.tndata.android.grow.task.SurveyResponseTask;
import org.tndata.android.grow.util.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class InstrumentFragment extends Fragment implements InstrumentLoaderTask
        .InstrumentLoaderListener,
        SurveyResponseTask.SurveyResponseListener {
    private Button mNextButton;
    private ProgressBar mStatusProgressBar;
    private ProgressBar mLoadProgressBar;
    private LinearLayout mSurveyContainer;
    private ArrayList<Survey> mSurveys;
    private int mCurrentSurvey = -1;
    private int mInstrumentId = -1;
    private InstrumentFragmentListener mCallback;

    public interface InstrumentFragmentListener {
        public void instrumentFinished(int instrumentId);
    }

    public static InstrumentFragment newInstance(int instrumentId) {
        InstrumentFragment fragment = new InstrumentFragment();
        Bundle args = new Bundle();
        args.putInt("instrumentId", instrumentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstrumentId = getArguments() != null ? getArguments().getInt("instrumentId", -1) : -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_quality_of_life, container, false);
        mStatusProgressBar = (ProgressBar) v
                .findViewById(R.id.qol_status_progress);
        mLoadProgressBar = (ProgressBar) v
                .findViewById(R.id.qol_load_progress);
        mSurveyContainer = (LinearLayout) v.findViewById(R.id.qol_survey_container);
        mNextButton = (Button) v
                .findViewById(R.id.qol_next_button);
        mNextButton.setEnabled(false);
        mNextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                saveCurrentSurvey();
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSurveys = new ArrayList<Survey>();
        loadSurveys();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity); // This makes sure that the container activity
        // has implemented the callback interface. If not, it throws an
        // exception
        try {
            mCallback = (InstrumentFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement InstrumentFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private void loadSurveys() {
        mLoadProgressBar.setVisibility(View.VISIBLE);
        new InstrumentLoaderTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                ((GrowApplication) getActivity().getApplication()).getToken(),
                String.valueOf(mInstrumentId));
    }

    private void saveCurrentSurvey() {
        Survey survey = mSurveys.get(mCurrentSurvey);
        mNextButton.setEnabled(false);
        new SurveyResponseTask(getActivity(), this).executeOnExecutor(AsyncTask
                .THREAD_POOL_EXECUTOR, survey);
        showNextSurvey();
    }

    private void showNextSurvey() {
        mCurrentSurvey++;
        if (mCurrentSurvey < mSurveys.size()) {
            mStatusProgressBar.setProgress(mCurrentSurvey);
            Survey survey = mSurveys.get(mCurrentSurvey);
            if (survey.getQuestionType().equalsIgnoreCase(Constants.SURVEY_BINARY)) {
                showBinarySurvey(survey);
            } else if (survey.getQuestionType().equalsIgnoreCase(Constants.SURVEY_LIKERT)) {
                showLikertSurvey(survey);
            } else if (survey.getQuestionType().equalsIgnoreCase(Constants.SURVEY_MULTICHOICE)) {
                showMultiChoiceSurvey(survey);
            } else if (survey.getQuestionType().equalsIgnoreCase(Constants.SURVEY_OPENENDED)) {
                showOpenEndedSurvey(survey);
            }
        }
    }

    @Override
    public void instrumentsLoaded(ArrayList<Instrument> instruments) {
        mLoadProgressBar.setVisibility(View.GONE);
        if (instruments != null && !instruments.isEmpty()) {
            mSurveys.addAll(instruments.get(0).getQuestions());
            mStatusProgressBar.setMax(mSurveys.size());
            showNextSurvey();
        }
    }

    @Override
    public void surveyResponseRecorded(Survey survey) {
        if (mCurrentSurvey >= mSurveys.size()) {
            mCallback.instrumentFinished(mInstrumentId);
        }
    }

    private void showBinarySurvey(final Survey survey) {
        mSurveyContainer.removeAllViews();
        View v = getActivity().getLayoutInflater().inflate(R.layout.view_survey_binary,
                mSurveyContainer);
        final TextView instructions = (TextView) v.findViewById(R.id
                .view_survey_binary_instructions_textview);
        if (survey.getInstructions().isEmpty()) {
            instructions.setVisibility(View.GONE);
        } else {
            instructions.setText(survey.getInstructions());
        }
        TextView title = (TextView) v.findViewById(R.id.view_survey_binary_title_textview);
        title.setText(survey.getText());
        final RadioButton negativeRadioButton = (RadioButton) v.findViewById(R.id
                .view_survey_binary_no_radiobutton);
        final RadioButton positiveRadioButton = (RadioButton) v.findViewById(R.id
                .view_survey_binary_yes_radiobutton);
        negativeRadioButton.setText(survey.getOptions().get(0).getText());
        positiveRadioButton.setText(survey.getOptions().get(1).getText());
        negativeRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                negativeRadioButton.setChecked(true);
                survey.setSelectedOption(survey.getOptions().get(0));
                mNextButton.setEnabled(true);
            }
        });
        positiveRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positiveRadioButton.setChecked(true);
                survey.setSelectedOption(survey.getOptions().get(1));
                mNextButton.setEnabled(true);
            }
        });
    }

    private void showLikertSurvey(final Survey survey) {
        mSurveyContainer.removeAllViews();
        View v = getActivity().getLayoutInflater().inflate(R.layout.view_survey_likert,
                mSurveyContainer);
        final TextView instructions = (TextView) v.findViewById(R.id
                .view_survey_likert_instructions_textview);
        if (survey.getInstructions().isEmpty()) {
            instructions.setVisibility(View.GONE);
        } else {
            instructions.setText(survey.getInstructions());
        }
        final TextView title = (TextView) v.findViewById(R.id.view_survey_likert_title_textview);
        title.setText(survey.getText());
        final TextView minTextView = (TextView) v.findViewById(R.id
                .view_survey_likert_seekbar_min_textview);
        final TextView maxTextView = (TextView) v.findViewById(R.id
                .view_survey_likert_seekbar_max_textview);
        final TextView choiceTextView = (TextView) v.findViewById(R.id
                .view_survey_likert_choice_textview);
        minTextView.setText(survey.getOptions().get(0).getText());
        maxTextView.setText(survey.getOptions().get(survey.getOptions().size() - 1).getText());
        final SeekBar seekBar = (SeekBar) v.findViewById(R.id.view_survey_likert_seekbar);
        seekBar.setMax(survey.getOptions().size() - 1);
        if (survey.getSelectedOption() != null) {
            seekBar.setProgress(survey.getSelectedOption().getId());
            choiceTextView.setText(survey.getSelectedOption().getText());
            mNextButton.setEnabled(true);
        } else {
            seekBar.setProgress(0);
            mNextButton.setEnabled(false);
            choiceTextView.setText("");
            survey.setSelectedOption(survey.getOptions().get(0));
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                for (SurveyOptions option : survey.getOptions()) {
                    if ((option.getId() - 1) == progress) {
                        survey.setSelectedOption(option);
                        choiceTextView.setText(option.getText());
                        break;
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mNextButton.setEnabled(true);
            }
        });
    }

    private void showMultiChoiceSurvey(final Survey survey) {
        mSurveyContainer.removeAllViews();
        View v = getActivity().getLayoutInflater().inflate(R.layout.view_survey_multichoice,
                mSurveyContainer);
        final TextView instructions = (TextView) v.findViewById(R.id
                .view_survey_multi_instructions_textview);
        if (survey.getInstructions().isEmpty()) {
            instructions.setVisibility(View.GONE);
        } else {
            instructions.setText(survey.getInstructions());
        }
        final TextView title = (TextView) v.findViewById(R.id.view_survey_multi_title_textview);
        title.setText(survey.getText());
        final Spinner spinner = (Spinner) v.findViewById(R.id.view_survey_multi_spinner);
        ArrayAdapter<SurveyOptions> adapter = new ArrayAdapter<SurveyOptions>(getActivity()
                .getApplicationContext(), R.layout.list_item_simple_spinner,
                survey.getOptions());
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SurveyOptions option = (SurveyOptions) spinner.getSelectedItem();
                survey.setSelectedOption(option);
                mNextButton.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showOpenEndedSurvey(final Survey survey) {
        mSurveyContainer.removeAllViews();
        View v = getActivity().getLayoutInflater().inflate(R.layout.view_survey_openended,
                mSurveyContainer);
        final TextView instructions = (TextView) v.findViewById(R.id
                .view_survey_openended_instructions_textview);
        if (survey.getInstructions().isEmpty()) {
            instructions.setVisibility(View.GONE);
        } else {
            instructions.setText(survey.getInstructions());
        }
        final TextView title = (TextView) v.findViewById(R.id.view_survey_openended_title_textview);
        title.setText(survey.getText());

        if (survey.getInputType().equalsIgnoreCase(Constants.SURVEY_OPENENDED_DATE_TYPE)) {
            final DatePicker datePicker = (DatePicker) v.findViewById(R.id
                    .view_survey_openended_datepicker);
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                    Calendar cal = Calendar.getInstance();
                    cal.set(year, monthOfYear, dayOfMonth);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String date = formatter.format(cal.getTime());
                    survey.setResponse(date);
                    mNextButton.setEnabled(true);
                }
            });
            datePicker.setVisibility(View.VISIBLE);
        } else {
            final EditText editText = (EditText) v.findViewById(R.id
                    .view_survey_openended_edittext);
            editText.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0) {
                        survey.setResponse(s.toString());
                        mNextButton.setEnabled(true);
                    } else {
                        mNextButton.setEnabled(false);
                    }
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });
            editText.setVisibility(View.VISIBLE);
        }
    }
}
