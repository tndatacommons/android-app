package org.tndata.android.compass.model;

import android.os.Parcel;
import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.util.ImageLoader;


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
    @SerializedName("featured")
    private boolean mFeatured;
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
        return mFeatured;
    }

    public String getImageUrl(){
        return mImageUrl != null ? mImageUrl : "";
    }

    public String getColor(){
        return mColor != null ? mColor : "";
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

    public void loadImageIntoView(ImageView imageView){
        String url = getImageUrl();
        if (!url.isEmpty()){
            ImageLoader.Options options = new ImageLoader.Options()
                    .setUsePlaceholder(false)
                    .setCropToCircle(true);
            ImageLoader.loadBitmap(imageView, url, options);
        }
    }

    @Override
    public String toString(){
        return "CategoryContent #" + getId() + ": " + getTitle();
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
        dest.writeByte((byte)(mFeatured ? 1 : 0));
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
        mFeatured = src.readByte() == 1;
        mImageUrl = src.readString();
        mColor = src.readString();
        mSecondaryColor = src.readString();
        mPackagedContent = src.readByte() == 1;
        mSelectedByDefault = src.readByte() == 1;
    }
}
