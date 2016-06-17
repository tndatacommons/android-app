package org.tndata.android.compass.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.HashSet;
import java.util.Set;


/**
 * Model class for behaviors. Strictly speaking, we aren't using this class any more.
 *
 * @author Edited by Ismael Alonso
 * @version 1.0.0
 */
public class TDCBehavior extends TDCContent{
    public static final String TYPE = "behavior";


    @SerializedName("more_info")
    private String mMoreInfo = "";
    @SerializedName("html_more_info")
    private String mHtmlMoreInfo = "";
    @SerializedName("external_resource")
    private String mExternalResource = "";
    @SerializedName("external_resource_name")
    private String mExternalResourceName = "";

    @SerializedName("goals")
    private Set<Long> mGoalIdSet;
    @SerializedName("actions_count")
    private int mActionCount = 0;


    /*---------*
     * GETTERS *
     *---------*/

    public String getMoreInfo(){
        return mMoreInfo;
    }

    public String getHTMLMoreInfo(){
        return mHtmlMoreInfo;
    }

    public String getExternalResource(){
        return mExternalResource;
    }

    public String getExternalResourceName(){
        return mExternalResourceName;
    }

    public Set<Long> getGoalIdSet(){
        return mGoalIdSet;
    }

    public int getActionCount(){
        return mActionCount;
    }

    @Override
    protected String getType(){
        return TYPE;
    }


    /*---------*
     * UTILITY *
     *---------*/

    @Override
    public String toString(){
        return "BehaviorContent #" + getId() + ": " + getTitle();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        super.writeToParcel(dest, flags);
        dest.writeString(mMoreInfo);
        dest.writeString(mHtmlMoreInfo);
        dest.writeString(mExternalResource);
        dest.writeString(mExternalResourceName);
        dest.writeInt(mGoalIdSet.size());
        for (Long goalId:mGoalIdSet){
            dest.writeLong(goalId);
        }
        dest.writeInt(mActionCount);
    }

    public static final Creator<TDCBehavior> CREATOR = new Creator<TDCBehavior>(){
        @Override
        public TDCBehavior createFromParcel(Parcel source){
            return new TDCBehavior(source);
        }

        @Override
        public TDCBehavior[] newArray(int size){
            return new TDCBehavior[size];
        }
    };

    /**
     * Constructor to read from a parcel.
     *
     * @param src the source parcel.
     */
    private TDCBehavior(Parcel src){
        super(src);
        mMoreInfo = src.readString();
        mHtmlMoreInfo = src.readString();
        mExternalResource = src.readString();
        mExternalResourceName = src.readString();
        mGoalIdSet = new HashSet<>();
        for (int i = 0, length = src.readInt(); i < length; i++){
            mGoalIdSet.add(src.readLong());
        }
        mActionCount = src.readInt();
    }
}
