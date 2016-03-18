package org.tndata.android.compass.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Trigger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Contains the UI components of TriggerActivity.
 *
 * TODO merge with TriggerActivity. Fragment here is unnecessary since it is not reused and
 * TODO merging it would make the code more compact.
 *
 * @author Edited by Ismael Alonso
 * @version 2.0.0
 */
public class TriggerFragment
        extends Fragment
        implements
                View.OnClickListener,
                CompoundButton.OnCheckedChangeListener{

    private static final String ACTION_KEY = "org.tndata.compass.TriggerFragment.Action";
    private static final String TRIGGER_KEY = "org.tndata.compass.TriggerFragment.Trigger";

    private TriggerFragmentListener mCallback;

    private Action mAction;
    private Trigger mTrigger;

    private TextView datePickerTextView;
    private TextView timePickerTextView;
    private TextView recurrencePickerTextView;
    private ProgressBar mProgress;
    private TextView mUpdateTrigger;


    /**
     * Creates a new instance of the fragment and delivers the action.
     *
     * @param action the action containing the trigger.
     * @return the new fragment.
     */
    public static TriggerFragment newInstance(@NonNull Action action){
        Bundle args = new Bundle();
        args.putParcelable(ACTION_KEY, action);

        TriggerFragment fragment = new TriggerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        //This makes sure that the container activity has implemented the callback
        //  interface. If not, it throws an exception
        try{
            mCallback = (TriggerFragmentListener)context;
        }
        catch (ClassCastException ccx){
            throw new ClassCastException(context.toString()
                    + " must implement ActionTriggerFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mAction = getArguments().getParcelable(ACTION_KEY);
        mTrigger = mAction.getTrigger();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_trigger, container, false);
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState){
        TextView title = (TextView)rootView.findViewById(R.id.action_title);
        title.setText(mAction.getTitle());

        SwitchCompat notificationSwitch = (SwitchCompat)rootView.findViewById(R.id.trigger_enabled);
        notificationSwitch.setChecked(mTrigger.isEnabled());

        timePickerTextView = (TextView)rootView.findViewById(R.id.trigger_time);
        datePickerTextView = (TextView)rootView.findViewById(R.id.trigger_date);
        recurrencePickerTextView = (TextView)rootView.findViewById(R.id.trigger_recurrence);

        mProgress = (ProgressBar)rootView.findViewById(R.id.trigger_progress);
        mUpdateTrigger = (TextView)rootView.findViewById(R.id.trigger_update);
        mUpdateTrigger.setOnClickListener(this);

        if (mAction.isEditable()){
            notificationSwitch.setEnabled(true);
            notificationSwitch.setOnCheckedChangeListener(this);
            rootView.findViewById(R.id.trigger_time_container).setOnClickListener(this);
            rootView.findViewById(R.id.trigger_date_container).setOnClickListener(this);
            rootView.findViewById(R.id.trigger_recurrence_container).setOnClickListener(this);
        }
        else{
            notificationSwitch.setEnabled(false);
        }

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
                if (!isChecked){
                    mCallback.onDisableTrigger();
                }
                break;
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.trigger_time_container:
                mCallback.onFireTimePicker();
                break;

            case R.id.trigger_date_container:
                mCallback.onFireDatePicker();
                break;

            case R.id.trigger_recurrence_container:
                if (!mTrigger.getRecurrences().isEmpty()){
                    mCallback.onFireRecurrencePicker(mTrigger.getRRULE());
                }
                else{
                    mCallback.onFireRecurrencePicker(Trigger.DEFAULT_RRULE);
                }
                break;

            case R.id.trigger_update:
                mProgress.setVisibility(View.VISIBLE);
                mUpdateTrigger.setEnabled(false);
                mCallback.onSaveTrigger();
                break;
        }
    }

    public void updateTimeView(String time){
        timePickerTextView.setText(time);
    }

    public void updateTimeView(Date time) {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
        timePickerTextView.setText(sdf.format(time));
    }

    public void updateDateView(String date){
        datePickerTextView.setText(date);
    }

    public void updateDateView(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d yyyy", Locale.getDefault());
        datePickerTextView.setText(sdf.format(date));
    }

    public void updateRecurrenceView(String recurrence) {
        if(recurrence != null && !recurrence.isEmpty()) {
            recurrencePickerTextView.setText(recurrence);
        } else {
            recurrencePickerTextView.setText(getText(R.string.trigger_recurrence_picker_label));
        }
    }

    public void reportError(){
        mProgress.setVisibility(View.GONE);
        mUpdateTrigger.setEnabled(true);
    }


    /**
     * Listener interface for actions carried out in this fragment.
     *
     * @author Edited by Ismael Alonso
     * @version 1.0.0
     */
    public interface TriggerFragmentListener{
        /**
         * Called when the time button is clicked.
         */
        void onFireTimePicker();

        /**
         * Called when the date button is clicked.
         */
        void onFireDatePicker();

        /**
         * Called when the recurrence picker is clicked.
         *
         * @param rrule the current recurrence rule.
         */
        void onFireRecurrencePicker(String rrule);

        /**
         * Called when the save button is clicked.
         */
        void onSaveTrigger();

        /**
         * Called when the enabled switch is switched to false.
         */
        void onDisableTrigger();
    }
}
