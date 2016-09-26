package org.tndata.android.compass.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.tndata.android.compass.R;
import org.tndata.android.compass.service.LocationNotificationService;
import org.tndata.android.compass.ui.ProgressView;
import org.tndata.android.compass.ui.TransitionButton;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * An activity to test new features without compromising the integrity of the
 * rest of the application.
 */
public class PlaygroundActivity extends AppCompatActivity implements View.OnClickListener{
    private ProgressView mProgressView;
    private TransitionButton button;
    private int state;
    static String TAG = "Playground";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playground);

        //button = (TransitionButton)findViewById(R.id.playground_button);
        //button.setOnClickListener(this);

        state = 0;

        mProgressView = (ProgressView)findViewById(R.id.playground_test);
        //mProgressView.setBackgroundColor(Color.RED);
        mProgressView.setProgressValue(66);

        findViewById(R.id.playground_button_start).setOnClickListener(this);
        findViewById(R.id.playground_button_kill).setOnClickListener(this);

        findViewById(R.id.playground_button_calendar).setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        /*if (state == 0){
            button.setTransitioningToActive(true);
            state = 1;
        }
        else if (state == 1){
            button.setActive(true);
            state = 2;
        }
        else if (state == 2){
            button.setTransitioningToInactive(true);
            state = 3;
        }
        else if (state == 3){
            button.setInactive(true);
            state = 0;
        }*/
        switch (v.getId()){
            case R.id.playground_button_start:
                LocationNotificationService.start(this);
                break;

            case R.id.playground_button_kill:
                LocationNotificationService.kill(this);
                break;

            case R.id.playground_button_calendar:

                // --------------------------------------------------------------------
                // This is an experiment in trying to detect & parse a date from a string.
                // 1. Use a regex to see if we have a date in some chunk of text.
                // 2. If so, pull it out and create a date object, setting the approprate year
                // 3. Use the created date to construct a duration and launch an Intent to
                //    save that in the user's calendar.
                // --------------------------------------------------------------------
                String description = "Ages 5-12: Registration Required.\n" +
                        "\nPersonal Safety - Wednesday, July 13, 2:30 pm-3:30 pm. Children ages 6-12.";
                //Pattern p = Pattern.compile("[A-Z][a-z]+ \\d+, *\\d+:\\d\\d *[am|pm]* *- *\\d+:\\d\\d *[am|pm]*");
                Pattern p = Pattern.compile("[A-Z][a-z]+ \\d+, *\\d+:\\d\\d *[am|pm]*"); // only the start date/time
                Matcher m = p.matcher(description);

                Date date;

                if (m.find()) {
                    // match
                    String startString = description.substring(m.start(), m.end());

                    Log.d(TAG, "FOUND! '" + m.group() + "' (from " + m.start() + " to " + m.end() + ")");
                    Log.d(TAG, "---> '" + description.substring(m.start(), m.end()) + "'");

                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, hh:mm aa");
                    ParsePosition pos = new ParsePosition(0);
                    Date startDate = (Date)sdf.parseObject(startString, pos);

                    // --------------------------------------------------------------------
                    // Convert the parsed date into milliseconds and generate a duration,
                    // then ask to save to the calendar.
                    // -------------------------------------------------------------------
                    Calendar now = Calendar.getInstance(); // for use in comparing
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(startDate);

                    Log.d(TAG, "-- current year: " + now.get(Calendar.YEAR));
                    Log.d(TAG, "-- parsed date's year: " + cal.get(Calendar.YEAR));

                    // Since the Year's not included in the date string we parsed, the date
                    // we're given could be in the past. If so, we need to update the year.
                    if(cal.getTimeInMillis() < now.getTimeInMillis()) {
                        cal.set(Calendar.YEAR, now.get(Calendar.YEAR) + 1);
                    }

                    Log.d(TAG, "---> DATE: " + cal.toString());

                    // Make all events a 2-hour duration.
                    long start = cal.getTimeInMillis();
                    cal.add(Calendar.HOUR_OF_DAY, 2);
                    long end = cal.getTimeInMillis();

                    // launch an intent for the calendar
                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setType("vnd.android.cursor.item/event");
                    intent.putExtra("title", "My Great new event");
                    intent.putExtra("description", "And heres' an description to let me know what's happening.");
                    intent.putExtra("beginTime", start);
                    intent.putExtra("endTime", end);
                    startActivity(intent);

                } else {
                    Log.d(TAG, "No Date string found");
                }

                break;
        }
    }
}
