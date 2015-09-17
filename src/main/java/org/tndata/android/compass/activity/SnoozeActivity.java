package org.tndata.android.compass.activity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.SnoozeAdapter;
import org.tndata.android.compass.database.CompassDbHelper;
import org.tndata.android.compass.model.Place;
import org.tndata.android.compass.model.Reminder;
import org.tndata.android.compass.service.GcmIntentService;
import org.tndata.android.compass.service.LocationNotificationService;
import org.tndata.android.compass.service.SnoozeService;

import java.util.Calendar;
import java.util.List;


/**
 * Activity with the appearance of a dialog that lets the user pick a snoozing
 * option for a notification.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class SnoozeActivity
        extends AppCompatActivity
        implements
                AdapterView.OnItemClickListener,
                CalendarDatePickerDialog.OnDateSetListener,
                RadialTimePickerDialog.OnTimeSetListener,
                DialogInterface.OnClickListener{

    public static final String REMINDER_KEY = "org.tndata.compass.Snooze.Reminder";
    public static final String NOTIFICATION_ID_KEY = "org.tndata.compass.Snooze.NotificationId";
    public static final String PUSH_NOTIFICATION_ID_KEY = "org.tndata.compass.Snooze.PushNotificationId";

    private static final String TAG = "SnoozeActivity";

    private static final int PLACES_REQUEST_CODE = 600;


    private Reminder mReminder;
    private int notificationId;
    private int pushNotificationId;
    private int mYear, mMonth, mDay;

    private List<Place> mPlaces;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snooze);
        setTitle(R.string.later_title);

        mReminder = (Reminder)getIntent().getSerializableExtra(REMINDER_KEY);
        notificationId = getIntent().getIntExtra(NOTIFICATION_ID_KEY, -1);
        pushNotificationId = getIntent().getIntExtra(PUSH_NOTIFICATION_ID_KEY, -1);

        ListView list = (ListView)findViewById(R.id.snooze_list);
        list.setAdapter(new SnoozeAdapter(this));
        list.setOnItemClickListener(this);

        CompassDbHelper dbHelper = new CompassDbHelper(this);
        mPlaces = dbHelper.getPlaces();
        dbHelper.close();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        if (position == 0){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(calendar.getTimeInMillis()+3600*1000);
            snooze(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1,
                    calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE));
        }
        else if (position == 1){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(calendar.getTimeInMillis()+24*3600*1000);
            snooze(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1,
                    calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE));
        }
        else if (position == 2){
            //Start the date picker
            Calendar calendar = Calendar.getInstance();
            CalendarDatePickerDialog datePickerDialog = CalendarDatePickerDialog.newInstance(this,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show(getSupportFragmentManager(), "SnoozeDate");
        }
        else if (position == 3){
            displayPlacesDialog();
        }
    }

    /**
     * Opens a dialog with a list of places or an item telling the user to pick places.
     */
    private void displayPlacesDialog(){
        Log.d(TAG, mPlaces.size() + " places found");
        CharSequence[] placeNames = new CharSequence[mPlaces.size()==0 ? 1 : mPlaces.size()];
        if (mPlaces.size() == 0){
            placeNames[0] = getString(R.string.later_no_places);
        }
        else{
            for (int i = 0; i < mPlaces.size(); i++){
                placeNames[i] = mPlaces.get(i).getName();
            }
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.later_pick_place)
                .setItems(placeNames, this)
                .create().show();
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int month, int day){
        //Set the data and log it
        mYear = year;
        mMonth = month+1;
        mDay = day;
        Log.d(TAG, mYear + "-" + mMonth + "-" + mDay);

        //Start the time picker
        Calendar calendar = Calendar.getInstance();
        RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog.newInstance(this,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                android.text.format.DateFormat.is24HourFormat(this));

        timePickerDialog.show(getSupportFragmentManager(), "SnoozeTime");
    }

    @Override
    public void onTimeSet(RadialTimePickerDialog radialTimePickerDialog, int hour, int minute){
        Log.d(TAG, hour + ":" + minute);
        snooze(mYear, mMonth, mDay, hour, minute);
    }

    /**
     * Snoozes a notification time-wise.
     *
     * @param year the year to snooze to.
     * @param month the month to snooze to.
     * @param day the day to snooze to.
     * @param hour the hour to snooze to.
     * @param minute the minute to snooze to.
     */
    private void snooze(int year, int month, int day, int hour, int minute){
        //Translate to strings
        String monthString = month + "";
        String dayString = day + "";
        String hourString = hour + "";
        String minuteString = minute + "";

        //Process the data
        if (monthString.length() == 1){
            monthString = "0" + monthString;
        }
        if (dayString.length() == 1){
            dayString = "0" + dayString;
        }
        if (hourString.length() == 1){
            hourString = "0" + hourString;
        }
        if (minuteString.length() == 1){
            minuteString = "0" + minuteString;
        }

        //Prepare the strings
        String date = year + "-" + monthString + "-" + dayString;
        String time = hourString + ":" + minuteString;

        //Log the data to verify format
        Log.d(TAG, date + " " + time);

        //Fire up the service with the appropriate parameters
        Intent snooze = new Intent(this, SnoozeService.class);
        snooze.putExtra(SnoozeService.NOTIFICATION_ID_KEY, notificationId);
        snooze.putExtra(SnoozeService.PUSH_NOTIFICATION_ID_KEY, pushNotificationId);
        snooze.putExtra(SnoozeService.DATE_KEY, date);
        snooze.putExtra(SnoozeService.TIME_KEY, time);
        startService(snooze);

        //Kill the activity
        finish();
    }

    @Override
    public void onClick(DialogInterface dialog, int which){
        if (mPlaces.size() == 0){
            startActivityForResult(new Intent(this, PlacesActivity.class), PLACES_REQUEST_CODE);
        }
        else{
            Log.d(TAG, mPlaces.get(which).getName() + " selected");
            mReminder.setPlaceId(mPlaces.get(which).getId());
            mReminder.setSnoozed(true);
            CompassDbHelper dbHelper = new CompassDbHelper(this);
            dbHelper.saveReminder(mReminder);
            dbHelper.close();

            NotificationManager manager = ((NotificationManager)getSystemService(NOTIFICATION_SERVICE));
            manager.cancel(GcmIntentService.NOTIFICATION_TYPE_ACTION, pushNotificationId);

            startService(new Intent(this, LocationNotificationService.class));

            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == PLACES_REQUEST_CODE){
            //Reload the list of places
            CompassDbHelper dbHelper = new CompassDbHelper(this);
            mPlaces = dbHelper.getPlaces();
            dbHelper.close();

            //Create the dialog
            displayPlacesDialog();
        }
    }
}
