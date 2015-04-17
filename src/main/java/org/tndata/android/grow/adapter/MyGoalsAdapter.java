package org.tndata.android.grow.adapter;

import java.util.List;

import org.tndata.android.grow.R;
import org.tndata.android.grow.model.Goal;
import org.tndata.android.grow.model.MyGoalsViewItem;
import org.tndata.android.grow.model.Survey;
import org.tndata.android.grow.model.SurveyOptions;
import org.tndata.android.grow.util.ImageCache;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class MyGoalsAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public interface OnClickEvent {
        void onClick(View v, int position);
    }

    public interface SurveyCompleteInterface {
        public void surveyCompleted(Survey survey);
    }

    private Context mContext;
    private List<MyGoalsViewItem> mItems;
    private OnClickEvent mOnClickEvent;
    private SurveyCompleteInterface mSurveyCompleteCallback;

    static class MyGoalsViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        ImageView iconImageView;

        public MyGoalsViewHolder(View view) {
            super(view);
            iconImageView = (ImageView) view
                    .findViewById(R.id.list_item_goal_imageview);
            titleTextView = (TextView) view
                    .findViewById(R.id.list_item_goal_title_textview);
            descriptionTextView = (TextView) view
                    .findViewById(R.id.list_item_goal_description_textview);
        }
    }

    static class LikertSurveyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView minTextView;
        TextView maxTextView;
        TextView choiceTextView;
        SeekBar seekBar;
        Button doneButton;

        public LikertSurveyViewHolder(View view) {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id
                    .list_item_survey_likert_title_textview);
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
        Spinner spinner;
        Button doneButton;

        public MultiChoiceSurveyViewHolder(View view) {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id
                    .list_item_survey_multi_title_textview);
            spinner = (Spinner) view.findViewById(R.id.list_item_survey_multi_spinner);
            doneButton = (Button) view.findViewById(R.id.list_item_survey_multi_done_button);
        }
    }

    static class BinarySurveyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        RadioButton negativeRadioButton;
        RadioButton positiveRadioButton;
        Button doneButton;

        public BinarySurveyViewHolder(View view) {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id
                    .list_item_survey_binary_title_textview);
            negativeRadioButton = (RadioButton) view.findViewById(R.id
                    .list_item_survey_binary_no_radiobutton);
            positiveRadioButton = (RadioButton) view.findViewById(R.id
                    .list_item_survey_binary_yes_radiobutton);
            doneButton = (Button) view.findViewById(R.id.list_item_survey_binary_done_button);
        }
    }

    public MyGoalsAdapter(Context context, List<MyGoalsViewItem> objects,
                          SurveyCompleteInterface callback) {
        if (objects == null) {
            throw new IllegalArgumentException("Goals List must not be null");
        }
        this.mItems = objects;
        this.mContext = context;
        this.mSurveyCompleteCallback = callback;
    }

    public void updateEntries(List<MyGoalsViewItem> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void setOnClickEvent(OnClickEvent onClickEvent) {
        mOnClickEvent = onClickEvent;
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
            case MyGoalsViewItem.TYPE_GOAL:
                final Goal goal = mItems.get(position).getGoal();
                if (goal.getIconUrl() != null && !goal.getIconUrl().isEmpty()) {
                    ImageCache.instance(mContext).loadBitmap(((MyGoalsViewHolder) viewHolder)
                                    .iconImageView,
                            goal.getIconUrl(), false);
                } else {
                    ((MyGoalsViewHolder) viewHolder).iconImageView.setImageResource(R.drawable
                            .default_image);
                }
                ((MyGoalsViewHolder) viewHolder).titleTextView.setText(goal.getTitle());
                ((MyGoalsViewHolder) viewHolder).descriptionTextView.setText(goal.getSubtitle());
                if (mOnClickEvent != null) {
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnClickEvent.onClick(v, position);
                        }
                    });
                }
                break;

            case MyGoalsViewItem.TYPE_SURVEY_LIKERT:
                ((LikertSurveyViewHolder) viewHolder).titleTextView.setText(survey.getText());
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
                        mSurveyCompleteCallback.surveyCompleted(survey);
                    }
                });
                break;
            case MyGoalsViewItem.TYPE_SURVEY_MULTICHOICE:
                ((MultiChoiceSurveyViewHolder) viewHolder).titleTextView.setText(survey.getText());
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
                        mSurveyCompleteCallback.surveyCompleted(survey);
                    }
                });
                break;
            case MyGoalsViewItem.TYPE_SURVEY_BINARY:
                ((BinarySurveyViewHolder) viewHolder).titleTextView.setText(survey.getText());
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
                        mSurveyCompleteCallback.surveyCompleted(survey);
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

            case MyGoalsViewItem.TYPE_GOAL:
                itemView = inflater.inflate(
                        R.layout.list_item_goal, viewGroup, false);

                return new MyGoalsViewHolder(itemView);
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
