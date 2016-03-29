package org.tndata.android.compass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence;
import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrenceFormatter;
import com.doomonafireball.betterpickers.recurrencepicker.RecurrencePickerDialog;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.CustomAction;
import org.tndata.android.compass.model.Trigger;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


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
 * TODO optimize and clean up this mess.
 *
 * @author Kevin Zetterstrom
 * @author Edited by Ismael Alonso
 * @version 2.1.0
 */
public class TriggerActivity
        extends AppCompatActivity
        implements
                View.OnClickListener,
                CompoundButton.OnCheckedChangeListener,
                HttpRequest.RequestCallback,
                Parser.ParserCallback,
                RecurrencePickerDialog.OnRecurrenceSetListener,
                RadialTimePickerDialog.OnTimeSetListener,
                CalendarDatePickerDialog.OnDateSetListener{

    public static final String GOAL_TITLE_KEY = "org.tndata.compass.TriggerActivity.GoalTitle";
    public static final String ACTION_KEY = "org.tndata.compass.TriggerActivity.Action";

    private static final String TAG = "TriggerActivity";
    private static final String FRAG_TAG_RECUR_PICKER = "recurrencePickerDialogFragment";
    private static final String FRAG_TAG_DATE_PICKER = "datePickerDialogFragment";
    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";


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

    //UI components
    private TextView datePickerTextView;
    private TextView timePickerTextView;
    private TextView recurrencePickerTextView;
    private ProgressBar mProgress;
    private TextView mUpdateTrigger;

    //Model components (extras)
    private Action mAction;
    private Trigger mTrigger;

    // An EventRecurrence instance gives us access to different representations
    // of the RRULE data.
    private EventRecurrence mEventRecurrence = new EventRecurrence();
    private String mRrule; // RFC 2445 RRULE string



    //Request codes
    private int mPutTriggerRequestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trigger);

        //Flags and stores
        mDateTime = new Date();
        mTimeSelected = false;
        mDateSelected = false;

        //Create parsers/formatters
        mDisplayTimeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        mDisplayDateFormat = new SimpleDateFormat("MMM d yyyy", Locale.getDefault());
        mApiTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        mApiDateFormat = new SimpleDateFormat("yyyy-MM-d", Locale.getDefault());
        mApiDateTimeFormat = new SimpleDateFormat("y-MM-d HH:mm", Locale.getDefault());

        //Grab UI components
        Toolbar toolbar = (Toolbar)findViewById(R.id.trigger_toolbar);
        TextView title = (TextView)findViewById(R.id.trigger_action_title);
        SwitchCompat enabled = (SwitchCompat)findViewById(R.id.trigger_enabled);
        timePickerTextView = (TextView)findViewById(R.id.trigger_time);
        datePickerTextView = (TextView)findViewById(R.id.trigger_date);
        recurrencePickerTextView = (TextView)findViewById(R.id.trigger_recurrence);
        mProgress = (ProgressBar)findViewById(R.id.trigger_progress);
        mUpdateTrigger = (TextView)findViewById(R.id.trigger_update);

        //Grab extras
        String goalTitle = getIntent().getStringExtra(GOAL_TITLE_KEY);
        mAction = getIntent().getParcelableExtra(ACTION_KEY);
        mTrigger = mAction.getTrigger();

        //Setup toolbar
        toolbar.setNavigationIcon(R.drawable.ic_back_white_24dp);
        toolbar.getBackground().setAlpha(255);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(goalTitle);
        }

        //Setup UI components
        title.setText(mAction.getTitle());
        enabled.setChecked(mTrigger.isEnabled());

        if (mAction.isEditable()){
            enabled.setEnabled(true);
            enabled.setOnCheckedChangeListener(this);
            findViewById(R.id.trigger_time_container).setOnClickListener(this);
            findViewById(R.id.trigger_date_container).setOnClickListener(this);
            findViewById(R.id.trigger_recurrence_container).setOnClickListener(this);
        }
        else{
            enabled.setEnabled(false);
        }
        mUpdateTrigger.setOnClickListener(this);

        try{
            if (mTrigger.getRawDate().equals("")){
                if (!mTrigger.getRawTime().equals("")){
                    mTimeSelected = true;
                    mDateTime = mTrigger.getTime();
                }
            }
            else{
                mDateSelected = true;
                if (!mTrigger.getRawTime().equals("")){
                    mTimeSelected = true;
                    String dateTime = mTrigger.getRawDate() + " " + mTrigger.getRawTime();
                    mDateTime = mApiDateTimeFormat.parse(dateTime);
                }
                else{
                    mDateTime = mTrigger.getDate();
                }
            }
        }
        catch (ParseException px){
            px.printStackTrace();
        }

        setRRULE(mTrigger.getRRULE());



        // Update labels with Trigger details if applicable.
        if(!mTrigger.getRawTime().isEmpty()) {
            updateTimeView(mTrigger.getTime());
        }
        if(!mTrigger.getRawDate().isEmpty()) {
            updateDateView(mTrigger.getDate());
        }
        if(!mTrigger.getRecurrences().isEmpty()) {
            updateRecurrenceView(mTrigger.getRecurrencesDisplay());
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
        switch (buttonView.getId()){
            case R.id.trigger_enabled:
                mTrigger.setEnabled(isChecked);
                break;
        }
    }

    @Override
    public void onClick(View view){
        Calendar calendar = Calendar.getInstance();
        FragmentManager fm = getSupportFragmentManager();
        switch (view.getId()){
            case R.id.trigger_time_container:
                RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog.newInstance(
                        this, calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), android.text.format.DateFormat.is24HourFormat(this));

                timePickerDialog.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
                break;

            case R.id.trigger_date_container:
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                        .newInstance(TriggerActivity.this, year, month, day);
                calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
                break;

            case R.id.trigger_recurrence_container:
                String rrule;
                if (!mTrigger.getRecurrences().isEmpty()){
                    rrule = mTrigger.getRRULE();
                }
                else{
                    rrule = Trigger.DEFAULT_RRULE;
                }
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

                RecurrencePickerDialog rpd = (RecurrencePickerDialog)fm.findFragmentByTag(FRAG_TAG_RECUR_PICKER);
                if (rpd != null) {
                    rpd.dismiss();
                }
                rpd = new RecurrencePickerDialog();
                rpd.setArguments(b);
                rpd.setOnRecurrenceSetListener(this);
                rpd.show(fm, FRAG_TAG_RECUR_PICKER);
                break;

            case R.id.trigger_update:
                mProgress.setVisibility(View.VISIBLE);
                mUpdateTrigger.setEnabled(false);
                saveActionTrigger(mAction);
                break;
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
    public void onTimeSet(RadialTimePickerDialog dialog, int hourOfDay, int minute){
        //A calendar instance is retrieved and the picked time is set
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDateTime);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        //The datetime object is replaced with the new one
        mDateTime = calendar.getTime();
        mTimeSelected = true;
        updateTimeView(getDisplayTime());

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
        updateDateView(getDisplayDate());

        Toast.makeText(this,
                getText(R.string.date_picker_confirmation_toast),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecurrenceSet(String rrule){
        if (rrule != null){
            setEventRecurrence(rrule);
        }
        updateRecurrenceView(getFriendlyRecurrenceString());

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

            Trigger trigger = new Trigger(time, date, rrule);
            mPutTriggerRequestCode = HttpRequest.put(this, API.getPutTriggerUrl(action),
                    API.getPutTriggerBody(trigger));
        }
        else{
            setResult(RESULT_CANCELED);
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
    public void onRequestFailed(int requestCode, HttpRequestError error){
        if (requestCode == mPutTriggerRequestCode){
            mProgress.setVisibility(View.GONE);
            mUpdateTrigger.setEnabled(true);
            Toast.makeText(this, getText(R.string.reminder_failed), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){

    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof Action){
            setResult(RESULT_OK, new Intent().putExtra(ACTION_KEY, (Parcelable)result));
            finish();
        }
    }



    public void updateTimeView(String time){
        timePickerTextView.setText(time);
    }

    public void updateTimeView(Date time){
        timePickerTextView.setText(mDisplayTimeFormat.format(time));
    }

    public void updateDateView(String date){
        datePickerTextView.setText(date);
    }

    public void updateDateView(Date date){
        datePickerTextView.setText(mDisplayDateFormat.format(date));
    }

    public void updateRecurrenceView(String recurrence){
        if(recurrence != null && !recurrence.isEmpty()){
            recurrencePickerTextView.setText(recurrence);
        }
        else{
            recurrencePickerTextView.setText(getText(R.string.trigger_recurrence_picker_label));
        }
    }
}
