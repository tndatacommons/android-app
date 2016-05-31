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
    @SerializedName("external_resource_type")
    private String mExternalResourceType;

    @SerializedName("behavior")
    private long mBehaviorId;
    @SerializedName("behavior_title")
    private String mBehaviorTitle;
    @SerializedName("behavior_description")
    private String mBehaviorDescription;


    /*---------*
     * GETTERS *
     *---------*/

    /**
     * Sequence order getter.
     *
     * @return the action's sequence order position.
     */
    public int getSequenceOrder(){
        return mSequenceOrder;
    }

    /**
     * More info getter.
     *
     * @return the action's more info.
     */
    public String getMoreInfo(){
        return mMoreInfo;
    }

    /**
     * HTML more info getter.
     *
     * @return the action's more info in HTML format.
     */
    public String getHTMLMoreInfo(){
        return mHtmlMoreInfo;
    }

    /**
     * External resource getter.
     *
     * @return the action's external resource.
     */
    public String getExternalResource(){
        return mExternalResource;
    }

    /**
     * External resource name getter.
     *
     * @return the action's external resource name.
     */
    public String getExternalResourceName(){
        return mExternalResourceName;
    }

    /**
     * External resource type getter.
     *
     * @return the action's external resource type.
     */
    public String getExternalResourceType(){
        return mExternalResourceType;
    }

    /**
     * Behavior id getter.
     *
     * @return the id of the action's parent behavior.
     */
    public long getBehaviorId(){
        return mBehaviorId;
    }

    /**
     * Behavior title getter.
     *
     * @return the title of the action's parent behavior.
     */
    public String getBehaviorTitle(){
        return mBehaviorTitle;
    }

    /**
     * Behavior description getter.
     *
     * @return the description of the action's parent behavior.
     */
    public String getBehaviorDescription(){
        return mBehaviorDescription;
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
    public void writeToParcel(Parcel dest, int flags){
        super.writeToParcel(dest, flags);
        dest.writeInt(mSequenceOrder);
        dest.writeString(mMoreInfo);
        dest.writeString(mHtmlMoreInfo);
        dest.writeString(mExternalResource);
        dest.writeString(mExternalResourceName);
        dest.writeString(mExternalResourceType);
        dest.writeLong(mBehaviorId);
        dest.writeString(mBehaviorTitle);
    }

    public boolean hasDatetimeResource() {
        return getExternalResourceType().equals("datetime");
    }

    public boolean hasPhoneNumberResource() {
        return getExternalResourceType().equals("phone");
    }

    public boolean hasLinkResource() {
        return getExternalResourceType().equals("link");
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
        mExternalResourceType = src.readString();
        mBehaviorId = src.readLong();
        mBehaviorTitle = src.readString();
    }
}
