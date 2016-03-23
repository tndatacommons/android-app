package org.tndata.android.compass.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Model class for a trigger.
 *
 * @author Edited by Ismael Alonso
 * @version 2.0.0
 */
public class Trigger extends TDCBase implements Parcelable, Comparable<Trigger>{
    public static final String TYPE = "trigger";


    // A default RRULE value for the recurrence picker (NOTE: no RRULE prefix)
    public static final String DEFAULT_RRULE = "FREQ=DAILY";

    @SerializedName("name")
    private String mName;

    //Actual values, these can be null
    /** {@link} http://www.ietf.org/rfc/rfc2445.txt */
    @SerializedName("time")
    private String mTime;
    @SerializedName("trigger_date")
    private String mDate;
    @SerializedName("recurrences")
    private String mRecurrences;

    @SerializedName("recurrences_display")
    private String mRecurrencesDisplay;

    @SerializedName("disabled")
    private boolean mDisabled;


    /**
     * Constructor.
     */
    public Trigger(){

    }

    public Trigger(@NonNull String time, @NonNull String date, @NonNull String recurrences){
        mTime = time;
        mDate = date;
        mRecurrences = recurrences;

        mDisabled = false;
    }

    /**
     * Name getter.
     *
     * @return the name of the trigger.
     */
    public String getName() {
        return mName;
    }

    /**
     * Name setter.
     *
     * @param name the name of the trigger.
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * Raw time setter.
     *
     * @param time raw trigger time
     */
    public void setRawTime(String time){
        mTime = time;
    }

    /**
     * Raw time getter.
     *
     * @return the raw time as a string in format "H:mm:ss" or an empty string if there is no time
     */
    public String getRawTime(){
        if(mTime == null){
            return "";
        }
        return mTime;
    }

    /**
     * Raw date setter.
     *
     * @param date raw trigger date.
     */
    public void setRawDate(String date){
        mDate = date;
    }

    /**
     * Raw date getter.
     *
     * @return the date as a string in format "yyyy-MM-d" or an empty string if there is no date.
     */
    public String getRawDate(){
        if (mDate == null){
            return "";
        }
        return mDate;
    }

    /**
     * Recurrences setter.
     *
     * @param recurrences the recurrences.
     */
    public void setRecurrences(String recurrences){
        mRecurrences = recurrences;
    }

    /**
     * Recurrences getter.
     *
     * @return the mRecurrences string.
     */
    public String getRecurrences(){
        if(mRecurrences == null){
            return "";
        }
        return mRecurrences;
    }

    /**
     * Recurrences display setter.
     *
     * @param recurrencesDisplay the recurrences display string
     */
    public void setRecurrencesDisplay(String recurrencesDisplay){
        this.mRecurrencesDisplay = recurrencesDisplay;
    }

    /**
     * Recurrences display getter.
     *
     * @return the recurrences display string.
     */
    public String getRecurrencesDisplay(){
        if (mRecurrencesDisplay == null){
            return "";
        }
        return mRecurrencesDisplay;
    }

    /**
     * RRULE getter.
     *
     * @return the RRULE of the recurrences.
     */
    public String getRRULE() {
        if(mRecurrences == null) {return "";}
        //The RRULE data from the api (stored in `recurrences`) will contain a RRULE:
        //  prefix. However, the betterPickers library doesn't like this, so this method
        //  will return the RRULE data without that prefix.
        if(mRecurrences.startsWith("RRULE:")){
            return mRecurrences.substring(6);
        }
        return mRecurrences;
    }

    /**
     * Gets a Date object with the trigger time set. If the trigger has no time, the returned
     * object will contain the current time.
     *
     * @return the trigger time in a Date object.
     */
    public Date getTime(){
        Date date = new Date();
        try{
            if (!mTime.isEmpty()){
                DateFormat format = new SimpleDateFormat("H:mm", Locale.getDefault());
                date = format.parse(mTime.substring(0, 5));
            }
        }
        catch (ParseException px){
            px.printStackTrace();
        }
        return date;
    }

