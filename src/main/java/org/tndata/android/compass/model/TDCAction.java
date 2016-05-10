package org.tndata.android.compass.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;


/**
 * Model class for actions.
 *
 * @author Edited by Ismael Alonso
 * @version 1.0.0
 */
public class TDCAction extends TDCContent{
    public static final String TYPE = "action";


    @SerializedName("sequence_order")
    private int mSequenceOrder;
    @SerializedName("more_info")
    private String mMoreInfo;
    @SerializedName("html_more_info")
    private String mHtmlMoreInfo;
    @SerializedName("external_resource")
    private String mExternalResource;
    @SerializedName("external_resource_name")
    private String mExternalResourceName;
    @SerializedName("default_trigger")
    private Trigger mTrigger;

    @SerializedName("behavior")
    private long mBehaviorId;
    @SerializedName("behavior_title")
    private String mBehaviorTitle;


    /*---------*
     * GETTERS *
     *---------*/

    public int getSequenceOrder(){
        return mSequenceOrder;
    }

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

    public Trigger getTrigger(){
        return mTrigger;
    }

    public long getBehaviorId(){
        return mBehaviorId;
    }

    public String getBehaviorTitle(){
        return mBehaviorTitle == null ? "" : mBehaviorTitle;
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
        return "ActionContent #" + getId() + ": " + getTitle();
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        super.writeToParcel(dest, flags);
        dest.writeInt(mSequenceOrder);
        dest.writeString(mMoreInfo);
        dest.writeString(mHtmlMoreInfo);
        dest.writeString(mExternalResource);
        dest.writeString(mExternalResourceName);
        dest.writeByte((byte)(mTrigger != null ? 1 : 0));
        if (mTrigger != null){
            dest.writeParcelable(mTrigger, flags);
        }
        dest.writeLong(mBehaviorId);
        dest.writeString(mBehaviorTitle);
    }

    public static final Creator<TDCAction> CREATOR = new Creator<TDCAction>(){
        @Override
        public TDCAction createFromParcel(Parcel in){
            return new TDCAction(in);
        }

        @Override
        public TDCAction[] newArray(int size){
            return new TDCAction[size];
        }
    };

    /**
     * Constructor to create from parcel.
     *
     * @param src the parcel where the object is stored.
     */
    private TDCAction(Parcel src){
        super(src);
        mSequenceOrder = src.readInt();
        mMoreInfo = src.readString();
        mHtmlMoreInfo = src.readString();
        mExternalResource = src.readString();
        mExternalResourceName = src.readString();
        if (src.readByte() == 1){
            mTrigger = src.readParcelable(Trigger.class.getClassLoader());
        }
        mBehaviorId = src.readLong();
        mBehaviorTitle = src.readString();
    }
}
