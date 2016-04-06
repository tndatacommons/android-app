package org.tndata.android.compass.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;


public class SurveyOption extends TDCBase implements Parcelable{
    public static final String TYPE = "option";


    @SerializedName("text")
    private String mText;


    public SurveyOption(long id, @NonNull String text){
        super(id);
        mText = text;
    }

    public void setText(String text){
        mText = text;
    }

    public String getText(){
        return mText != null ? mText : "";
    }

    @Override
    protected String getType(){
        return TYPE;
    }

    public String toString() {
        return mText;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        super.addToParcel(dest, flags);
        dest.writeString(getText());
    }

    public static final Creator<SurveyOption> CREATOR = new Creator<SurveyOption>(){
        @Override
        public SurveyOption createFromParcel(Parcel source){
            return new SurveyOption(source);
        }

        @Override
        public SurveyOption[] newArray(int size){
            return new SurveyOption[size];
        }
    };

    private SurveyOption(Parcel src){
        super(src);
        mText = src.readString();
    }
}
