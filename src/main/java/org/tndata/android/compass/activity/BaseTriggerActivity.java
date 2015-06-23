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

import java.util.Calendar;

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

    private static final String TAG = "BaseTriggerActivity";
    private static final String FRAG_TAG_RECUR_PICKER = "recurrencePickerDialogFragment";
    private static final String FRAG_TAG_DATE_PICKER = "datePickerDialogFragment";
    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";

    public void initializeReminders(Trigger trigger) {
        // initialize local vars with a given Trigger
        String time = trigger.getTime();
        String date = trigger.getDate();
        String rrule = trigger.getRRULE();
        initializeReminders(time, date, rrule);
    }

    public void initializeReminders(String time, String date, String rrule) {
        // initialize local vars with String time, date, and rrule data
        setDate(date);
        setTime(time);
        setRRULE(rrule);
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
    public void onTimeSet(RadialTimePickerDialog dialog, int hourOfDay, int minute) {
        Log.d(TAG, "hourOfDay: " + hourOfDay + ", minute: " + minute);
        mTime = String.format("%02d", hourOfDay) + ":" +
                String.format("%02d", minute);
        Toast.makeText(this,
                getString(R.string.time_picker_confirmation_toast, mTime),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        String date = String.format("%4d", year) + "-" +
                String.format("%02d", monthOfYear) + "-" +
                String.format("%02d", dayOfMonth);
        setDate(date);
        Toast.makeText(this,
                getText(R.string.date_picker_confirmation_toast),
                Toast.LENGTH_SHORT).show();
    }

    /*
    Use the selected Time/Date/Recurrence to update the given User's Action.
     */
    protected void saveActionTrigger(Action action) {
        // TODO: Assemble the Date/Time/RRULE information in the Trigger & Post to the api.
        String rrule = getRRULE();
        String date = getDate();
        String time = getTime();

        Log.d(TAG, "saveActionTrigger, for Action: " + action.getTitle());
        Log.d(TAG, "Time: " + time);
        Log.d(TAG, "Date: " + date);
        Log.d(TAG, "RRULE: " + rrule);

        // Time is required and one of date or rule is required
        if (action != null && !mTime.isEmpty() && (!mDate.isEmpty() || !mRrule.isEmpty())) {
            new AddActionTriggerTask(this,
                    ((CompassApplication) getApplication()).getToken(),
                    rrule, time, date, String.valueOf(action.getMappingId()))
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public boolean actionTriggerAdded(Action action) {
        // action is the updated Action, presumably with a Trigger attached.
        if(action != null) {
            Log.d(TAG, "Updated Action & Trigger: " + action.getCustomTrigger().getRecurrences());
            Toast.makeText(this,
                    getText(R.string.trigger_saved_confirmation_toast),
                    Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(this, getText(R.string.reminder_failed), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
