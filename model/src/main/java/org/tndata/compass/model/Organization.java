package org.tndata.compass.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;


/**
 * Model for an organization.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class Organization extends TDCBase{
    public static final String TYPE = "organization";


    @SerializedName("name")
    private String mName;


    /**
     * Name getter.
     *
     * @return the name of this organization.
     */
    public String getName(){
        return mName;
    }

    @Override
    protected String getType(){
        return TYPE;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        super.writeToParcel(dest, flags);
        dest.writeString(mName);
    }

    public static final Creator<Organization> CREATOR = new Creator<Organization>(){
        @Override
        public Organization createFromParcel(Parcel src){
            return new Organization(src);
        }

        @Override
        public Organization[] newArray(int i){
            return new Organization[i];
        }
    };

    /**
     * Parcelable constructor.
     *
     * @param src the source parcel.
     */
    private Organization(Parcel src){
        super(src);
        mName = src.readString();
    }
}
