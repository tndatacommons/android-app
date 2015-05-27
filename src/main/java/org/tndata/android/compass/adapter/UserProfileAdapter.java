package org.tndata.android.compass.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Survey;
import org.tndata.android.compass.util.Constants;

import java.util.ArrayList;

public class UserProfileAdapter extends ArrayAdapter<Survey> {
    private ArrayList<Survey> mItems;
    private Context mContext = null;

    static class UserProfileViewHolder {
        TextView questionTextView;
        TextView responseTextView;
    }

    public UserProfileAdapter(Context context, int textViewResourceId,
                              ArrayList<Survey> items) {
        super(context, textViewResourceId, items);
        this.mItems = items;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserProfileViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_item_user_profile, null);
            viewHolder = new UserProfileViewHolder();
            viewHolder.questionTextView = (TextView) convertView.findViewById(R.id
                    .list_item_user_profile_question_textview);
            viewHolder.responseTextView = (TextView) convertView.findViewById(R.id
                    .list_item_user_profile_response_textview);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (UserProfileViewHolder) convertView.getTag();
        }
        final Survey survey = mItems.get(position);
        String question = survey.getText();
        String response = null;
        if (survey.getQuestionType().equals(Constants.SURVEY_OPENENDED)) {
            response = survey.getResponse();
        } else {
            if (survey.getSelectedOption() != null && survey.getSelectedOption().getText() !=
                    null) {
                response = survey.getSelectedOption().getText();
            }
        }

        if (question != null && !question.isEmpty()) {
            viewHolder.questionTextView.setText(question);
        }
        if (response != null && !response.isEmpty()) {
            viewHolder.responseTextView.setText(response);
        }

        return convertView;

    }
}
