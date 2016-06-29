package org.tndata.android.compass.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


/**
 * Model class for a gcm message. It will eventually replace {@code Reminder}.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public final class GcmMessage implements Parcelable{
    //Message types
    private static final String TYPE_USER_ACTION = "action";
    private static final String TYPE_CUSTOM_ACTION = "customaction";
    private static final String TYPE_ENROLLMENT = "package enrollment";
    private static final String TYPE_CHECK_IN = "checkin";
    private static final String TYPE_AWARD = "award";


    //Common fields
    @SerializedName("id")
    private long mId;
    @SerializedName("title")
    private String mContentTitle;
    @SerializedName("message")
    private String mContentText;

    //Type
    @SerializedName("object_type")
    private String mObjectType;

    //New fields
    @SerializedName("action")
    private UserAction mUserAction;
    @SerializedName("custom_action")
    private CustomAction mCustomAction;
    @SerializedName("badge")
    private Badge mBadge;

    //Support for payloads bigger than 4092KB
    @SerializedName("object_id")
    private int mObjectId;
    @SerializedName("user_mapping_id")
    private int mUserMappingId;

    //Actual message (equivalent to reminder)
    private String mGcmMessage;


    public void setGcmMessage(String gcmMessage){
        mGcmMessage = gcmMessage;
    }

    public long getId(){
        return mId;
    }

    public String getContentTitle(){
        return mContentTitle;
    }

    public String getContentText(){
        return mContentText;
    }

    public boolean isUserActionMessage(){
        return mObjectType.equals(TYPE_USER_ACTION);
    }

    public boolean isCustomActionMessage(){
        return mObjectType.equals(TYPE_CUSTOM_ACTION);
    }

    public boolean isPackageEnrollmentMessage(){
        return mObjectType.equals(TYPE_ENROLLMENT);
    }

    public boolean isCheckInMessage(){
        return mObjectType.equals(TYPE_CHECK_IN);
    }

    public boolean isBadgeMessage(){
        return mObjectType.equals(TYPE_AWARD);
    }

    public UserAction getUserAction(){
        return mUserAction;
    }

    public CustomAction getCustomAction(){
        return mCustomAction;
    }

    public Badge getBadge(){
        return mBadge;
    }

    public int getObjectId(){
        return mObjectId;
    }

    public int getUserMappingId(){
        return mUserMappingId;
    }

    public String getGcmMessage(){
        return mGcmMessage;
    }


    /*------------*
     * PARCELABLE *
     *------------*/

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeLong(mId);
        dest.writeString(mContentTitle);
        dest.writeString(mContentText);
        dest.writeString(mObjectType);

        if (isUserActionMessage()){
            dest.writeParcelable(mUserAction, flags);
        }
        else if (isCustomActionMessage()){
            dest.writeParcelable(mCustomAction, flags);
        }
        else if (isBadgeMessage()){
            dest.writeParcelable(mBadge, flags);
        }

        dest.writeInt(mObjectId);
        dest.writeInt(mUserMappingId);
        dest.writeString(mGcmMessage);
    }

    public static final Creator<GcmMessage> CREATOR = new Creator<GcmMessage>(){
        @Override
        public GcmMessage createFromParcel(Parcel source){
            return new GcmMessage(source);
        }

        @Override
        public GcmMessage[] newArray(int size){
            return new GcmMessage[size];
        }
    };

    /**
     * Constructor. Creates a GcmMessage from a parcel.
     *
     * @param src the source parcel.
     */
    public GcmMessage(Parcel src){
        mId = src.readLong();
        mContentTitle = src.readString();
        mContentText = src.readString();
        mObjectType = src.readString();

        if (isUserActionMessage()){
            mUserAction = src.readParcelable(UserAction.class.getClassLoader());
        }
        else if (isCustomActionMessage()){
            mCustomAction = src.readParcelable(CustomAction.class.getClassLoader());
        }
        else if (isBadgeMessage()){
            mBadge = src.readParcelable(Badge.class.getClassLoader());
        }

        mObjectId = src.readInt();
        mUserMappingId = src.readInt();
        mGcmMessage = src.readString();
    }
}
