package org.tndata.compass.model;

import android.graphics.Color;
import android.os.Parcel;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;


/**
 * Model class for categories.
 *
 * @author Edited by Ismael Alonso
 * @version 1.0.0
 */
public class TDCCategory extends TDCContent{
    public static final String TYPE = "category";


    @SerializedName("grouping")
    private int mGroup;
    @SerializedName("grouping_name")
    private String mGroupName;

    @SerializedName("order")
    private int mOrder = -1;
    @SerializedName("image_url")
    private String mImageUrl = "";
    @SerializedName("color")
    private String mColor = "";
    @SerializedName("secondary_color")
    private String mSecondaryColor = "";

    @SerializedName("packaged_content")
    private boolean mPackagedContent = false;

    @SerializedName("selected_by_default")
    private boolean mSelectedByDefault = false;


    public TDCCategory(){

    }


    /*-----------------------------------------------------------------------------------------*
     * SETTERS. NOTE: these should only be used by the table handler, categories are read only *
     *-----------------------------------------------------------------------------------------*/

    public void setGroup(int group){
        mGroup = group;
    }

    public void setGroupName(String groupName){
        mGroupName = groupName;
    }

    public void setOrder(int order){
        mOrder = order;
    }

    public void setImageUrl(String imageUrl){
        mImageUrl = imageUrl;
    }

    public void setColor(String color){
        mColor = color;
    }

    public void setSecondaryColor(String secondaryColor){
        mSecondaryColor = secondaryColor;
    }

    public void setPackagedContent(boolean isPackagedContent){
        mPackagedContent = isPackagedContent;
    }

    public void setSelectedByDefault(boolean isSelectedByDefault){
        mSelectedByDefault = isSelectedByDefault;
    }


    /*---------*
     * GETTERS *
     *---------*/

    public int getGroup(){
        return mGroup;
    }

    public String getGroupName(){
        return mGroupName;
    }

    public int getOrder(){
        return mOrder;
    }

    public boolean isFeatured(){
        return mGroup != -1;
    }

    public String getImageUrl(){
        return mImageUrl != null ? mImageUrl : "";
    }

    public String getColor(){
        return mColor != null ? mColor : "";
    }

    public int getColorInt(){
        return Color.parseColor(getColor());
    }

    public String getSecondaryColor(){
        return mSecondaryColor != null ? mSecondaryColor : "";
    }

    public boolean isPackagedContent(){
        return mPackagedContent;
    }

    public boolean isSelectedByDefault(){
        return mSelectedByDefault;
    }

    @Override
    protected String getType(){
        return TYPE;
    }


    /*---------*
     * UTILITY *
     *---------*/

    @Override
    public int compareTo(@NonNull TDCContent another){
        if (another instanceof TDCCategory){
            TDCCategory category = (TDCCategory)another;
            if (mGroup < category.getGroup()){
                if (mGroup == -1){
                    return 1;
                }
                return -1;
            }
            if (mGroup > category.getGroup()){
                if (category.getGroup() == -1){
                    return -1;
                }
                return 1;
            }
        }
        return super.compareTo(another);
    }

    @Override
    public String toString(){
        return "CategoryContent #" + getId() + ": " + getTitle() + " (" + getGroupName() + ")";
    }


    /*-------------------*
     * Parcelable stuffs *
     *-------------------*/

    @Override
    public void writeToParcel(Parcel dest, int flags){
        super.writeToParcel(dest, flags);
        dest.writeInt(mGroup);
        dest.writeString(mGroupName);
        dest.writeInt(mOrder);
        dest.writeString(getImageUrl());
        dest.writeString(getColor());
        dest.writeString(getSecondaryColor());
        dest.writeByte((byte)(mPackagedContent ? 1 : 0));
        dest.writeByte((byte)(mSelectedByDefault ? 1 : 0));
    }

    public static final Creator<TDCCategory> CREATOR = new Creator<TDCCategory>(){
        @Override
        public TDCCategory createFromParcel(Parcel source){
            return new TDCCategory(source);
        }

        @Override
        public TDCCategory[] newArray(int size){
            return new TDCCategory[size];
        }
    };

    private TDCCategory(Parcel src){
        super(src);
        mGroup = src.readInt();
        mGroupName = src.readString();
        mOrder = src.readInt();
        mImageUrl = src.readString();
        mColor = src.readString();
        mSecondaryColor = src.readString();
        mPackagedContent = src.readByte() == 1;
        mSelectedByDefault = src.readByte() == 1;
    }
}
