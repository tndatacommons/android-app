package org.tndata.compass.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;


/**
 * Model class for a badge.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class Badge extends TDCBase{
    public static final String TYPE = "badge";


    @SerializedName("name")
    private String mName;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("image")
    private String mImageUrl;
    @SerializedName("users_count")
    private int mUserCount;


    public Badge(String name, String description, String imageUrl){
        mName = name;
        mDescription = description;
        mImageUrl = imageUrl;
    }


    /*---------*
     * GETTERS *
     *---------*/

    public String getName(){
        return mName;
    }

    public String getUpperCaseName(){
        return mName.toUpperCase();
    }

    public String getDescription(){
        return mDescription;
    }

    public String getImageUrl(){
        return mImageUrl;
    }

    public int getUserCount(){
        return mUserCount;
    }

    @Override
    protected String getType(){
        return TYPE;
    }

    @Override
    public String toString(){
        return "Badge #" + getId() + ", " + mName + ": " + mDescription;
    }

    /*------------*
     * PARCELABLE *
     *------------*/

    @Override
    public void writeToParcel(Parcel dest, int flags){
        super.writeToParcel(dest, flags);
        dest.writeString(mName);
        dest.writeString(mDescription);
        dest.writeString(mImageUrl);
        dest.writeInt(mUserCount);
    }

    public static final Creator<Badge> CREATOR = new Creator<Badge>(){
        @Override
        public Badge createFromParcel(Parcel source){
            return new Badge(source);
        }

        @Override
        public Badge[] newArray(int size){
            return new Badge[size];
        }
    };

    private Badge(Parcel src){
        super(src);
        mName = src.readString();
        mDescription = src.readString();
        mImageUrl = src.readString();
        mUserCount = src.readInt();
    }
}
