package org.tndata.android.compass.model;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.parser.ParserModels;

import java.io.Serializable;


/**
 * Base class for all content. This class forces a type on every model type.
 *
 * @author Edited by Ismael Alonso
 * @version 1.0.0
 */
public abstract class TDCBase implements Serializable, ParserModels.ResultSet{
    private static final long serialVersionUID = 7554126338541297545L;


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
