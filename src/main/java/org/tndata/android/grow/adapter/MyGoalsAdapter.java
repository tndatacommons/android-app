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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
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
            titleTextView = (TextView) view.findViewById(R.id.list_item_survey_likert_title_textview);
            minTextView = (TextView) view.findViewById(R.id.list_item_survey_likert_seekbar_min_textview);
            maxTextView = (TextView) view.findViewById(R.id.list_item_survey_likert_seekbar_max_textview);
            choiceTextView = (TextView) view.findViewById(R.id.list_item_survey_likert_choice_textview);
            seekBar = (SeekBar) view.findViewById(R.id.list_item_survey_likert_seekbar);
            doneButton = (Button) view.findViewById(R.id.list_item_survey_likert_done_button);
        }
    }

    public MyGoalsAdapter(Context context, List<MyGoalsViewItem> objects, SurveyCompleteInterface callback) {
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
        switch (viewHolder.getItemViewType()) {
            case MyGoalsViewItem.TYPE_GOAL:
                final Goal goal = mItems.get(position).getGoal();
                if (goal.getIconUrl() != null && !goal.getIconUrl().isEmpty()) {
                    ImageCache.instance(mContext).loadBitmap(((MyGoalsViewHolder) viewHolder).iconImageView,
                            goal.getIconUrl(), false);
                } else {
                    ((MyGoalsViewHolder) viewHolder).iconImageView.setImageResource(R.drawable.default_image);
                }
                ((MyGoalsViewHolder) viewHolder).titleTextView.setText(goal.getTitle());
                ((MyGoalsViewHolder) viewHolder).descriptionTextView.setText(goal.getSubtitle());
                if (mOnClickEvent != null)
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnClickEvent.onClick(v, position);
                        }
                    });
                break;

            case MyGoalsViewItem.TYPE_SURVEY_LIKERT:
                final Survey survey = mItems.get(position).getSurvey();
                ((LikertSurveyViewHolder) viewHolder).titleTextView.setText(survey.getText());
                ((LikertSurveyViewHolder) viewHolder).minTextView.setText(survey.getOptions().get(0).getText());
                ((LikertSurveyViewHolder) viewHolder).maxTextView.setText(survey.getOptions().get(survey.getOptions().size() - 1).getText());
                ((LikertSurveyViewHolder) viewHolder).seekBar.setMax(survey.getOptions().size() - 1);
                if (survey.getSelectedOption() != null) {
                    ((LikertSurveyViewHolder) viewHolder).seekBar.setProgress(survey.getSelectedOption().getId());
                    ((LikertSurveyViewHolder) viewHolder).choiceTextView.setText(survey.getSelectedOption().getText());
                    ((LikertSurveyViewHolder) viewHolder).doneButton.setEnabled(true);
                } else {
                    ((LikertSurveyViewHolder) viewHolder).seekBar.setProgress(0);
                    ((LikertSurveyViewHolder) viewHolder).doneButton.setEnabled(false);
                    ((LikertSurveyViewHolder) viewHolder).choiceTextView.setText("");
                }
                ((LikertSurveyViewHolder) viewHolder).seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        for (SurveyOptions option : survey.getOptions()) {
                            if ((option.getId()-1) == progress) {
                                survey.setSelectedOption(option);
                                ((LikertSurveyViewHolder) viewHolder).choiceTextView.setText(option.getText());
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
                ((LikertSurveyViewHolder) viewHolder).doneButton.setOnClickListener(new View.OnClickListener() {
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
            default:
                itemView = inflater.inflate(
                        R.layout.list_item_goal, viewGroup, false);

                return new MyGoalsViewHolder(itemView);
        }

    }

    @Override
    public int getItemViewType(int position) {
        final MyGoalsViewItem item = mItems.get(position);
        return item.getType();
    }

}
