package org.tndata.android.compass.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Survey extends TDCBase implements Parcelable, Comparable<Survey>{
    private static final String LIKERT = "likertquestion";
    private static final String MULTIPLE_CHOICE = "multiplechoicequestion";
    private static final String BINARY = "binaryquestion";
    private static final String OPEN_ENDED = "openendedquestion";

    private static final String TEXT_TYPE = "text";
    private static final String NUMBER_TYPE = "number";
    private static final String DATE_TYPE = "datetime";

    public static final String TYPE = "survey";


    @SerializedName("order")
    private int mOrder;
    @SerializedName("text")
    private String mQuestion;
    @SerializedName("available")
    private boolean mAvailable;
    @SerializedName("instructions")
    private String mInstructions;
    @SerializedName("question_type")
    private String mQuestionTypeStr;
    private QuestionType mQuestionType;
    @SerializedName("input_type")
    private String mInputTypeStr;
    private InputType mInputType;
    @SerializedName("response")
    private String mResponse;
    @SerializedName("options")
    private List<SurveyOption> mOptions;
    //The selected option is not API-provided
    private SurveyOption mSelectedOption;
    @SerializedName("response_url")
    private String mResponseUrl;


    public Survey(){

    }

    public void setOrder(int order){
        mOrder = order;
    }

    public void setQuestion(String question){
        mQuestion = question;
    }

    public void setAvailable(boolean available){
        mAvailable = available;
    }

    public void setInstructions(String instructions){
        mInstructions = instructions;
    }

    public void setQuestionType(QuestionType type){
        mQuestionTypeStr = type.get();
        mQuestionType = type;
    }

    public void setInputType(InputType type){
        mInputTypeStr = type.get();
        mInputType = type;
    }

    public void setResponse(String response){
        mResponse = response;
    }

    public void setOptions(List<SurveyOption> options){
        mOptions = options;
    }

    public void addOption(SurveyOption option){
        if (mOptions == null){
            mOptions = new ArrayList<>();
        }
        mOptions.add(option);
    }

    public void setSelectedOption(long selectedOptionId){
        if (mOptions != null){
            for (SurveyOption option:mOptions){
                if (option.getId() == selectedOptionId){
                    mSelectedOption = option;
                    break;
                }
            }
        }
    }

    public void setSelectedOption(SurveyOption selectedOption){
        if (mOptions.contains(selectedOption)){
            mSelectedOption = selectedOption;
        }
    }

    public void setResponseUrl(String responseUrl){
        this.mResponseUrl = responseUrl;
    }


    public int getOrder(){
        return mOrder;
    }

    public String getQuestion(){
        return mQuestion != null ? mQuestion : "";
    }

    public boolean isAvailable(){
        return mAvailable;
    }

    public String getInstructions(){
        return mInstructions != null ? mInstructions : "";
    }

    public QuestionType getQuestionType(){
        if (mQuestionType == null){
            switch (mQuestionTypeStr){
                case LIKERT:
                    mQuestionType = QuestionType.LIKERT;
                    break;

                case MULTIPLE_CHOICE:
                    mQuestionType = QuestionType.MULTIPLE_CHOICE;
                    break;

                case BINARY:
                    mQuestionType = QuestionType.BINARY;
                    break;

                case OPEN_ENDED:
                    mQuestionType = QuestionType.OPEN_ENDED;
                    break;

                default:
                    throw new IllegalStateException("The state of the survey is not allowed");
            }
        }
        return mQuestionType;
    }

    public InputType getInputType(){
        if (getQuestionType() != QuestionType.OPEN_ENDED){
            return null;
        }
        if (mInputType == null){
            switch (mInputTypeStr){
                case TEXT_TYPE:
                    mInputType = InputType.TEXT;
                    break;

                case NUMBER_TYPE:
                    mInputType = InputType.NUMBER;
                    break;

                case DATE_TYPE:
                    mInputType = InputType.DATE;
                    break;

                default:
                    throw new IllegalStateException("The state of the survey is not allowed.");
            }
        }
        return mInputType;
    }

    public String getResponse(){
        //For open ended
        return mResponse != null ? mResponse : "";
    }

    public List<SurveyOption> getOptions(){
        if (mOptions == null){
            mOptions = new ArrayList<>();
        }
        return mOptions;
    }

    public SurveyOption getSelectedOption(){
        return mSelectedOption;
    }

    public String getResponseUrl(){
        return mResponseUrl != null ? mResponseUrl : "";
    }

    @Override
    protected String getType(){
        return TYPE;
    }

    @Override
    public int compareTo(@NonNull Survey another){
        if (getId() == another.getId()){
            return 0;
        }
        else if (getId() < another.getId()){
            return -1;
        }
        else{
            return 1;
        }
    }

    @Override
    public String toString(){
        String result = getId() + " (";

        switch (getQuestionType().get()){
            case BINARY:
                result += "binary";
                break;

            case MULTIPLE_CHOICE:
                result += "multiple choice";
                break;

            case LIKERT:
                result += "likert";
                break;

            case OPEN_ENDED:
                result += "open ended: ";
                switch (getInputType().get()){
                    case TEXT_TYPE:
                        result += "text";
                        break;

                    case NUMBER_TYPE:
                        result += "number";
                        break;

                    case DATE_TYPE:
                        result += "date";
                        break;

                    default:
                        result += getInputType().get();
                }
                break;

            default:
                result += getQuestionType().get();
        }

        return result + "): " + mQuestion;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        super.addToParcel(dest, flags);
        dest.writeInt(mOrder);
        dest.writeString(getQuestion());
        dest.writeByte((byte)(mAvailable ? 1 : 0));
        dest.writeString(getInstructions());
        dest.writeString(getQuestionType().get());
        if (getQuestionType() == QuestionType.OPEN_ENDED){
            dest.writeString(getInputType().get());
        }
        dest.writeString(getResponse());
        dest.writeTypedList(getOptions());
        dest.writeByte((byte)(mSelectedOption == null ? 0 : 1));
        if (mSelectedOption != null){
            dest.writeParcelable(mSelectedOption, flags);
        }
        dest.writeString(getResponseUrl());
    }

    public static final Creator<Survey> CREATOR = new Creator<Survey>(){
        @Override
        public Survey createFromParcel(Parcel source){
            return null;
        }

        @Override
        public Survey[] newArray(int size){
            return new Survey[0];
        }
    };

    public Survey(Parcel src){
        super(src);
        mOrder = src.readInt();
        mQuestion = src.readString();
        mAvailable = src.readByte() == 1;
        mInstructions = src.readString();
        mQuestionTypeStr = src.readString();
        if (getQuestionType() == QuestionType.OPEN_ENDED){
            mInputTypeStr = src.readString();
            getInputType();
        }
        mResponse = src.readString();
        mOptions = new ArrayList<>();
        src.readTypedList(mOptions, SurveyOption.CREATOR);
        if (src.readByte() == 1){
            mSelectedOption = src.readParcelable(SurveyOption.class.getClassLoader());
        }
        mResponseUrl = src.readString();
    }


    public enum QuestionType{
        LIKERT(Survey.LIKERT),
        MULTIPLE_CHOICE(Survey.MULTIPLE_CHOICE),
        BINARY(Survey.BINARY),
        OPEN_ENDED(Survey.OPEN_ENDED);


        private final String mType;


        QuestionType(String type){
            mType = type;
        }

        public String get(){
            return mType;
        }
    }


    public enum InputType{
        TEXT(TEXT_TYPE),
        NUMBER(NUMBER_TYPE),
        DATE(DATE_TYPE);


        private final String mType;


        InputType(String type){
            mType = type;
        }

        public String get(){
            return mType;
        }
    }
}
