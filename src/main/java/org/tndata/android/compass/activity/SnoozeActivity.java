package org.tndata.android.compass.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

import org.tndata.android.compass.R;
import org.tndata.android.compass.util.CompassRadialTimePickerDialog;

import java.util.Calendar;

/**
 * Created by isma on 8/25/15.
 */
public class SnoozeActivity
        extends AppCompatActivity
        implements
                RadialTimePickerDialog.OnTimeSetListener,
                RadialTimePickerDialog.OnDialogDismissListener,
                DialogInterface.OnKeyListener{

    private RadialTimePickerDialog mTimePickerDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snooze);
        setTitle("Snooze");

        Calendar calendar = Calendar.getInstance();
        mTimePickerDialog = CompassRadialTimePickerDialog.newInstance(this,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                android.text.format.DateFormat.is24HourFormat(this));

        mTimePickerDialog.setOnDismissListener(this);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.snooze_host, mTimePickerDialog)
                .commit();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onResumeFragments(){
        super.onResumeFragments();
        mTimePickerDialog.getDialog().setOnKeyListener(this);
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    @Override
    public void onTimeSet(RadialTimePickerDialog radialTimePickerDialog, int i, int i1){
        finish();
    }

    @Override
    public void onDialogDismiss(DialogInterface dialogInterface){
        finish();
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event){
        if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)){
            finish();
            return true;
        }
        return false;
    }
}
