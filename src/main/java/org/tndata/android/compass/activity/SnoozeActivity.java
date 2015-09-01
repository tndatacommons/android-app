package org.tndata.android.compass.activity;

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

    private static final String TAG = "SnoozeActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snooze);
        setTitle("Later");

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
            finish();
        }
        else if (position == 1){
            finish();
        }
        else if (position == 2){
            Calendar calendar = Calendar.getInstance();
            CalendarDatePickerDialog datePickerDialog = CalendarDatePickerDialog.newInstance(this,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show(getSupportFragmentManager(), "SnoozeDate");
        }
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int month, int day){
        Log.d(TAG, year + "-" + (month+1) + "-" + day);
        Calendar calendar = Calendar.getInstance();
        RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog.newInstance(this,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                android.text.format.DateFormat.is24HourFormat(this));

        timePickerDialog.show(getSupportFragmentManager(), "SnoozeTime");
    }

    @Override
    public void onTimeSet(RadialTimePickerDialog radialTimePickerDialog, int hour, int minute){
        Log.d(TAG, hour + ":" + minute);
        //TODO start the service and post
        finish();
    }
}
