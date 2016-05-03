package org.tndata.android.compass.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


/**
 * Superclass of all classes that represent UserContent. The rationale behind this class
 * is that in order for the MapDeserializer to work as expected we need a generic, direct
 * way to retrieve the ID of the piece of content embedded into the UserContent object.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public abstract class UserContent extends TDCBase implements Serializable{
    private static final long serialVersionUID = -8654445236984566619L;


    @SerializedName("editable")
    private boolean mEditable;


    protected UserContent(){

    }

    /**
     * Gets the id of the actual piece of content for selected content or the id of the
     * object for custom content. In custom content, getContentId() === getId().
     *
     * @return an ID as described above.
     */
    public abstract long getContentId();

    /**
     * Method used to initialize the content's inner Lists.
     */
    public abstract void init();

    public void setEditable(boolean editable){
        this.mEditable = editable;
    }

    public boolean isEditable(){
        return mEditable;
    }

    protected void addToParcel(Parcel dest, int flags){
        super.addToParcel(dest, flags);
        dest.writeByte((byte)(mEditable ? 1 : 0));
    }

    protected UserContent(Parcel src){
        super(src);
        mEditable = src.readByte() == 1;
    }
}
