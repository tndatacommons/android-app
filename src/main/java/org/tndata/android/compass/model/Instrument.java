package org.tndata.android.compass.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.parser.ParserModels;

import java.util.ArrayList;
import java.util.List;


/**
 * Model class for an instrument.
 *
 * @author Edited by Ismael Alonso
 * @version 1.0.0
 */
public class Instrument extends TDCBase implements Parcelable, ParserModels.ResultSet{
    public static final String TYPE = "instrument";


    @SerializedName("title")
    private String mTitle;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("instructions")
    private String mInstructions;
    @SerializedName("questions")
    private List<Survey> mQuestions;


    public Instrument(String title, String description, String instructions){
        mTitle = title;
        mDescription = description;
        mInstructions = instructions;
        mQuestions = new ArrayList<>();
    }

    @Override
    protected String getType(){
        return TYPE;
    }

    public String getTitle(){
        return mTitle != null ? mTitle : "";
    }

    public String getDescription(){
        return mDescription != null ? mDescription : "";
    }

    public String getInstructions(){
        return mInstructions != null ? mInstructions : "";
    }

    public List<Survey> getQuestions(){
        if (mQuestions == null){
            mQuestions = new ArrayList<>();
        }
        return mQuestions;
    }

    public int size(){
        return mQuestions.size();
    }

    public Survey get(int location){
        return mQuestions.get(location);
    }

    public void setTitle(String title){
        this.mTitle = title;
    }

    public void setDescription(String description){
        this.mDescription = description;
    }

    public void setInstructions(String instructions){
        this.mInstructions = instructions;
    }

    public void setQuestions(List<Survey> questions){
        this.mQuestions = questions;
    }

    public void addSurvey(Survey survey){
        if (mQuestions == null){
            mQuestions = new ArrayList<>();
        }
        mQuestions.add(survey);
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        super.addToParcel(dest, flags);
        dest.writeString(getTitle());
        dest.writeString(getDescription());
        dest.writeString(getInstructions());
        dest.writeTypedList(mQuestions);
    }

    public static final Creator<Instrument> CREATOR = new Creator<Instrument>(){
        @Override
        public Instrument createFromParcel(Parcel source){
            return new Instrument(source);
        }

        @Override
        public Instrument[] newArray(int size){
            return new Instrument[0];
        }
    };

    public Instrument(Parcel src){
        mTitle = src.readString();
        mDescription = src.readString();
        mInstructions = src.readString();
        mQuestions = new ArrayList<>();
        src.readTypedList(mQuestions, Survey.CREATOR);
    }
}
