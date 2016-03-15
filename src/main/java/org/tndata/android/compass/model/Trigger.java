package org.tndata.android.compass.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * An action trigger.
 *
 * //TODO yo, future Izzy, fix this mess. Seriously.
 *
 * @author Edited by Ismael Alonso
 * @version 2.0.0
 */
public class Trigger extends TDCBase implements Serializable, Comparable<Trigger>{
    private static final long serialVersionUID = 7914473023695112323L;

    public static final String TYPE = "trigger";


    // A default RRULE value for the recurrence picker (NOTE: no RRULE prefix)
    public static final String DEFAULT_RRULE = "FREQ=DAILY";

    private String recurrences_display = "";
    private String location = "";
    private String name = "";

    // NOTE: Any of these can be null
    /** {@link} http://www.ietf.org/rfc/rfc2445.txt */
    private String recurrences = "";
    private String time = "";
    private String trigger_date = "";

    @SerializedName("disabled")
    private boolean mDisabled;


    /**
     * Constructor.
     */
    public Trigger(){

    }

    /**
     * Recurrences display getter.
     *
     * @return the recurrences display string.
     */
    public String getRecurrencesDisplay(){
        if (recurrences_display == null){
            return "";
        }
        return recurrences_display;
    }

    /**
     * Recurrences display getter.
     *
     * @param recurrences_display the recurrences display string
     */
    public void setRecurrencesDisplay(String recurrences_display){
        this.recurrences_display = recurrences_display;
    }

    /**
     * Recurrences getter.
     *
     * @return the recurrences string.
     */
    public String getRecurrences(){
        if(recurrences == null){
            return "";
        }
        return recurrences;
    }

    /**
     * Recurrences setter.
     *
     * @param recurrences the recurrences.
     */
    public void setRecurrences(String recurrences){
        this.recurrences = recurrences;
    }

    /**
     * RRULE getter.
     *
     * @return the RRULE of the recurrences.
     */
    public String getRRULE() {
        if(recurrences == null) {return "";}
        // The RRULE data from the api (stored in `recurrences`) will contain a RRULE:
        // prefix. However, the betterPickers library doesn't like this, so this method
        // will return the RRULE data without that prefix.
        if(recurrences.startsWith("RRULE:")){
            return recurrences.substring(6);
        }
        return recurrences;
    }

    /**
     * Raw date setter.
     *
     * @param date raw trigger date
     */
    public void setRawDate(String date){
        this.trigger_date = date;
    }

    /**
     * Raw date getter.
     *
     * @return the date as a string in format "yyyy-MM-d" or an empty string if there is no date.
     */
    public String getRawDate(){
        if (trigger_date == null){
            return "";
        }
        return trigger_date;
    }

    /**
     * Raw time setter.
     *
     * @param time raw trigger time
     */
    public void setRawTime(String time){
        this.time = time;
    }

    /**
     * Raw time getter.
     *
     * @return the raw time as a string in format "H:mm:ss" or an empty string if there is no time
     */
    public String getRawTime(){
        if(time == null){
            return "";
        }
        return time;
    }

    /**
     * Name getter.
     *
     * @return the name of the trigger.
     */
    public String getName() {
        return name;
    }

    /**
     * Name setter.
     *
     * @param name the name of the trigger.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Location getter.
     *
     * @return the location.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Location setter.
     *
     * @param location the location.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    protected String getType(){
        return TYPE;
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
            if(!time.isEmpty()){
                DateFormat format = new SimpleDateFormat("H:mm", Locale.getDefault());
                date = format.parse(time.substring(0, 5));
            }
        }
        catch (ParseException px){
            px.printStackTrace();
        }
        return date;
    }

    /**
     * Returns a string with the time formatted in the default format or an empty
     * string if the trigger hs no time.
     *
     * @return the time as a string.
     */
    public String getFormattedTime(){
        return getFormattedTime(new SimpleDateFormat("h:mm a", Locale.getDefault()));
    }

    /**
     * Returns a string with the time formatted in the specified format or an empty
     * string if the trigger hs no time.
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
            if(!trigger_date.isEmpty()){
                DateFormat format = new SimpleDateFormat("yyyy-MM-d", Locale.getDefault());
                date = format.parse(trigger_date);
            }
        }
        catch (ParseException px){
            px.printStackTrace();
        }
        return date;
    }

    /**
     * Returns a string with the date formatted in the default format or an empty
     * string if the trigger hs no date.
     *
     * @return the date as a string.
     */
    public String getFormattedDate(){
        return getFormattedDate(new SimpleDateFormat("MMM d yyyy", Locale.getDefault()));
    }

    /**
     * Returns a string with the date formatted in the specified format or an empty
     * string if the trigger hs no date.
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
     * Tells whether the trigger is disabled or not.
     *
     * @return true if the trigger is disabled, false otherwise.
     */
    public boolean isDisabled(){
        //If the user disabled the trigger, then the Trigger object will exist
        //  with details like a name, but all of the time/trigger_date/recurrence
        //  information will be null (or empty)
        return mDisabled;
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
            return getTime().compareTo(another.getTime());
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
}
