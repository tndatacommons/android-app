package org.tndata.android.compass.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


/**
 * Base class of all Tennessee Data Commons created content,
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public abstract class TDCContent implements Serializable, Comparable<TDCContent>{
    private static final long serialVersionUID = -7297141782846963404L;

    @SerializedName("id")
    private long mId;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("html_description")
    private String mHtmlDescription;
    @SerializedName("icon_url")
    private String mIconUrl;
    @SerializedName("editable")
    private boolean mEditable;


    /*---------*
     * SETTERS *
     *---------*/

    public void setId(long id){
        this.mId = id;
    }

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

    public void setEditable(boolean editable){
        this.mEditable = editable;
    }


    /*---------*
     * GETTERS *
     *---------*/

    public long getId() {
        return mId;
    }

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

    public boolean isEditable(){
        return mEditable;
    }

    protected abstract String getType();


    /*---------*
     * UTILITY *
     *---------*/

    @Override
    public boolean equals(Object object){
        boolean result = false;
        if (object == this){
            result = true;
        }
        else if (object != null && object instanceof TDCContent){
            if (getType().equals(((TDCContent)object).getType())){
                if (getId() == ((TDCContent)object).getId()){
                    result = true;
                }
            }
        }
        return result;
    }

    @Override
    public int hashCode(){
        return 21 + mTitle.hashCode();
    }

    @Override
    public int compareTo(@NonNull TDCContent another){
        return getTitle().compareTo(another.getTitle());
    }
}
