package org.tndata.android.compass.model;

import java.io.Serializable;


/**
 * Model class for a reminder.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class Reminder implements Serializable{
    static final long serialVersionUID = 94124918239L;

    private int mId;
    private int mPlaceId;
    private String mTitle;
    private String mMessage;
    private String mObjectId;
    private String mUserMappingId;
    private boolean mSnoozed;
    private long mLastDelivered;


    /**
     * Constructor.
     *
     * @param placeId the place id.
     * @param title the title of the reminder.
     * @param message the message of the reminder.
     * @param objectId the id of the action reminder.
     * @param userMappingId the mapping id of the action in the reminder.
     */
    public Reminder(int placeId, String title, String message, String objectId, String userMappingId){
        mId = -1;
        mPlaceId = placeId;
        mTitle = title;
        mMessage = message;
        mObjectId = objectId;
        mUserMappingId = userMappingId;
        mSnoozed = true;
    }

    /**
     * Id setter.
     *
     * @param id the reminder id as given by the database.
     */
    public void setId(int id){
        mId = id;
    }

    /**
     * Place id setter.
     *
     * @param placeId the place id.
     */
    public void setPlaceId(int placeId){
        mPlaceId = placeId;
    }

    /**
     * Snoozed setter.
     *
     * @param snoozed true if this reminder has been snoozed.
     */
    public void setSnoozed(boolean snoozed){
        mSnoozed = snoozed;
    }

    /**
     * Last delivered setter.
     *
     * @param lastDelivered last time of delivery.
     */
    public void setLastDelivered(long lastDelivered){
        mLastDelivered = lastDelivered;
    }

    /**
     * Id getter,
     *
     * @return the id of the reminder.
     */
    public int getId(){
        return mId;
    }

    /**
     * Place id getter.
     *
     * @return the place id.
     */
    public int getPlaceId(){
        return mPlaceId;
    }

    /**
     * Title getter.
     *
     * @return the title of the reminder.
     */
    public String getTitle(){
        return mTitle;
    }

    /**
     * Message getter.
     *
     * @return the message of the reminder.
     */
    public String getMessage(){
        return mMessage;
    }

    /**
     * Object id getter.
     *
     * @return the object id of the reminder.
     */
    public String getObjectId(){
        return mObjectId;
    }

    /**
     * Mapping id getter,
     *
     * @return the mapping id of the reminder.
     */
    public String getUserMappingId(){
        return mUserMappingId;
    }

    /**
     * Snoozed getter.
     *
     * @return true if the reminder has been snoozed.
     */
    public boolean isSnoozed(){
        return mSnoozed;
    }

    /**
     * Last delivered getter.
     *
     * @return the last time the reminder was delivered.
     */
    public long getLastDelivered(){
        return mLastDelivered;
    }
}
