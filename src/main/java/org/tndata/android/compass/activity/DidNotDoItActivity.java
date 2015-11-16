package org.tndata.android.compass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Reminder;
import org.tndata.android.compass.service.ActionReportService;
import org.tndata.android.compass.util.NotificationUtil;


/**
 * Dialog activity to confirm did not do it events.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class DidNotDoItActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int SNOOZE_REQUEST_CODE = 1874;


    private Reminder mReminder;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_did_not_do_it);
        setTitle("Are you sure?");

        mReminder = (Reminder)getIntent().getSerializableExtra(NotificationUtil.REMINDER_KEY);

        findViewById(R.id.did_not_do_it_ok).setOnClickListener(this);
        findViewById(R.id.did_not_do_it_later).setOnClickListener(this);
        findViewById(R.id.did_not_do_it_no).setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.did_not_do_it_ok:
                Intent didItIntent = new Intent(this, ActionReportService.class)
                        .putExtra(NotificationUtil.REMINDER_KEY, mReminder)
                        .putExtra(ActionReportService.STATE_KEY, ActionReportService.STATE_COMPLETED);
                startService(didItIntent);
                finish();
                break;

            case R.id.did_not_do_it_later:
                Intent snoozeIntent = new Intent(this, SnoozeActivity.class)
                        .putExtra(NotificationUtil.REMINDER_KEY, mReminder);
                //The activity terminates only if the snooze was carried out, so it needs
                //  to wait to the response to make that call
                startActivityForResult(snoozeIntent, SNOOZE_REQUEST_CODE);
                break;

            case R.id.did_not_do_it_no:
                Intent didNotDoItIntent = new Intent(this, ActionReportService.class)
                        .putExtra(NotificationUtil.REMINDER_KEY, mReminder)
                        .putExtra(ActionReportService.STATE_KEY, ActionReportService.STATE_UNCOMPLETED);
                startService(didNotDoItIntent);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == SNOOZE_REQUEST_CODE && resultCode == RESULT_OK){
            finish();
        }
    }
}
