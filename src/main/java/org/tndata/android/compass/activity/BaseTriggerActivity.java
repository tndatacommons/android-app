package org.tndata.android.compass.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence;
import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrenceFormatter;
import com.doomonafireball.betterpickers.recurrencepicker.RecurrencePickerDialog;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Trigger;
import org.tndata.android.compass.task.AddActionTriggerTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Created by kevin on 6/14/15.
 *
 * This is an Base Activity that provides methods for setting Triggers (reminder notifications).
 * Triggers can be be comprised of 3 parts:
 *
 * 1. A Time of day (when the notification should be sent)
 * 2. A Date (in the case of 1-time notifications).
 * 3. A Recurrence (an RFC2445 RRULE string) for repeating reminders.
 *
 * This Activity tracks those bits of information as well as an Action to update.
 *
 */
public class BaseTriggerActivity extends ActionBarActivity implements
        RecurrencePickerDialog.OnRecurrenceSetListener,
        RadialTimePickerDialog.OnTimeSetListener,
        CalendarDatePickerDialog.OnDateSetListener,
        AddActionTriggerTask.AddActionTriggerTaskListener {

    // An EventRecurrence instance gives us access to different representations
    // of the RRULE data.
    private EventRecurrence mEventRecurrence = new EventRecurrence();
    private String mRrule; // RFC 2445 RRULE string
    private String mTime;  // HH:mm
    private String mDate;  // YYYY-mm-dd formatted date string
    private boolean triggerSaved = false; // flag so we know when a trigger is saved.

    //Date object that stores both the selected time and date
    private Date mDateTime;

    //Selection flags
    private boolean mTimeSelected;
    private boolean mDateSelected;

    //Date and time formats
    private SimpleDateFormat mLocalTimeFormatter;
    private SimpleDateFormat mLocalDateFormatter;
    private SimpleDateFormat mUtcTimeFormatter;
    private SimpleDateFormat mUtcDateFormatter;

    //Datetime parsers
    private SimpleDateFormat mLocalTimeParser;
    private SimpleDateFormat mLocalDateTimeParser;
    private SimpleDateFormat mUtcDateTimeParser;

    private static final String TAG = "BaseTriggerActivity";
    private static final String FRAG_TAG_RECUR_PICKER = "recurrencePickerDialogFragment";
    private static final String FRAG_TAG_DATE_PICKER = "datePickerDialogFragment";
    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mDateTime = new Date();
        mTimeSelected = false;
        mDateSelected = false;

        mLocalTimeFormatter = new SimpleDateFormat("h:mm a", Locale.getDefault());
        mLocalDateFormatter = new SimpleDateFormat("MMM d y", Locale.getDefault());
        mUtcTimeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        mUtcTimeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        mUtcDateFormatter = new SimpleDateFormat("y-MM-d", Locale.getDefault());
        mUtcDateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        mLocalTimeParser = new SimpleDateFormat("HH:mm", Locale.getDefault());
        mLocalDateTimeParser = new SimpleDateFormat("y-MM-d HH:mm", Locale.getDefault());
        mUtcDateTimeParser = new SimpleDateFormat("y-MM-d HH:mm", Locale.getDefault());
        mUtcDateTimeParser.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public boolean isTriggerSaved() {
        return triggerSaved;
    }

    public void initializeReminders(Trigger trigger){
        // initialize local vars with a given Trigger
        if(trigger != null){
            try{
                String datetime = trigger.getDate();
                if (datetime.equals("")){
                    datetime = trigger.getTime();
                    if (!datetime.equals("")){
                        mTimeSelected = true;
                        if (trigger.isDefaultTrigger()){
                            mDateTime = mLocalTimeParser.parse(datetime);
                        }
                        else{
                            mDateTime = mUtcTimeFormatter.parse(datetime);
                        }
                    }
                }
                else{
                    mDateSelected = true;
                    mTimeSelected = true;

                    datetime += " " + trigger.getTime();
                    if (trigger.isDefaultTrigger()){
                        mDateTime = mLocalDateTimeParser.parse(datetime);
                    }
                    else{
                        mDateTime = mUtcDateTimeParser.parse(datetime);
                    }
                }
            }
            catch (ParseException px){
                px.printStackTrace();
            }

            Log.d("Local TIME", getLocalTime());
            Log.d("UTC TIME", getUtcTime());

            setRRULE(trigger.getRRULE());
        }
        else{
            setRRULE("");
        }
    }

    /**
     * Local date getter.
     *
     * @return a formatted String with the selected date in the local timezone.
     */
    public String getLocalDate(){
        if (!mDateSelected){
            return "";
        }
        return mLocalDateFormatter.format(mDateTime);
    }

    /**
     * UTC date getter.
     *
     * @return a formatted String with the selected date in UTC.
     */
    public String getUtcDate(){
        if (!mDateSelected){
            return "";
        }
        return mUtcDateFormatter.format(mDateTime);
    }

    /**
     * Local time getter.
     *
     * @return a formatted String with the selected time in the local timezone.
     */
    public String getLocalTime(){
        if (!mTimeSelected){
            return "";
        }
        return mLocalTimeFormatter.format(mDateTime);
    }

    /**
     * UTC time getter.
     *
     * @return a formatted string with the selected time in UTC.
     */
    public String getUtcTime(){
        if (!mTimeSelected){
            return "";
        }
        return mUtcTimeFormatter.format(mDateTime);
    }

    public String getDate() {
        if(mDate == null) {mDate = "";}
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getTime() {
        if(mTime == null) { mTime = "";}
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public void disableTrigger() {
        // To disable a Trigger, we simply set all of it's parts to empty strings
        setDate("");
        setTime("");
        setRRULE("");
        Log.d(TAG, "disableTrigger");
    }

    public EventRecurrence getEventRecurrence() {
        return mEventRecurrence;
    }

    /*
    Return a human-friendly string representation of the recurrence, e.g.
    "weekly, each Friday"
      */
    public String getFriendlyRecurrenceString() {
        return EventRecurrenceFormatter.getRepeatString(
                getApplicationContext(), getResources(), mEventRecurrence, true);
    }

    // Given an RRULE string, parse it and update the local eventRecurrence object.
    public void setEventRecurrence(String rrule) {
        mEventRecurrence.parse(rrule);
    }

    /*
    Ensure that our RRULE string is formatted property. For the API to accept
    it, it must start with an 'RRULE:' prefix.
     */
    public void setRRULE(String rrule) {
        if(rrule == null) {
            rrule = "";
        }
        if(!rrule.isEmpty() && !rrule.toUpperCase().startsWith("RRULE:")) {
            rrule = "RRULE:" + rrule;
        }
        mRrule = rrule;
    }

    public String getRRULE() {
        if(mRrule == null) { mRrule = ""; }
        if(!mRrule.isEmpty() && !mRrule.toUpperCase().startsWith("RRULE:")) {
            mRrule = "RRULE:" + mRrule.toUpperCase();
        }
        return mRrule;
    }

    protected void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog.newInstance(
                BaseTriggerActivity.this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(BaseTriggerActivity.this));

        timePickerDialog.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
    }

    protected void showDatePicker() {
        FragmentManager fm = getSupportFragmentManager();
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                .newInstance(BaseTriggerActivity.this, year, month, day);
        calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
    }

    protected void showRecurrencePicker(String rrule) {
        Bundle b = new Bundle();
        Time t = new Time();
        t.setToNow();

        b.putLong(RecurrencePickerDialog.BUNDLE_START_TIME_MILLIS, t.toMillis(false));
        b.putString(RecurrencePickerDialog.BUNDLE_TIME_ZONE, t.timezone);

        // may be more efficient to serialize and pass in EventRecurrence
        if(rrule == null) { rrule = ""; }
        b.putString(RecurrencePickerDialog.BUNDLE_RRULE, rrule);

        FragmentManager fm = getSupportFragmentManager();
        RecurrencePickerDialog rpd = (RecurrencePickerDialog) fm.findFragmentByTag(
                FRAG_TAG_RECUR_PICKER);
        if (rpd != null) {
            rpd.dismiss();
        }
        rpd = new RecurrencePickerDialog();
        rpd.setArguments(b);
        rpd.setOnRecurrenceSetListener(this);
        rpd.show(fm, FRAG_TAG_RECUR_PICKER);
    }


    @Override
    public void onRecurrenceSet(String rrule) {
        if(rrule != null) {
            setEventRecurrence(rrule);
        }

        // Our API needs the 'RRULE' prefix on the rrule string, but
        // BetterPicker's EventRecurrence should *not* have that: https://goo.gl/QhY9aC
        setRRULE(rrule);
        Toast.makeText(this,
                getText(R.string.recurrence_picker_confirmation_toast),
                Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onTimeSet(RadialTimePickerDialog dialog, int hourOfDay, int minute){
        //A calendar instance is retrieved and the picked time is set
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDateTime);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        //The datetime object is replaced with the new one
        mDateTime = calendar.getTime();
        mTimeSelected = true;

        Log.d(TAG, "utc: " + getUtcTime());
        Log.d(TAG, "local: " + getLocalTime());

        Toast.makeText(this,
                getString(R.string.time_picker_confirmation_toast, getLocalTime()),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth){
        //A calendar instance is retrieved and the picked date is set
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDateTime);
        calendar.set(year, monthOfYear, dayOfMonth);

        //The datetime object is replaced with the new one
        mDateTime = calendar.getTime();
        mDateSelected = true;

        Log.d(TAG, "utc: " + getUtcDate());
        Log.d(TAG, "local: " + getLocalDate());

        Toast.makeText(this,
                getText(R.string.date_picker_confirmation_toast),
                Toast.LENGTH_SHORT).show();
    }

    /*
    Use the selected Time/Date/Recurrence to update the given User's Action.
     */
    protected void saveActionTrigger(Action action) {
        String rrule = getRRULE();
        String date = getUtcDate();
        String time = getUtcTime();

        Log.d(TAG, "saveActionTrigger, for Action: " + action.getTitle());
        Log.d(TAG, "Time: " + time);
        Log.d(TAG, "Date: " + date);
        Log.d(TAG, "RRULE: " + rrule);

        // Time is required and one of date or rule is required
        if (action != null) {
            // TODO: Fails when time/date/rrule is empty or null
            new AddActionTriggerTask(this,
                    ((CompassApplication) getApplication()).getToken(),
                    rrule, time, date, String.valueOf(action.getMappingId()))
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        triggerSaved = true; // We want to know that we've attempted to save, even if saving fails.
    }

    public boolean actionTriggerAdded(Action action) {
        // action is the updated Action, presumably with a Trigger attached.
        if(action != null) {
            Log.d(TAG, "Updated Action: " + action.getTitle());
            Log.d(TAG, "Updated Trigger: " + action.getTrigger());
            Toast.makeText(this,
                    getText(R.string.trigger_saved_confirmation_toast),
                    Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Log.d(TAG, "actionTriggerAdded: received null Action");
            Toast.makeText(this, getText(R.string.reminder_failed), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
