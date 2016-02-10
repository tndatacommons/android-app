package org.tndata.android.compass.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class SurveyOption extends TDCBase implements Serializable{
    private static final long serialVersionUID = 7660016179070794886L;

    public static final String TYPE = "option";


    @SerializedName("text")
    private String mText;


    public void setText(String text){
        mText = text;
    }

    public String getText(){
        return mText;
    }

    @Override
    protected String getType(){
        return TYPE;
    }

    public String toString() {
        return mText;
    }
}
