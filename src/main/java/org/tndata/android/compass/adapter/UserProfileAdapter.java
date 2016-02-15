package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.UserProfile;

import java.util.List;


public class UserProfileAdapter extends BaseAdapter{
    private Context mContext;
    private List<UserProfile.SurveyResponse> mSurveyResponses;


    public UserProfileAdapter(@NonNull Context context, @NonNull List<UserProfile.SurveyResponse> surveyResponses){
        mContext = context;
        mSurveyResponses = surveyResponses;
    }

    @Override
    public int getCount(){
        return mSurveyResponses.size();
    }

    @Override
    public UserProfile.SurveyResponse getItem(int position){
        return mSurveyResponses.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Holder holder;
        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_user_profile_question, parent, false);

            holder = new Holder();
            holder.mQuestion = (TextView)convertView.findViewById(R.id.user_profile_question);
            holder.mResponse = (TextView)convertView.findViewById(R.id.user_profile_response);

            convertView.setTag(holder);
        }
        else{
            holder = (Holder)convertView.getTag();
        }

        UserProfile.SurveyResponse surveyResponse = getItem(position);

        holder.mQuestion.setText(surveyResponse.getQuestionText());
        if (surveyResponse.isOpenEnded()){
            holder.mResponse.setText(surveyResponse.getResponse());
        }
        else{
            holder.mResponse.setText(surveyResponse.getSelectedOptionText());
        }

        return convertView;
    }

    private static class Holder{
        TextView mQuestion;
        TextView mResponse;
    }
}
