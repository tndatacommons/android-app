package org.tndata.android.compass.model;

import com.google.gson.annotations.SerializedName;


/**
 * Model class for a gcm message. It will eventually replace {@code Reminder}.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public final class GcmMessage{
    //Message types
    private static final String TYPE_ACTION = "action";
    private static final String TYPE_CUSTOM_ACTION = "customaction";
    private static final String TYPE_ENROLLMENT = "package enrollment";
    private static final String TYPE_CHECK_IN = "checkin";
    private static final String TYPE_BADGE = "award";


    //Common fields
    @SerializedName("id")
    private long mId;
    @SerializedName("title")
    private String mContentTitle;
    @SerializedName("message")
    private String mContentText;

    //Backwards support, these are to be removed by the beginning of July
    @SerializedName("object_id")
    private int mObjectId;
    @SerializedName("user_mapping_id")
    private int mUserMappingId;

    //New fields
    @SerializedName("badge")
    private Badge mBadge;

    //Type
    @SerializedName("object_type")
    private String mObjectType;


    public long getId(){
        return mId;
    }

    public String getContentTitle(){
        return mContentTitle;
    }

    public String getContentText(){
        return mContentText;
    }

    public int getObjectId(){
        return mObjectId;
    }

    public int getUserMappingId(){
        return mUserMappingId;
    }

    public Badge getBadge(){
        return mBadge;
    }

    public boolean isActionMessage(){
        return mObjectType.equals(TYPE_ACTION);
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
        return mObjectType.equals(TYPE_BADGE);
    }
}
