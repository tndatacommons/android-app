package org.tndata.android.compass.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
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
 * @author Edited by Ismael Alonso
 * @version 2.0.0
 */
public class TriggerFragment
        extends Fragment
        implements
                View.OnClickListener,
                CompoundButton.OnCheckedChangeListener{

    //private static final String TAG = "ActionTriggerFragment";

    private TriggerFragmentListener mCallback;

    private Action mAction;
    private Trigger mTrigger;

    private TextView datePickerTextView;
    private TextView timePickerTextView;
    private TextView recurrencePickerTextView;


    /**
     * Creates a new instance of the fragment and delivers the action.
     *
     * @param action the action to be delivered.
     * @return the fragment.
     */
    public static TriggerFragment newInstance(Action action){
        Bundle args = new Bundle();
        args.putSerializable("action", action);

        TriggerFragment fragment = new TriggerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        //This makes sure that the container activity has implemented
        //   the callback interface. If not, it throws an exception
        try{
            mCallback = (TriggerFragmentListener)activity;
        }
        catch (ClassCastException e){
            throw new ClassCastException(activity.toString()
                    + " must implement ActionTriggerFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mAction = (getArguments() != null) ? ((Action) getArguments().get(
                "action")) : new Action();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mTrigger = mAction.getTrigger();

        View view = inflater.inflate(R.layout.fragment_trigger, container, false);

        TextView title = (TextView)view.findViewById(R.id.action_title_textview);
        title.setText(mAction.getTitle());

        Switch notificationSwitch = (Switch)view.findViewById(R.id.notification_option_switch);
        notificationSwitch.setChecked(!mTrigger.isDisabled());
        notificationSwitch.setOnCheckedChangeListener(this);

        timePickerTextView = (TextView)view.findViewById(R.id.time_picker_textview);
        view.findViewById(R.id.time_picker_container).setOnClickListener(this);

        datePickerTextView = (TextView)view.findViewById(R.id.date_picker_textview);
        view.findViewById(R.id.date_picker_container).setOnClickListener(this);

        recurrencePickerTextView = (TextView)view.findViewById(R.id.recurrence_picker_textview);
        view.findViewById(R.id.recurrence_picker_container).setOnClickListener(this);

        view.findViewById(R.id.trigger_update_textview).setOnClickListener(this);

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

        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
        switch (buttonView.getId()){
            case R.id.notification_option_switch:
                if (!isChecked){
                    mCallback.onDisableTrigger();
                }
                break;
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.time_picker_container:
                mCallback.onFireTimePicker();
                break;

            case R.id.date_picker_container:
                mCallback.onFireDatePicker();
                break;

            case R.id.recurrence_picker_container:
                if (!mTrigger.getRecurrences().isEmpty()){
                    mCallback.onFireRecurrencePicker(mTrigger.getRRULE());
                }
                else{
                    mCallback.onFireRecurrencePicker(Trigger.DEFAULT_RRULE);
                }
                break;

            case R.id.trigger_update_textview:
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
