package org.tndata.android.compass.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


/**
 * A model representation of a place.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class Place implements Parcelable{
    @SerializedName("name")
    private String mName;

    @SerializedName("primary")
    private boolean mPrimary;
    private boolean mSet;


    public Place(String name){
        mName = name;
        mPrimary = false;
        mSet = false;
    }

    public void setName(String name){
        mName = name;
    }

    public void setPrimary(boolean primary){
        mPrimary = primary;
    }

    public void setSet(boolean set){
        mSet = set;
    }

    public String getName(){
        return mName;
    }

    public boolean isPrimary(){
        return mPrimary;
    }

    public boolean isSet(){
        return mSet;
    }

    @Override
    public String toString(){
        return mName;
    }

    @Override
    public boolean equals(Object o){
        return o instanceof Place && ((Place)o).getName().equals(mName);
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(mName);
        dest.writeByte((byte)(mPrimary ? 1 : 0));
        dest.writeByte((byte)(mSet ? 1 : 0));
    }

    public static final Creator<Place> CREATOR = new Creator<Place>(){
        @Override
        public Place createFromParcel(Parcel source){
            return new Place(source);
        }

        @Override
        public Place[] newArray(int size){
            return new Place[size];
        }
    };

    /**
     * Constructor to create from parcel.
     *
     * @param src the source parcel.
     */
    private Place(Parcel src){
        mName = src.readString();
        mPrimary = src.readByte() == 1;
        mSet = src.readByte() == 1;
    }
}
