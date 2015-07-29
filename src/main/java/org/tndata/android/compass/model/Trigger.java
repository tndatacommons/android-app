package org.tndata.android.compass.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * An action trigger.
 *
 * @author Edited by Ismael Alonso
 * @version 2.0.0
 */
public class Trigger implements Serializable, Comparable<Trigger>{
    private static final long serialVersionUID = 7914473023695112323L;

    public static long getSerialVersionUID(){
        return serialVersionUID;
    }


    // A default RRULE value for the recurrence picker (NOTE: no RRULE prefix)
    public static final String DEFAULT_RRULE = "FREQ=DAILY";

    private int id = -1;
    private String recurrences_display = "";
    private String name_slug = "";
    private String location = "";
    private String name = "";

    // NOTE: Any of these can be null
    /** {@link} http://www.ietf.org/rfc/rfc2445.txt */
    private String recurrences = "";
    private String time = "";
    private String trigger_date = "";


    /**
     * Constructor.
     */
    public Trigger(){

    }

    /**
     * Id getter.
     *
     * @return the id.
     */
    public int getId(){
        return id;
    }

    /**
     * Id setter.
     *
     * @param id the id.
     */
    public void setId(int id){
        this.id = id;
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
    public void setRecurrences(String recurrences) {
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
     * Name slug getter.
     *
     * @return the name slug.
     */
    public String getNameSlug() {
        return name_slug;
    }

    /**
     * Name slug setter.
     *
     * @param name_slug the name slug.
     */
    public void setNameSlug(String name_slug) {
        this.name_slug = name_slug;
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
    public boolean equals(Object object){
        if (object == null){
            return false;
        }
        else if (object == this){
            return true;
        }
        else if (object instanceof Trigger){
            return (this.getId() == ((Trigger)object).getId());
        }
        return false;
    }

    @Override
    public int hashCode(){
        int hash = 3;
        hash = 7*hash + this.getName().hashCode();
        return hash;
    }

    @Override
    public int compareTo(@NonNull Trigger another){
        if (getId() == another.getId()){
            return 0;
        }
        else if (getId() < another.getId()){
            return -1;
        }
        else{
            return 1;
        }
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
        return getFormattedTime(new SimpleDateFormat("MMM d yyyy", Locale.getDefault()));
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
        return name != null && !getName().isEmpty() && getRawDate().isEmpty() &&
                getRawTime().isEmpty() && getRecurrences().isEmpty();
    }
}
