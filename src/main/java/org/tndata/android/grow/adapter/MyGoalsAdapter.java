package org.tndata.android.grow.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.tndata.android.grow.R;
import org.tndata.android.grow.model.Category;
import org.tndata.android.grow.model.Goal;
import org.tndata.android.grow.model.MyGoalsViewItem;
import org.tndata.android.grow.model.Survey;
import org.tndata.android.grow.model.SurveyOptions;
import org.tndata.android.grow.ui.GoalCellView;
import org.tndata.android.grow.util.Constants;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
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
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class MyGoalsAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public interface OnClickEvent {
        void onClick(View v, int position);
    }

    public interface MyGoalsAdapterInterface {
        public void surveyCompleted(Survey survey);

        public void chooseGoals(Category category);

        public void chooseBehaviors(Goal goal, Category category);
    }

    private Context mContext;
    private List<MyGoalsViewItem> mItems;
    private MyGoalsAdapterInterface mCallback;

    static class MyGoalsViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView subTitleTextView;
        View circleView;
        LinearLayout noGoalsContainer;
        LinearLayout goalContainer;

        public MyGoalsViewHolder(View view) {
            super(view);
            circleView = view
                    .findViewById(R.id.list_item_my_goals_category_circle_view);
            titleTextView = (TextView) view
                    .findViewById(R.id.list_item_my_goals_category_title_textview);
            subTitleTextView = (TextView) view
                    .findViewById(R.id.list_item_my_goals_category_add_textview);
            noGoalsContainer = (LinearLayout) view.findViewById(R.id
                    .list_item_my_goals_category_no_goals_container);
            goalContainer = (LinearLayout) view.findViewById(R.id
                    .list_item_my_goals_category_goals_container);
        }
    }

    static class MyGoalsNoContentViewHolder extends RecyclerView.ViewHolder {
        public MyGoalsNoContentViewHolder(View view) {
            super(view);
        }
    }

    static class LikertSurveyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView minTextView;
        TextView maxTextView;
        TextView choiceTextView;
        TextView instructionsTextView;
        SeekBar seekBar;
        Button doneButton;

        public LikertSurveyViewHolder(View view) {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id
                    .list_item_survey_likert_title_textview);
            instructionsTextView = (TextView) view.findViewById(R.id
                    .list_item_survey_likert_instructions_textview);
            minTextView = (TextView) view.findViewById(R.id
                    .list_item_survey_likert_seekbar_min_textview);
            maxTextView = (TextView) view.findViewById(R.id
                    .list_item_survey_likert_seekbar_max_textview);
            choiceTextView = (TextView) view.findViewById(R.id
                    .list_item_survey_likert_choice_textview);
            seekBar = (SeekBar) view.findViewById(R.id.list_item_survey_likert_seekbar);
            doneButton = (Button) view.findViewById(R.id.list_item_survey_likert_done_button);
        }
    }

    static class MultiChoiceSurveyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView instructionsTextView;
        Spinner spinner;
        Button doneButton;

        public MultiChoiceSurveyViewHolder(View view) {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id
                    .list_item_survey_multi_title_textview);
            instructionsTextView = (TextView) view.findViewById(R.id
                    .list_item_survey_multi_instructions_textview);
            spinner = (Spinner) view.findViewById(R.id.list_item_survey_multi_spinner);
            doneButton = (Button) view.findViewById(R.id.list_item_survey_multi_done_button);
        }
    }

    static class BinarySurveyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView instructionsTextView;
        RadioButton negativeRadioButton;
        RadioButton positiveRadioButton;
        Button doneButton;

        public BinarySurveyViewHolder(View view) {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id
                    .list_item_survey_binary_title_textview);
            instructionsTextView = (TextView) view.findViewById(R.id
                    .list_item_survey_binary_instructions_textview);
            negativeRadioButton = (RadioButton) view.findViewById(R.id
                    .list_item_survey_binary_no_radiobutton);
            positiveRadioButton = (RadioButton) view.findViewById(R.id
                    .list_item_survey_binary_yes_radiobutton);
            doneButton = (Button) view.findViewById(R.id.list_item_survey_binary_done_button);
        }
    }

    static class OpenEndedSurveyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView instructionsTextView;
        EditText editText;
        DatePicker datePicker;
        Button doneButton;

        public OpenEndedSurveyViewHolder(View view) {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id
                    .list_item_survey_openended_title_textview);
            instructionsTextView = (TextView) view.findViewById(R.id
                    .list_item_survey_openended_instructions_textview);
            editText = (EditText) view.findViewById(R.id.list_item_survey_openended_edittext);
            datePicker = (DatePicker) view.findViewById(R.id.list_item_survey_openended_datepicker);
            doneButton = (Button) view.findViewById(R.id.list_item_survey_openended_done_button);
        }
    }

    public MyGoalsAdapter(Context context, List<MyGoalsViewItem> objects,
                          MyGoalsAdapterInterface callback) {
        if (objects == null) {
            throw new IllegalArgumentException("Goals List must not be null");
        }
        this.mItems = objects;
        this.mContext = context;
        this.mCallback = callback;
    }

    public void updateEntries(List<MyGoalsViewItem> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder,
                                 final int position) {
        final Survey survey = mItems.get(position).getSurvey();
        switch (viewHolder.getItemViewType()) {
            case MyGoalsViewItem.TYPE_DEFAULT_NO_CONTENT:
                break;
            case MyGoalsViewItem.TYPE_CATEGORY:
                final Category category = mItems.get(position).getCategory();
                ArrayList<Goal> goals = category.getGoals();
                ((MyGoalsViewHolder) viewHolder).titleTextView.setText(mContext.getString(R
                        .string.category_goals, category.getTitle()));
                ((MyGoalsViewHolder) viewHolder).goalContainer.removeAllViews();
                if (goals != null && !goals.isEmpty()) {
                    ((MyGoalsViewHolder) viewHolder).noGoalsContainer.setVisibility(View.GONE);
                    for (final Goal goal : goals) {
                        GoalCellView goalCellView = new GoalCellView(mContext);
                        goalCellView.setGoal(goal, category);
                        goalCellView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mCallback.chooseBehaviors(goal, category);
                            }
                        });
                        ((MyGoalsViewHolder) viewHolder).goalContainer.addView(goalCellView);
                    }
                } else {
                    GradientDrawable gradientDrawable = (GradientDrawable) ((MyGoalsViewHolder)
                            viewHolder).circleView.getBackground();
                    String colorString = category.getColor();
                    if (colorString != null && !colorString.isEmpty()) {
                        gradientDrawable.setColor(Color.parseColor(colorString));
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((MyGoalsViewHolder) viewHolder).circleView.setBackground
                                (gradientDrawable);
                    } else {
                        ((MyGoalsViewHolder) viewHolder).circleView
                                .setBackgroundDrawable(gradientDrawable);
                    }
                    ((MyGoalsViewHolder) viewHolder).noGoalsContainer.setVisibility(View.VISIBLE);
                    ((MyGoalsViewHolder) viewHolder).subTitleTextView.setText(mContext.getString
                            (R.string.category_goals_add,
                                    category.getTitle()));
                    ((MyGoalsViewHolder) viewHolder).noGoalsContainer.setOnClickListener(new View
                            .OnClickListener() {


                        @Override
                        public void onClick(View v) {
                            mCallback.chooseGoals(category);
                        }
                    });
                }
                break;

            case MyGoalsViewItem.TYPE_SURVEY_LIKERT:
                ((LikertSurveyViewHolder) viewHolder).titleTextView.setText(survey.getText());
                if (survey.getInstructions().isEmpty()) {
                    ((LikertSurveyViewHolder) viewHolder).instructionsTextView.setVisibility(View
                            .GONE);
                } else {
                    ((LikertSurveyViewHolder) viewHolder).instructionsTextView.setText(survey
                            .getInstructions());
                }
                ((LikertSurveyViewHolder) viewHolder).minTextView.setText(survey.getOptions().get
                        (0).getText());
                ((LikertSurveyViewHolder) viewHolder).maxTextView.setText(survey.getOptions().get
                        (survey.getOptions().size() - 1).getText());
                ((LikertSurveyViewHolder) viewHolder).seekBar.setMax(survey.getOptions().size() -
                        1);
                if (survey.getSelectedOption() != null) {
                    ((LikertSurveyViewHolder) viewHolder).seekBar.setProgress(survey
                            .getSelectedOption().getId());
                    ((LikertSurveyViewHolder) viewHolder).choiceTextView.setText(survey
                            .getSelectedOption().getText());
                    ((LikertSurveyViewHolder) viewHolder).doneButton.setEnabled(true);
                } else {
                    ((LikertSurveyViewHolder) viewHolder).seekBar.setProgress(0);
                    ((LikertSurveyViewHolder) viewHolder).doneButton.setEnabled(false);
                    ((LikertSurveyViewHolder) viewHolder).choiceTextView.setText("");
                    survey.setSelectedOption(survey.getOptions().get(0));
                }
                ((LikertSurveyViewHolder) viewHolder).seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        for (SurveyOptions option : survey.getOptions()) {
                            if ((option.getId() - 1) == progress) {
                                survey.setSelectedOption(option);
                                ((LikertSurveyViewHolder) viewHolder).choiceTextView.setText
                                        (option.getText());
                                break;
                            }
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        ((LikertSurveyViewHolder) viewHolder).doneButton.setEnabled(true);
                    }
                });
                ((LikertSurveyViewHolder) viewHolder).doneButton.setOnClickListener(new View
                        .OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallback.surveyCompleted(survey);
                    }
                });
                break;
            case MyGoalsViewItem.TYPE_SURVEY_MULTICHOICE:
                ((MultiChoiceSurveyViewHolder) viewHolder).titleTextView.setText(survey.getText());
                if (survey.getInstructions().isEmpty()) {
                    ((MultiChoiceSurveyViewHolder) viewHolder).instructionsTextView.setVisibility
                            (View
                                    .GONE);
                } else {
                    ((MultiChoiceSurveyViewHolder) viewHolder).instructionsTextView.setText(survey
                            .getInstructions());
                }
                ArrayAdapter<SurveyOptions> adapter = new ArrayAdapter<SurveyOptions>(mContext,
                        R.layout.list_item_simple_spinner, survey.getOptions());
                ((MultiChoiceSurveyViewHolder) viewHolder).spinner.setAdapter(adapter);
                ((MultiChoiceSurveyViewHolder) viewHolder).spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position,
                                               long id) {
                        SurveyOptions option = (SurveyOptions) ((MultiChoiceSurveyViewHolder)
                                viewHolder).spinner.getSelectedItem();
                        survey.setSelectedOption(option);
                        ((MultiChoiceSurveyViewHolder) viewHolder).doneButton.setEnabled(true);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                ((MultiChoiceSurveyViewHolder) viewHolder).doneButton.setOnClickListener(new View
                        .OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallback.surveyCompleted(survey);
                    }
                });
                break;
            case MyGoalsViewItem.TYPE_SURVEY_BINARY:
                ((BinarySurveyViewHolder) viewHolder).titleTextView.setText(survey.getText());
                if (survey.getInstructions().isEmpty()) {
                    ((BinarySurveyViewHolder) viewHolder).instructionsTextView.setVisibility(View
                            .GONE);
                } else {
                    ((BinarySurveyViewHolder) viewHolder).instructionsTextView.setText(survey
                            .getInstructions());
                }
                ((BinarySurveyViewHolder) viewHolder).doneButton.setEnabled(false);
                ((BinarySurveyViewHolder) viewHolder).negativeRadioButton.setText(survey
                        .getOptions().get(0).getText());
                ((BinarySurveyViewHolder) viewHolder).positiveRadioButton.setText(survey
                        .getOptions().get(1).getText());
                ((BinarySurveyViewHolder) viewHolder).negativeRadioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((BinarySurveyViewHolder) viewHolder).negativeRadioButton.setChecked(true);
                        survey.setSelectedOption(survey.getOptions().get(0));
                        ((BinarySurveyViewHolder) viewHolder).doneButton.setEnabled(true);
                    }
                });
                ((BinarySurveyViewHolder) viewHolder).positiveRadioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((BinarySurveyViewHolder) viewHolder).positiveRadioButton.setChecked(true);
                        survey.setSelectedOption(survey.getOptions().get(1));
                        ((BinarySurveyViewHolder) viewHolder).doneButton.setEnabled(true);
                    }
                });
                ((BinarySurveyViewHolder) viewHolder).doneButton.setOnClickListener(new View
                        .OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallback.surveyCompleted(survey);
                    }
                });
                break;
            case MyGoalsViewItem.TYPE_SURVEY_OPENENDED:
                ((OpenEndedSurveyViewHolder) viewHolder).titleTextView.setText(survey.getText());
                if (survey.getInstructions().isEmpty()) {
                    ((OpenEndedSurveyViewHolder) viewHolder).instructionsTextView.setVisibility(View
                            .GONE);
                } else {
                    ((OpenEndedSurveyViewHolder) viewHolder).instructionsTextView.setText(survey
                            .getInstructions());
                }
                if (survey.getInputType().equalsIgnoreCase(Constants.SURVEY_OPENENDED_DATE_TYPE)) {
                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    ((OpenEndedSurveyViewHolder) viewHolder).datePicker.init(year, month, day,
                            new DatePicker.OnDateChangedListener() {
                                @Override
                                public void onDateChanged(DatePicker view, int year,
                                                          int monthOfYear,
                                                          int dayOfMonth) {
                                    Calendar cal = Calendar.getInstance();
                                    cal.set(year, monthOfYear, dayOfMonth);
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                    String date = formatter.format(cal.getTime());
                                    survey.setResponse(date);
                                    ((OpenEndedSurveyViewHolder) viewHolder).doneButton
                                            .setEnabled(true);
                                }
                            });
                    ((OpenEndedSurveyViewHolder) viewHolder).datePicker.setVisibility(View.VISIBLE);
                } else {
                    ((OpenEndedSurveyViewHolder) viewHolder).editText.addTextChangedListener(new TextWatcher() {
                        public void afterTextChanged(Editable s) {
                            if (s.length() > 0) {
                                survey.setResponse(s.toString());
                                ((OpenEndedSurveyViewHolder) viewHolder).doneButton.setEnabled
                                        (true);
                            } else {
                                ((OpenEndedSurveyViewHolder) viewHolder).doneButton.setEnabled
                                        (false);
                            }
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count,
                                                      int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before,
                                                  int count) {
                        }
                    });
                    ((OpenEndedSurveyViewHolder) viewHolder).editText.setVisibility(View.VISIBLE);
                }
                ((OpenEndedSurveyViewHolder) viewHolder).doneButton.setOnClickListener(new View
                        .OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallback.surveyCompleted(survey);
                    }
                });
                break;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup,
                                                      int viewType) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView;
        switch (viewType) {

            case MyGoalsViewItem.TYPE_CATEGORY:
                itemView = inflater.inflate(
                        R.layout.list_item_my_goals_category, viewGroup, false);
                return new MyGoalsViewHolder(itemView);
            case MyGoalsViewItem.TYPE_DEFAULT_NO_CONTENT:
                itemView = inflater.inflate(
                        R.layout.list_item_default_no_content, viewGroup, false);
                return new MyGoalsNoContentViewHolder(itemView);
            case MyGoalsViewItem.TYPE_SURVEY_LIKERT:
                itemView = inflater.inflate(R.layout.list_item_survey_likert, viewGroup, false);
                return new LikertSurveyViewHolder(itemView);
            case MyGoalsViewItem.TYPE_SURVEY_MULTICHOICE:
                itemView = inflater.inflate(R.layout.list_item_survey_multichoice, viewGroup,
                        false);
                return new MultiChoiceSurveyViewHolder(itemView);
            case MyGoalsViewItem.TYPE_SURVEY_BINARY:
                itemView = inflater.inflate(R.layout.list_item_survey_binary, viewGroup, false);
                return new BinarySurveyViewHolder(itemView);
            case MyGoalsViewItem.TYPE_SURVEY_OPENENDED:
                itemView = inflater.inflate(R.layout.list_item_survey_openended, viewGroup, false);
                return new OpenEndedSurveyViewHolder(itemView);
            default:
                itemView = inflater.inflate(
                        R.layout.list_item_goal, viewGroup, false);

                return new MyGoalsViewHolder(itemView);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getType();
    }

}
