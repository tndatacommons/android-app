package org.tndata.android.compass.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence;
import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrenceFormatter;
import com.doomonafireball.betterpickers.recurrencepicker.RecurrencePickerDialog;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.fragment.TriggerFragment;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.CustomAction;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.Trigger;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.NetworkRequest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * This is an Base Activity that provides methods for setting Triggers (reminder notifications).
 * Triggers can be be comprised of 3 parts:
 *
 * 1. A Time of day (when the notification should be sent)
 * 2. A Date (in the case of 1-time notifications).
 * 3. A Recurrence (an RFC2445 RRULE string) for repeating reminders.
 *
 * This Activity tracks those bits of information as well as an Action to update.
 *
 * @author Kevin Zetterstrom
 * @author Edited by Ismael Alonso
 * @version 2.0.0
 */
public class TriggerActivity
        extends AppCompatActivity
        implements
                NetworkRequest.RequestCallback,
                Parser.ParserCallback,
                RecurrencePickerDialog.OnRecurrenceSetListener,
                RadialTimePickerDialog.OnTimeSetListener,
                CalendarDatePickerDialog.OnDateSetListener,
                TriggerFragment.TriggerFragmentListener{

    //NOTE: These need to be user content. Once an action is added, the trigger can be
    //  edited. The trigger needs to be attached to an action when it is added
    public static final String ACTION_KEY = "org.tndata.compass.TriggerActivity.Action";
    public static final String GOAL_KEY = "org.tndata.compass.TriggerActivity.Goal";

    private static final String TAG = "TriggerActivity";
    private static final String FRAG_TAG_RECUR_PICKER = "recurrencePickerDialogFragment";
    private static final String FRAG_TAG_DATE_PICKER = "datePickerDialogFragment";
    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";


    private TriggerFragment fragment;

    private Action mAction;

    // An EventRecurrence instance gives us access to different representations
    // of the RRULE data.
    private EventRecurrence mEventRecurrence = new EventRecurrence();
    private String mRrule; // RFC 2445 RRULE string
    private boolean savingTrigger = false; // flag so we know when a trigger is saved.

    //Date object that stores both the selected time and date
    private Date mDateTime;

    //Selection flags
    private boolean mTimeSelected;
    private boolean mDateSelected;

    //Datetime parsers
    private DateFormat mDisplayTimeFormat;
    private DateFormat mDisplayDateFormat;
    private DateFormat mApiTimeFormat;
    private DateFormat mApiDateFormat;
    private DateFormat mApiDateTimeFormat;

    //Request codes
    private int mPutTriggerRequestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mDateTime = new Date();
        mTimeSelected = false;
        mDateSelected = false;

        mDisplayTimeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        mDisplayDateFormat = new SimpleDateFormat("MMM d yyyy", Locale.getDefault());
        mApiTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        mApiDateFormat = new SimpleDateFormat("yyyy-MM-d", Locale.getDefault());
        mApiDateTimeFormat = new SimpleDateFormat("y-MM-d HH:mm", Locale.getDefault());

        setContentView(R.layout.activity_base_toolbar);

        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.getBackground().setAlpha(255);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        UserData userData = ((CompassApplication)getApplication()).getUserData();

        //Retrieve the goal and the original user action
        Goal goal = (Goal)getIntent().getSerializableExtra(GOAL_KEY);
        mAction = userData.getAction((Action)getIntent().getSerializableExtra(ACTION_KEY));

        //TODO this is another workaround
        if (goal != null){
            getSupportActionBar().setTitle(goal.getTitle());
        }
        setAction();
    }

    /**
     * Calls the initialisation routine and brings in the TriggerFragment.
     */
    private void setAction(){
        initializeReminders(mAction.getTrigger());

        fragment = TriggerFragment.newInstance(mAction);
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.base_content, fragment)
                    .commit();
        }
    }

    /**
     * Populates the activity with the provided trigger.
     *
     * @param trigger the trigger from where the data is to be extracted.
     */
    public void initializeReminders(@NonNull Trigger trigger){
        //Initialize the Date object
        try{
            if (trigger.getRawDate().equals("")){
                if (!trigger.getRawTime().equals("")){
                    mTimeSelected = true;
                    mDateTime = trigger.getTime();
                }
            }
            else{
                mDateSelected = true;
                if (!trigger.getRawTime().equals("")){
                    mTimeSelected = true;
                    String dateTime = trigger.getRawDate() + " " + trigger.getRawTime();
                    mDateTime = mApiDateTimeFormat.parse(dateTime);
                }
                else{
                    mDateTime = trigger.getDate();
                }
            }
        }
        catch (ParseException px){
            px.printStackTrace();
        }

        setRRULE(trigger.getRRULE());
    }

    /**
     * Date getter.
     *
     * @return the date in display format.
     */
    private String getDisplayDate(){
        if (!mDateSelected){
            return "";
        }
        return mDisplayDateFormat.format(mDateTime);
    }

    /**
     * Time getter.
     *
     * @return the time in display format.
     */
    private String getDisplayTime(){
        if (!mTimeSelected){
            return "";
        }
        return mDisplayTimeFormat.format(mDateTime);
    }

    /**
     * Date getter.
     *
     * @return the date in API format.
     */
    private String getApiDate(){
        if (!mDateSelected){
            return "";
        }
        return mApiDateFormat.format(mDateTime);
    }

    /**
     * Time getter.
     *
     * @return the time in API format.
     */
    private String getApiTime(){
        if (!mTimeSelected){
            return "";
        }
        return mApiTimeFormat.format(mDateTime);
    }

    /**
     * Return a human-friendly string representation of the recurrence, e.g. "weekly, each Friday"
     *
     * @return The recurrence in human readable form.
     */
    public String getFriendlyRecurrenceString(){
        return EventRecurrenceFormatter.getRepeatString(
                getApplicationContext(), getResources(), mEventRecurrence, true);
    }

    /**
     * Given an RRULE string, parse it and update the local eventRecurrence object.
     *
     * @param rrule the rrule to be parsed.
     */
    public void setEventRecurrence(String rrule) {
        mEventRecurrence.parse(rrule);
    }

    /**
     * Ensure that our RRULE string is formatted property. For the API to accept it, it
     * must start with an 'RRULE:' prefix.
     *
     * @param rrule the rrule to be formatted
     */
    public void setRRULE(String rrule){
        if (rrule == null){
            rrule = "";
        }

        if (!rrule.isEmpty() && !rrule.toUpperCase().startsWith("RRULE:")){
            rrule = "RRULE:" + rrule;
        }
        mRrule = rrule;
    }

    @Override
    public void onBackPressed(){
        if (!savingTrigger){
            onSaveTrigger();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                if (!savingTrigger){
                    onSaveTrigger();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFireTimePicker(){
        Calendar calendar = Calendar.getInstance();
        RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog.newInstance(
                TriggerActivity.this, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), android.text.format.DateFormat.is24HourFormat(this));

        timePickerDialog.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
    }

    @Override
    public void onFireDatePicker(){
        FragmentManager fm = getSupportFragmentManager();
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                .newInstance(TriggerActivity.this, year, month, day);
        calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
    }

    @Override
    public void onFireRecurrencePicker(String rrule){
        Bundle b = new Bundle();
        Time t = new Time();
        t.setToNow();

        b.putLong(RecurrencePickerDialog.BUNDLE_START_TIME_MILLIS, t.toMillis(false));
        b.putString(RecurrencePickerDialog.BUNDLE_TIME_ZONE, t.timezone);

        // may be more efficient to serialize and pass in EventRecurrence
        if (rrule == null){
            rrule = "";
        }
        b.putString(RecurrencePickerDialog.BUNDLE_RRULE, rrule);

        FragmentManager fm = getSupportFragmentManager();
        RecurrencePickerDialog rpd = (RecurrencePickerDialog)fm.findFragmentByTag(FRAG_TAG_RECUR_PICKER);
        if (rpd != null) {
            rpd.dismiss();
        }
        rpd = new RecurrencePickerDialog();
        rpd.setArguments(b);
        rpd.setOnRecurrenceSetListener(this);
        rpd.show(fm, FRAG_TAG_RECUR_PICKER);
    }

    @Override
    public void onDisableTrigger(){
        if (mAction.isEditable()){
            mTimeSelected = false;
            mDateSelected = false;
            setRRULE(null);
            saveActionTrigger(mAction);
        }
        else{
            finish();
        }
    }

    @Override
    public void onSaveTrigger(){
        saveActionTrigger(mAction);
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

        if (fragment != null){
            fragment.updateTimeView(getDisplayTime());
        }

        Toast.makeText(this,
                getString(R.string.time_picker_confirmation_toast, getDisplayTime()),
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

        if (fragment != null){
            fragment.updateDateView(getDisplayDate());
        }

        Toast.makeText(this,
                getText(R.string.date_picker_confirmation_toast),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecurrenceSet(String rrule){
        if (rrule != null){
            setEventRecurrence(rrule);
        }

        if(fragment != null){
            fragment.updateRecurrenceView(getFriendlyRecurrenceString());
        }

        //Our API needs the 'RRULE' prefix on the rrule string, but BetterPicker's
        //  EventRecurrence should *not* have that: https://goo.gl/QhY9aC
        setRRULE(rrule);

        Toast.makeText(this,getText(R.string.recurrence_picker_confirmation_toast),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * RRule getter.
     *
     * @return a RRule in an API compatible format.
     */
    public String getRRULE(){
        if (mRrule == null){
            mRrule = "";
        }
        if (!mRrule.isEmpty() && !mRrule.toUpperCase().startsWith("RRULE:")){
            mRrule = "RRULE:" + mRrule.toUpperCase();
        }
        return mRrule;
    }

    /**
     * Use the selected Time/Date/Recurrence to update the given User's Action.
     *
     * @param action the action to which the trigger belongs to.
     */
    private void saveActionTrigger(Action action){
        if (mAction.isEditable()){
            String rrule = getRRULE();
            String date = getApiDate();
            String time = getApiTime();

            Log.d(TAG, "saveActionTrigger, for Action: " + action.getTitle());
            Log.d(TAG, "Time: " + time);
            Log.d(TAG, "Date: " + date);
            Log.d(TAG, "RRULE: " + rrule);

            //Time is required and one of date or rule is required
            //TODO: Fails when time/date/rrule is empty or null
            mPutTriggerRequestCode = NetworkRequest.put(this, this,
                    API.getPutTriggerUrl(action),
                    ((CompassApplication)getApplication()).getToken(),
                    API.getPutTriggerBody(time, rrule, date));

            //We want to know that we've attempted to save, even if saving fails.
            savingTrigger = true;
        }
        else{
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mPutTriggerRequestCode){
            if (mAction instanceof UserAction){
                Parser.parse(result, UserAction.class, this);
            }
            else if (mAction instanceof CustomAction){
                Parser.parse(result, CustomAction.class, this);
            }
        }
    }

    @Override
    public void onRequestFailed(int requestCode, String message){
        if (requestCode == mPutTriggerRequestCode){
            Log.d(TAG, "actionTriggerAdded: received null Action");
            if (fragment != null){
                fragment.reportError();
            }
            Toast.makeText(this, getText(R.string.reminder_failed), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof Action){
            Action action = (Action)result;
            Log.d(TAG, "Updated Action: " + action.getTitle());
            Log.d(TAG, "Updated Trigger: " + action.getTrigger());

            mAction.setTrigger(action.getTrigger());
            mAction.setNextReminder(action.getNextReminder());
            ((CompassApplication)getApplication()).getUserData().updateActionTrigger(mAction);
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        Toast.makeText(this, getText(R.string.trigger_saved_confirmation_toast), Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
}
