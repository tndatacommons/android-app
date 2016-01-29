package org.tndata.android.compass.model;

import org.tndata.android.compass.service.GcmIntentService;

import java.io.Serializable;


/**
 * Model class for a reminder. This is an app specific object to records received reminders
 * in a database. when snoozed to a location.
 *
 * @author Ismael Alonso
 * @version 1.1.0
 */
public class Reminder implements Serializable{
    static final long serialVersionUID = 94124918239L;

    public static final int TYPE_USER_ACTION_ID = 1;
    public static final int TYPE_CUSTOM_ACTION_ID = 2;

    private static final String TYPE_USER_ACTION = GcmIntentService.MESSAGE_TYPE_ACTION;
    private static final String TYPE_CUSTOM_ACTION = GcmIntentService.MESSAGE_TYPE_CUSTOM_ACTION;


    private int mId;
    private int mNotificationId;
    private int mPlaceId;
    private String mTitle;
    private String mMessage;
    private int mObjectId;
    private int mUserMappingId;
    private boolean mSnoozed;
    private long mLastDelivered;

    private final int mActionType;


    /**
     * Constructor for UserAction reminders.
     *
     * @param placeId the place id.
     * @param title the title of the reminder.
     * @param message the message of the reminder.
     * @param objectId the id of the action reminder.
     * @param userMappingId the mapping id of the action in the reminder.
     */
    public Reminder(int notificationId, int placeId, String title, String message, int objectId,
                    int userMappingId){
        mId = -1;
        mNotificationId = notificationId;
        mPlaceId = placeId;
        mTitle = title;
        mMessage = message;
        mObjectId = objectId;
        mUserMappingId = userMappingId;
        mSnoozed = true;

        mActionType = TYPE_USER_ACTION_ID;
    }

    /**
     * Constructor for CustomAction reminders.
     *
     * @param placeId the place id.
     * @param title the title of the reminder.
     * @param message the message of the reminder.
     * @param objectId the id of the action reminder.
     */
    public Reminder(int notificationId, int placeId, String title, String message, int objectId){
        mId = -1;
        mNotificationId = notificationId;
        mPlaceId = placeId;
        mTitle = title;
        mMessage = message;
        mObjectId = objectId;
        mUserMappingId = -1;
        mSnoozed = true;

        mActionType = TYPE_CUSTOM_ACTION_ID;
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
     * Notification id getter.
     *
     * @return the id of the notification.
     */
    public int getNotificationId(){
        return mNotificationId;
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
    public int getObjectId(){
        return mObjectId;
    }

    /**
     * Mapping id getter,
     *
     * @return the mapping id of the reminder.
     */
    public int getUserMappingId(){
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

    /**
     * Gets the object type id of the reminder.
     *
     * @return the object type id of the reminder.
     */
    public int getObjectTypeId(){
        return mActionType;
    }

    /**
     * Gets the object type of the reminder.
     *
     * @return the object type of the reminder.
     */
    public String getObjectType(){
        if (mActionType == TYPE_USER_ACTION_ID){
            return TYPE_USER_ACTION;
        }
        return TYPE_CUSTOM_ACTION;
    }
}
