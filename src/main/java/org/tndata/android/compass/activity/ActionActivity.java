package org.tndata.android.compass.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ActionAdapter;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.CustomAction;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.service.ActionReportService;
import org.tndata.android.compass.model.Reminder;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.ImageLoader;
import org.tndata.android.compass.util.NetworkRequest;
import org.tndata.android.compass.util.NotificationUtil;


/**
 * Displays an action after clicking a notification and allows the user to report
 * whether they did it or snooze the action.
 *
 * @author Ismael Alonso
 * @version 1.2.1
 */
public class ActionActivity
        extends LibraryActivity
        implements
                View.OnClickListener,
                NetworkRequest.RequestCallback,
                Parser.ParserCallback{

    public static final String ACTION_KEY = "org.tndata.compass.ActionActivity.Action";
    public static final String REMINDER_KEY = "org.tndata.compass.ActionActivity.Reminder";

    public static final String DID_IT_KEY = "org.tndata.compass.ActionActivity.DidIt";

    private static final int SNOOZE_REQUEST_CODE = 61428;
    private static final int RESCHEDULE_REQUEST_CODE = 61429;


    private CompassApplication mApplication;

    //The action in question and the associated reminder
    private Action mAction;
    private Reminder mReminder;

    private ActionAdapter mAdapter;

    //Firewall
    private boolean mActionUpdated;


    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mApplication = (CompassApplication)getApplication();

        //Retrieve the action and mark the reminder as nonexistent
        mAction = (UserAction)getIntent().getSerializableExtra(ACTION_KEY);
        mReminder = null;

        if (mAction != null){
            Action temp = mApplication.getUserData().getAction(mAction);
            if (temp != null){
                mAction = temp;
            }
        }

        mAdapter = new ActionAdapter(this, mAction);

        setAdapter(mAdapter);

        mActionUpdated = false;

        //If the action wasn't provided via the intent it needs to be fetched
        if (mAction == null){
            //timeOption.setText(R.string.action_snooze);
            mReminder = (Reminder)getIntent().getSerializableExtra(REMINDER_KEY);
            fetchAction();
        }
        else{
            mAction = mApplication.getUserData().getAction(mAction);
            if (mAction instanceof UserAction){
                setColor(Color.parseColor(((UserAction)mAction).getPrimaryCategory().getColor()));
            }
            else{
                setColor(getResources().getColor(R.color.grow_primary));
            }
            setHeader();
            //timeOption.setText(R.string.action_reschedule);
            /*if (mAction instanceof UserAction){
                populateUI((UserAction)mAction);
            }
            else if (mAction instanceof CustomAction){
                populateUI((CustomAction)mAction);
            }*/
        }
    }

    @SuppressWarnings("deprecation")
    private void setHeader(){
        View header = inflateHeader(R.layout.header_icon);
        RelativeLayout circle = (RelativeLayout)header.findViewById(R.id.header_icon_circle);
        ImageView icon = (ImageView)header.findViewById(R.id.header_icon_icon);

        GradientDrawable gradientDrawable = (GradientDrawable) circle.getBackground();
        gradientDrawable.setColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            circle.setBackground(gradientDrawable);
        }
        else{
            circle.setBackgroundDrawable(gradientDrawable);
        }

        if (mAction instanceof UserAction){
            ImageLoader.loadBitmap(icon, ((UserAction)mAction).getIconUrl());
        }
    }

    /**
     * Retrieves an action from the API
     */
    private void fetchAction(){
        if (mReminder.isUserAction()){
            int mappingId = mReminder.getUserMappingId();
            Log.d("ActionActivity", "Fetching UserAction: " + mappingId);
            NetworkRequest.get(this, this, API.getActionUrl(mappingId), mApplication.getToken());
        }
        else if (mReminder.isCustomAction()){
            int customId = mReminder.getObjectId();
            Log.d("ActionActivity", "Fetching UserAction: " + customId);
            NetworkRequest.get(this, this, API.getCustomActionUrl(customId), mApplication.getToken());
        }
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (mReminder.isUserAction()){
            Parser.parse(result, UserAction.class, this);
        }
        else if (mReminder.isCustomAction()){
            Parser.parse(result, CustomAction.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, String message){
        mAdapter.displayError("Couldn't retrieve information");
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof Action){
            mAction = (Action)result;
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof UserAction || result instanceof CustomAction){
            mAction = (Action)result;
            setHeader();
            mAdapter.setAction(mAction);
            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //We need to check for null action here because sometimes the action needs to
        //  be fetched from the backend. If the action has not been fetched yet, the
        //  overflow button doesn't make sense
        if (mAction != null && mAction.isEditable()){
            if (!mAction.hasTrigger() || mAction.getTrigger().isDisabled()){
                getMenuInflater().inflate(R.menu.menu_action_disabled, menu);
            }
            else{
                getMenuInflater().inflate(R.menu.menu_action, menu);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean menuItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_trigger:
                reschedule();
                break;

            case R.id.action_disable_trigger:
                disableTrigger();
                break;

            default:
                return false;
        }
        return true;
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.action_time_option:
                if (mReminder != null){
                    snooze();
                }
                else{
                    reschedule();
                }
                break;

            case R.id.action_did_it:
                didIt();
                break;

            case R.id.action_do_it_now:
                CompassUtil.doItNow(this, ((UserAction)mAction).getAction().getExternalResource());
                break;
        }
    }

    /**
     * Snooze clicked. This opens the snooze menu.
     */
    private void snooze(){
        if (mAction != null && !mActionUpdated){
            Intent snoozeIntent = new Intent(this, SnoozeActivity.class)
                    .putExtra(NotificationUtil.REMINDER_KEY, mReminder);
            startActivityForResult(snoozeIntent, SNOOZE_REQUEST_CODE);
        }
    }

    /**
     * Reschedule clicked. This opens the trigger picker.
     */
    private void reschedule(){
        if (mAction != null && !mActionUpdated){
            Intent reschedule = new Intent(this, TriggerActivity.class)
                    .putExtra(TriggerActivity.ACTION_KEY, mAction)
                    .putExtra(TriggerActivity.GOAL_KEY, mAction.getGoal());
            startActivityForResult(reschedule, RESCHEDULE_REQUEST_CODE);
        }
    }

    /**
     * Disables the current action's trigger.
     */
    private void disableTrigger(){
        NetworkRequest.put(this, null, API.getPutTriggerUrl(mAction),
                mApplication.getToken(), API.getPutTriggerBody("", "", ""));
        mAction.setTrigger(null);
        invalidateOptionsMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case RESCHEDULE_REQUEST_CODE:if (mReminder.isUserAction()){
                    NotificationUtil.cancel(this, NotificationUtil.USER_ACTION_TAG,
                            mReminder.getUserMappingId());
                }
                else if (mReminder.isCustomAction()){
                    NotificationUtil.cancel(this, NotificationUtil.CUSTOM_ACTION_TAG,
                            mReminder.getObjectId());
                }

                //In either case, the activity should finish after a second
                case SNOOZE_REQUEST_CODE:
                    mActionUpdated = true;
                    setResult(RESULT_OK, new Intent().putExtra(DID_IT_KEY, false));
                    finish();
            }
        }
    }

    /**
     * I did it clicked.
     */
    private void didIt(){
        if (mAction != null && !mActionUpdated){
            mActionUpdated = true;

            startService(new Intent(this, ActionReportService.class)
                    .putExtra(ActionReportService.ACTION_KEY, mAction)
                    .putExtra(ActionReportService.STATE_KEY, ActionReportService.STATE_COMPLETED));

            setResult(RESULT_OK, new Intent().putExtra(DID_IT_KEY, true));
            finish();
        }
    }
}
