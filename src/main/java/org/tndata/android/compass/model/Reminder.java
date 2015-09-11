package org.tndata.android.compass.model;

/**
 * Model class for a reminder.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class Reminder{
    private int mId;
    private int mPlaceId;
    private String mTitle;
    private String mMessage;
    private String mObjectId;
    private String mUserMappingId;


    public Reminder(int id, int placeId, String title, String message, String objectId, String userMappingId){
        mId = id;
        mPlaceId = placeId;
        mTitle = title;
        mMessage = message;
        mObjectId = objectId;
        mUserMappingId = userMappingId;
    }

    public int getId(){
        return mId;
    }

    public int getPlaceId(){
        return mPlaceId;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getMessage(){
        return mMessage;
    }

    public String getObjectId(){
        return mObjectId;
    }

    public String getUserMappingId(){
        return mUserMappingId;
    }
}
