package org.tndata.android.compass.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence;
import com.doomonafireball.betterpickers.recurrencepicker.RecurrencePickerDialog;
import com.doomonafireball.betterpickers.timepicker.TimePickerBuilder;
import com.doomonafireball.betterpickers.timepicker.TimePickerDialogFragment;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.task.AddActionTriggerTask;

/**
 * Created by kevin on 6/14/15.
 */
public class BaseTriggerActivity extends ActionBarActivity implements
        RecurrencePickerDialog.OnRecurrenceSetListener,
        TimePickerDialogFragment.TimePickerDialogHandler,
        AddActionTriggerTask.AddActionTriggerTaskListener {

    private EventRecurrence mEventRecurrence = new EventRecurrence();
    private String mRrule;
    private String mTime;

    private Action mActionToUpdate;
    private Behavior mBehaviorToUpdate;

    private static final String TAG = "BaseTriggerActivity";
    private static final String FRAG_TAG_RECUR_PICKER = "recurrencePickerDialogFragment";

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
        if(!mRrule.isEmpty() && !mRrule.toUpperCase().startsWith("RRULE:")) {
            mRrule = "RRULE:" + mRrule.toUpperCase();
        }
        return mRrule;
    }

    protected void getRecurrenceSchedule(Action action, Behavior behavior) {
        if (action != null) {
            mActionToUpdate = action;
        } else if (behavior != null) {
            mBehaviorToUpdate = behavior;
        } else {
            throw new IllegalStateException("Must set either an Action or Behavior!");
        }
        showTimePicker();
    }

    private void showTimePicker() {
        TimePickerBuilder tpb = new TimePickerBuilder()
                .setFragmentManager(getSupportFragmentManager())
                .setStyleResId(R.style.BetterPickersDialogFragment_Light);
        tpb.show();
    }

    private void showRecurrencePicker() {
        Bundle b = new Bundle();
        Time t = new Time();
        t.setToNow();
        b.putLong(RecurrencePickerDialog.BUNDLE_START_TIME_MILLIS, t.toMillis(false));
        b.putString(RecurrencePickerDialog.BUNDLE_TIME_ZONE, t.timezone);

        // may be more efficient to serialize and pass in EventRecurrence
        b.putString(RecurrencePickerDialog.BUNDLE_RRULE, mRrule);

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
        // Our API needs the 'RRULE' prefix on the rrule string, but
        // BetterPicker's EventRecurrence should *not* have that: https://goo.gl/QhY9aC
        setRRULE(rrule);
        if (rrule != null) {
            // use local rrule string for the EventRecurrence
            mEventRecurrence.parse(rrule);
        }
        saveTrigger();
    }

    @Override
    public void onDialogTimeSet(int reference, int hourOfDay, int minute) {
        Log.d(TAG, "reference: " + reference + ", hourOfDay: " + hourOfDay + ", minute: " + minute);
        mTime = String.format("%02d", hourOfDay) + ":" +
                String.format("%02d", minute);
        Toast.makeText(this,
                getString(R.string.time_picker_confirmation_toast, mTime),
                Toast.LENGTH_SHORT).show();
        showRecurrencePicker();
    }

    private void saveTrigger() {
        String rrule = getRRULE();
        Log.d(TAG, "mTime: " + mTime);
        Log.d(TAG, "mRrule: " + rrule);

        if (!TextUtils.isEmpty(rrule)) {
            if (mActionToUpdate != null) {
                new AddActionTriggerTask(this,
                        ((CompassApplication) getApplication()).getToken(),
                        rrule, mTime, String.valueOf(mActionToUpdate.getMappingId()))
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    public void actionTriggerAdded(Action action) {
        // action is the updated Action, presumably with a Trigger attached.
        if(action != null) {
            mActionToUpdate = action;

            Log.d(TAG, "Updated Action & Trigger: " + action.getCustomTrigger().getRecurrences());
            Toast.makeText(this,
                    getText(R.string.recurrence_picker_confirmation_toast),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save Reminder", Toast.LENGTH_SHORT).show();
        }
    }
}
