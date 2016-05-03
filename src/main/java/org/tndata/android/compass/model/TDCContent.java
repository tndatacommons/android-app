package org.tndata.android.compass.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;


/**
 * Base class of all Tennessee Data Commons created content,
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public abstract class TDCContent extends TDCBase implements Parcelable, Comparable<TDCContent>{
    @SerializedName("title")
    private String mTitle;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("html_description")
    private String mHtmlDescription;
    @SerializedName("icon_url")
    private String mIconUrl;


    public TDCContent(){

    }

    /*---------*
     * SETTERS *
     *---------*/

    public void setTitle(String title){
        this.mTitle = title;
    }

    public void setDescription(String description){
        this.mDescription = description;
    }

    public void setHTMLDescription(String htmlDescription){
        this.mHtmlDescription = htmlDescription;
    }

    public void setIconUrl(String iconUrl){
        this.mIconUrl = iconUrl;
    }


    /*---------*
     * GETTERS *
     *---------*/

    public String getTitle(){
        return mTitle;
    }

    public String getDescription(){
        return mDescription;
    }

    public String getHTMLDescription(){
        return mHtmlDescription;
    }

    public String getIconUrl(){
        return mIconUrl;
    }


    /*-------*
     * OTHER *
     *-------*/

    @Override
    public int compareTo(@NonNull TDCContent another){
        //By default, order by title in alphabetical order
        return getTitle().compareTo(another.getTitle());
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dst, int flags){
        super.addToParcel(dst, flags);
        dst.writeString(mTitle);
        dst.writeString(mDescription);
        dst.writeString(mHtmlDescription);
        dst.writeString(mIconUrl);
    }

    protected TDCContent(Parcel src){
        super(src);
        mTitle = src.readString();
        mDescription = src.readString();
        mHtmlDescription = src.readString();
        mIconUrl = src.readString();
    }
}
