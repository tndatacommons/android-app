package org.tndata.android.compass.model;

import com.google.gson.annotations.SerializedName;


/**
 * Base class for the content. Use it for both, user and regular content and rewire
 * its methods in user content to the instance of regular content.
 *
 * @author Edited by Ismael Alonso
 * @version 1.0.0
 */
public abstract class TDCBase{
    @SerializedName("id")
    private long mId;


    /*---------*
     * SETTERS *
     *---------*/

    public void setId(long id){
        this.mId = id;
    }


    /*---------*
     * GETTERS *
     *---------*/

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
}
