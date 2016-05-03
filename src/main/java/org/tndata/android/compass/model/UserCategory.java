package org.tndata.android.compass.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;


/**
 * Model class for user categories.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class UserCategory extends UserContent{
    public static final String TYPE = "usercategory";


    //API provided
    @SerializedName("category")
    private TDCCategory mCategory;


    /*---------*
     * SETTERS *
     *---------*/

    public void setCategory(TDCCategory category){
        this.mCategory = category;
    }


    /*---------*
     * GETTERS *
     *---------*/

    public TDCCategory getCategory(){
        return mCategory;
    }

    @Override
    public long getContentId(){
        return mCategory.getId();
    }

    public String getTitle(){
        return mCategory.getTitle();
    }

    public String getDescription(){
        return mCategory.getDescription();
    }

    public String getHTMLDescription(){
        return mCategory.getHTMLDescription();
    }

    public String getIconUrl(){
        return mCategory.getIconUrl();
    }

    public boolean isPackagedContent(){
        return mCategory.isPackagedContent();
    }

    public String getColor(){
        return mCategory.getColor();
    }

    @Override
    protected String getType(){
        return TYPE;
    }


    /*---------*
     * UTILITY *
     *---------*/

    @Override
    public void init(){

    }

    @Override
    public String toString(){
        return "UserCategory #" + getId() + " (" + mCategory.toString() + ")";
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        super.addToParcel(dest, flags);
        dest.writeParcelable(mCategory, flags);
    }

    public static final Creator<UserCategory> CREATOR = new Creator<UserCategory>(){
        @Override
        public UserCategory createFromParcel(Parcel source){
            return new UserCategory(source);
        }

        @Override
        public UserCategory[] newArray(int size){
            return new UserCategory[size];
        }
    };

    private UserCategory(Parcel src){
        super(src);
        mCategory = src.readParcelable(TDCCategory.class.getClassLoader());
    }
}
