package org.tndata.android.compass.activity;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import org.tndata.android.compass.fragment.ProgressFragment;
import org.tndata.android.compass.fragment.TriggerFragment;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.Trigger;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.task.AddActionTriggerTask;
import org.tndata.android.compass.task.GetUserActionsTask;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
                RecurrencePickerDialog.OnRecurrenceSetListener,
                RadialTimePickerDialog.OnTimeSetListener,
                CalendarDatePickerDialog.OnDateSetListener,
                AddActionTriggerTask.AddActionTriggerTaskListener,
                TriggerFragment.TriggerFragmentListener,
                GetUserActionsTask.GetUserActionsListener{

    public static final String NEEDS_FETCHING_KEY = "org.tndata.compass.Trigger.NeedsFetching";
    public static final String NOTIFICATION_ID_KEY = "org.tndata.compass.Trigger.NotificationId";
    public static final String ACTION_ID_KEY = "org.tndata.compass.Trigger.ActionId";

    private static final String TAG = "BaseTriggerActivity";
    private static final String FRAG_TAG_RECUR_PICKER = "recurrencePickerDialogFragment";
    private static final String FRAG_TAG_DATE_PICKER = "datePickerDialogFragment";
    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";


    private CompassApplication mApplication;

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


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mApplication = (CompassApplication)getApplication();

        mDateTime = new Date();
        mTimeSelected = false;
        mDateSelected = false;

        mDisplayTimeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        mDisplayDateFormat = new SimpleDateFormat("MMM d yyyy", Locale.getDefault());
        mApiTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        mApiDateFormat = new SimpleDateFormat("yyyy-MM-d", Locale.getDefault());
        mApiDateTimeFormat = new SimpleDateFormat("y-MM-d HH:mm", Locale.getDefault());

        setContentView(R.layout.activity_base);

        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.getBackground().setAlpha(255);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //If we are comming from a notification
        if (getIntent().getBooleanExtra(NEEDS_FETCHING_KEY, false)){
            getSupportActionBar().setTitle("Reschedule reminder");

            ProgressFragment progressFragment = new ProgressFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.base_content, progressFragment)
                    .commit();

            int notificationId = getIntent().getIntExtra(NOTIFICATION_ID_KEY, -1);
            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel(notificationId);

            fetchAction(getIntent().getIntExtra(ACTION_ID_KEY, -1));
        }
        else{
            UserData userData = mApplication.getUserData();

            Goal goal = (Goal)getIntent().getSerializableExtra("goal");
            mAction = userData.getAction((Action)getIntent().getSerializableExtra("action"));

            getSupportActionBar().setTitle(goal.getTitle());
            setAction();
        }
    }

    /**
     * Retrieves an action from an id.
     *
     * @param actionId the id of the action to be fetched.
     */
    private void fetchAction(int actionId){
        String token = ((CompassApplication)getApplication()).getToken();
        if (!token.isEmpty()){
            new GetUserActionsTask(this).execute(token, "action:" + actionId);
        }
        else{
            finish();
        }
    }

    @Override
    public void actionsLoaded(List<Action> actions){
        if (actions.size() > 0){
            mAction = mApplication.getUserData().getAction(actions.get(0));
            setAction();
        }
    }

    /**
     * Calls the initialisation routine and brongs in the TriggerFragment.
     */
    private void setAction(){
        initializeReminders(mAction.getTrigger());

        fragment = TriggerFragment.newInstance(mAction);
        if (fragment != null) {
            getFragmentManager().beginTransaction().replace(R.id.base_content, fragment).commit();
        }
    }

    /**
     * Populates the activity with the provided trigger.
     *
     * @param trigger the trigger from where the data is to be extracted.
     */
    public void initializeReminders(Trigger trigger){
        if (trigger != null){
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
        else{
            setRRULE("");
        }
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
    public void onDisableTrigger(){
        if (mAction.getCustomTrigger() == null){
            finish();
        }
        else{
            mTimeSelected = false;
            mDateSelected = false;
            setRRULE(null);
            saveActionTrigger(mAction);
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
        if (mAction.areCustomTriggersAllowed()){
            String rrule = getRRULE();
            String date = getApiDate();
            String time = getApiTime();

            Log.d(TAG, "saveActionTrigger, for Action: " + action.getTitle());
            Log.d(TAG, "Time: " + time);
            Log.d(TAG, "Date: " + date);
            Log.d(TAG, "RRULE: " + rrule);

            // Time is required and one of date or rule is required
            if (action != null){
                //TODO: Fails when time/date/rrule is empty or null
                new AddActionTriggerTask(this,
                        ((CompassApplication)getApplication()).getToken(),
                        rrule, time, date, String.valueOf(action.getMappingId()))
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            //We want to know that we've attempted to save, even if saving fails.
            savingTrigger = true;
        }
        else{
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public boolean actionTriggerAdded(Action action){
        // action is the updated Action, presumably with a Trigger attached.
        if(action != null){
            Log.d(TAG, "Updated Action: " + action.getTitle());
            Log.d(TAG, "Updated Trigger: " + action.getTrigger());

            mAction.setCustomTrigger(action.getTrigger());

            // We'll return the updated Action to the parent activity (GoalDetailsActivity)
            // but we also want to tell the Application about the updated version of the Action.
            //((CompassApplication) getApplication()).updateAction(action);

            Toast.makeText(this, getText(R.string.trigger_saved_confirmation_toast), Toast.LENGTH_SHORT).show();
            Intent returnIntent = new Intent();
            returnIntent.putExtra("action", mAction);
            setResult(RESULT_OK, returnIntent);
            finish();

            return true;
        }
        else{
            Log.d(TAG, "actionTriggerAdded: received null Action");
            if (fragment != null){
                fragment.reportError();
            }
            Toast.makeText(this, getText(R.string.reminder_failed), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
