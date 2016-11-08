package org.tndata.android.compass.model;


/**
 * Model to represent a reminder that has been snoozed to a location.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class LocationReminder{
    //The id is not final because it's set after the reminder is written to the DB
    private long mId;

    private final long mPlaceId;
    private final String mGcmMessage;


    /**
     * Constructor.
     *
     * @param placeId the id of the place.
     * @param gcmMessage the original GCM message.
     */
    public LocationReminder(long placeId, String gcmMessage){
        this(-1, placeId, gcmMessage);
    }

    /**
     * Constructor.
     *
     * @param id the id of the reminder as stored in the database.
     * @param placeId the id of the place.
     * @param gcmMessage the original GCM message.
     */
    public LocationReminder(long id, long placeId, String gcmMessage){
        mId = id;
        mPlaceId = placeId;
        mGcmMessage = gcmMessage;
    }

    /**
     * Setter for the reminder's ID.
     *
     * @param id the id of the reminder as stored in the database.
     */
    public void setId(long id){
        mId = id;
    }

    /**
     * Getter for the reminder's ID
     *
     * @return the ID of the reminder in the local DB
     */
    public long getId(){
        return mId;
    }

    /**
     * Place ID getter.
     *
     * @return the id of the place the reminder is to be fired at.
     */
    public long getPlaceId(){
        return mPlaceId;
    }

    /**
     * GCM message getter.
     *
     * @return the original gcm message delivered by the server.
     */
    public String getGcmMessage(){
        return mGcmMessage;
    }
}
