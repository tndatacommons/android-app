package org.tndata.android.compass.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.parser.ParserModels;


/**
 * Base class for all content. This class forces an ID and a type on every model type.
 *
 * @author Edited by Ismael Alonso
 * @version 1.0.0
 */
public abstract class TDCBase implements Parcelable, ParserModels.ResultSet{
    @SerializedName("id")
    private long mId;


    /**
     * Explicit default constructor.
     */
    public TDCBase(){
        //The rationale of this constructor is to avoid errors based on the presence of
        //  the TDCBase(Parcel) constructor
    }

    public TDCBase(long id){
        mId = id;
    }


    /*---------*
     * SETTERS *
     *---------*/

    /**
     * Id setter.
     *
     * @param id the id of the object.
     */
    public void setId(long id){
        this.mId = id;
    }


    /*---------*
     * GETTERS *
     *---------*/

    /**
     * Id getter.
     *
     * @return the id of the object.
     */
    public long getId(){
        return mId;
    }

    /**
     * Getter for the object type as returned by the API.
     *
     * @return an object type.
     */
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
        else if (object != null && object instanceof TDCBase){
            TDCBase cast = (TDCBase)object;
            if (getType().equals(cast.getType())){
                if (getId() == cast.getId()){
                    result = true;
                }
            }
        }
        return result;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeLong(mId);
    }

    /**
     * Constructor to create an object from a parcel.
     *
     * @param src the source parcel.
     */
    protected TDCBase(Parcel src){
        mId = src.readLong();
    }
}
