package org.tndata.android.compass.model;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Model class for a reminder. This is an app specific object to records received reminders
 * in a database. when snoozed to a location.
 *
 * @author Ismael Alonso
 * @version 1.1.0
 */
public class Reminder implements Parcelable{
    private int mId;
    private int mNotificationId;
    private int mPlaceId;
    private String mTitle;
    private String mMessage;
    private int mObjectId;
    private int mUserMappingId;
    private boolean mSnoozed;
    private long mLastDelivered;


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
     * Tells whether the reminder is that of a UserAction
     *
     * @return true if this reminder is that of a UserAction, false otherwise.
     */
    public boolean isUserAction(){
        return mUserMappingId != -1;
    }

    /**
     * Tells whether the reminder is that of a CustomAction
     *
     * @return true if this reminder is that of a CustomAction, false otherwise.
     */
    public boolean isCustomAction(){
        return mUserMappingId == -1;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeInt(mId);
        dest.writeInt(mNotificationId);
        dest.writeInt(mPlaceId);
        dest.writeString(mTitle);
        dest.writeString(mMessage);
        dest.writeInt(mObjectId);
        dest.writeInt(mUserMappingId);
        dest.writeByte((byte)(mSnoozed ? 1 : 0));
        dest.writeLong(mLastDelivered);
    }

    public static final Parcelable.Creator<Reminder> CREATOR = new Parcelable.Creator<Reminder>(){
        @Override
        public Reminder createFromParcel(Parcel in){
            return new Reminder(in);
        }

        @Override
        public Reminder[] newArray(int size){
            return new Reminder[size];
        }
    };

    /**
     * Constructor to create from parcel.
     *
     * @param in the parcel where the object is stored.
     */
    private Reminder(Parcel in){
        mId = in.readInt();
        mNotificationId = in.readInt();
        mPlaceId = in.readInt();
        mTitle = in.readString();
        mMessage = in.readString();
        mObjectId = in.readInt();
        mUserMappingId = in.readInt();
        mSnoozed = in.readByte() == 1;
        mLastDelivered = in.readLong();
    }
}
