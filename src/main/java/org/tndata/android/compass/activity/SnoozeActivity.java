package org.tndata.android.compass.activity;

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
import org.tndata.android.compass.service.SnoozeService;

import java.util.Calendar;


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
                RadialTimePickerDialog.OnTimeSetListener{

    public static final String NOTIFICATION_ID_KEY = "org.tndata.compass.Snooze.NotificationId";
    public static final String PUSH_NOTIFICATION_ID_KEY = "org.tndata.compass.Snooze.PushNotificationId";

    private static final String TAG = "SnoozeActivity";

    private int notificationId;
    private int pushNotificationId;
    private int mYear, mMonth, mDay;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snooze);
        setTitle(R.string.later_title);

        notificationId = getIntent().getIntExtra(NOTIFICATION_ID_KEY, -1);
        pushNotificationId = getIntent().getIntExtra(PUSH_NOTIFICATION_ID_KEY, -1);

        ListView list = (ListView)findViewById(R.id.snooze_list);
        list.setAdapter(new SnoozeAdapter(this));
        list.setOnItemClickListener(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public void onBackPressed(){
        finish();
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
}