    /**
     * Returns a string with the time formatted in the default format (h:mm a) or an empty
     * string if the trigger hs no time.
     *
     * @return the time as a string.
     */
    public String getFormattedTime(){
        return getFormattedTime(new SimpleDateFormat("h:mm a", Locale.getDefault()));
    }

    /**
     * Returns a string with the time formatted in the specified format or an empty
     * string if the trigger has no time.
     *
     * @param format the format the time should be formatted in.
     * @return the time as a string.
     */
    public String getFormattedTime(@NonNull DateFormat format){
        String result = "";
        if (!getRawTime().isEmpty()){
            result = format.format(getTime());
        }
        return result;
    }

    /**
     * Gets a Date object with the trigger date set. If the trigger has no date, the returned
     * object will contain the current date.
     *
     * @return the trigger date in a Date object.
     */
    public Date getDate(){
        Date date = new Date();
        try{
            if(!mDate.isEmpty()){
                DateFormat format = new SimpleDateFormat("yyyy-MM-d", Locale.getDefault());
                date = format.parse(mDate);
            }
        }
        catch (ParseException px){
            px.printStackTrace();
        }
        return date;
    }

    /**
     * Returns a string with the date formatted in the default format (MMM d yyy) or an empty
     * string if the trigger hs no date.
     *
     * @return the date as a string.
     */
    public String getFormattedDate(){
        return getFormattedDate(new SimpleDateFormat("MMM d yyyy", Locale.getDefault()));
    }

    /**
     * Returns a string with the date formatted in the specified format or an empty
     * string if the trigger has no date.
     *
     * @param format the format the date should be formatted in.
     * @return the date as a string.
     */
    public String getFormattedDate(@NonNull DateFormat format){
        String result = "";
        if (!getRawDate().isEmpty()){
            result = format.format(getDate());
        }
        return result;
    }

    /**
     * Tells whether the trigger is enabled or not.
     *
     * @return true if the trigger is enabled, false otherwise.
     */
    public boolean isEnabled(){
        return !mDisabled;
    }

    /**
     * Enables or disables the trigger.
     *
     * @param enabled true if the trigger should be enabled, false otherwise.
     */
    public void setEnabled(boolean enabled){
        mDisabled = !enabled;
    }

    @Override
    public int compareTo(@NonNull Trigger another){
        if (getRawDate().isEmpty() && another.getRawDate().isEmpty()){
            if (getRawTime().isEmpty() && another.getRawTime().isEmpty()){
                return 0;
            }
            else if (getRawTime().isEmpty()){
                return 1;
            }
            else if (another.getRawTime().isEmpty()){
                return -1;
            }
            else{
                return getTime().compareTo(another.getTime());
            }
        }
        else if (getRawDate().isEmpty()){
            return 1;
        }
        else if (another.getRawDate().isEmpty()){
            return -1;
        }
        else{
            int date = getDate().compareTo(another.getDate());
            if (date == 0){
                return getTime().compareTo(another.getTime());
            }
            else{
                return date;
            }
        }
    }

    @Override
    protected String getType(){
        return TYPE;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeLong(getId());
        //The getters are used because the values stored may be null
        dest.writeString(getName());
        dest.writeString(getRawTime());
        dest.writeString(getRawDate());
        dest.writeString(getRecurrences());
        dest.writeString(getRecurrencesDisplay());
        dest.writeByte((byte)(mDisabled ? 1 : 0));
    }

    public static final Parcelable.Creator<Trigger> CREATOR = new Parcelable.Creator<Trigger>(){
        @Override
        public Trigger createFromParcel(Parcel in){
            return new Trigger(in);
        }

        @Override
        public Trigger[] newArray(int size){
            return new Trigger[size];
        }
    };

    /**
     * Constructor to create from parcel.
     *
     * @param in the parcel where the object is stored.
     */
    private Trigger(Parcel in){
        setId(in.readLong());
        mName = in.readString();
        mTime = in.readString();
        mDate = in.readString();
        mRecurrences = in.readString();
        mRecurrencesDisplay = in.readString();
        mDisabled = in.readByte() == 1;
    }
}
