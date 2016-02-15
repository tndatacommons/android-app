package org.tndata.android.compass.model;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.util.Constants;

import java.util.List;


/**
 * Created by isma on 2/12/16.
 */
public class UserProfile extends TDCBase{
    public static final String TYPE = "user_profile";


    @SerializedName("bio")
    private List<SurveyResponse> mSurveyResponses;


    public List<SurveyResponse> getSurveyResponses(){
        return mSurveyResponses;
    }

    @Override
    protected String getType(){
        return TYPE;
    }

    public class SurveyResponse{
        @SerializedName("question_id")
        private long mQuestionId;
        @SerializedName("question_url")
        private String mQuestionUrl;
        @SerializedName("question_type")
        private String mQuestionType;
        @SerializedName("question_text")
        private String mQuestionText;

        @SerializedName("response_id")
        private long mResponseId;
        @SerializedName("response_url")
        private String mResponseUrl;
        //For binary, multiple choice and likert
        @SerializedName("selected_option")
        private long mSelectedOption;
        @SerializedName("selected_option_text")
        private String mSelectedOptionText;
        //For free text questions
        @SerializedName("question_input_type")
        private String mQuestionInputType;
        @SerializedName("response")
        private String mResponse;


        public long getQuestionId(){
            return mQuestionId;
        }

        public String getQuestionUrl(){
            return mQuestionUrl;
        }

        public String getQuestionType(){
            return mQuestionType;
        }

        public String getQuestionText(){
            return mQuestionText;
        }

        public long getResponseId(){
            return mResponseId;
        }

        public String getResponseUrl(){
            return mResponseUrl;
        }

        public void setSelectedOption(long selectedOption){
            mSelectedOption = selectedOption;
        }

        public long getSelectedOption(){
            return mSelectedOption;
        }

        public void setSelectedOptionText(String selectedOptionText){
            mSelectedOptionText = selectedOptionText;
        }

        public String getSelectedOptionText(){
            return mSelectedOptionText;
        }

        public String getQuestionInputType(){
            return mQuestionInputType;
        }

        public void setResponse(String response){
            mResponse = response;
        }

        public String getResponse(){
            return mResponse;
        }

        public boolean isOpenEnded(){
            return mQuestionType.equals(Constants.SURVEY_OPENENDED);
        }
    }
}
