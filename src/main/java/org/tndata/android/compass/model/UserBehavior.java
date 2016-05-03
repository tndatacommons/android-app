package org.tndata.android.compass.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.parser.ParserModels;


/**
 * Model class for user behaviors.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class UserBehavior extends UserContent implements ParserModels.ResultSet{
    public static final String TYPE = "userbehavior";


    //Values retrieved from the API
    @SerializedName("behavior")
    private TDCBehavior mBehavior;


    /*---------*
     * GETTERS *
     *---------*/

    public TDCBehavior getBehavior(){
        return mBehavior;
    }

    @Override
    public long getContentId(){
        return mBehavior.getId();
    }

    public String getTitle(){
        return mBehavior.getTitle();
    }

    public String getDescription(){
        return mBehavior.getDescription();
    }

    public String getHTMLDescription(){
        return mBehavior.getHTMLDescription();
    }

    public String getIconUrl(){
        return mBehavior.getIconUrl();
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
        return "UserBehavior #" + getId() + " (" + mBehavior.toString() + ")";
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        super.writeToParcel(dest, flags);
        dest.writeParcelable(mBehavior, flags);
    }

    public static final Creator<UserBehavior> CREATOR = new Creator<UserBehavior>(){
        @Override
        public UserBehavior createFromParcel(Parcel source){
            return new UserBehavior(source);
        }

        @Override
        public UserBehavior[] newArray(int size){
            return new UserBehavior[size];
        }
    };

    private UserBehavior(Parcel src){
        super(src);
        mBehavior = src.readParcelable(TDCBehavior.class.getClassLoader());
    }
}
