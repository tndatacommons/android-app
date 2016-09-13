package org.tndata.android.compass.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.NewActionAdapter;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.CustomAction;
import org.tndata.android.compass.model.GcmMessage;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.model.UpcomingAction;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.service.ActionReportService;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.ImageLoader;
import org.tndata.android.compass.util.ItemSpacing;
import org.tndata.android.compass.util.Tour;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * Displays an action after clicking a notification and allows the user to report
 * whether they did it or snooze the action.
 *
 * @author Ismael Alonso
 * @version 1.3.0
 */
public class ActionActivity
        extends MaterialActivity
        implements
                HttpRequest.RequestCallback,
                Parser.ParserCallback,
                NewActionAdapter.Listener{

    private static final String TAG = "ActionActivity";

    public static final String ACTION_KEY = "org.tndata.compass.ActionActivity.Action";
    public static final String UPCOMING_ACTION_KEY = "org.tndata.compass.ActionActivity.Upcoming";
    public static final String GCM_MESSAGE_KEY = "org.tndata.compass.ActionActivity.GcmMessage";

    public static final String DID_IT_KEY = "org.tndata.compass.ActionActivity.DidIt";

    private static final int SNOOZE_REQUEST_CODE = 61428;
    private static final int RESCHEDULE_REQUEST_CODE = 61429;


    private CompassApplication mApp;

    private NewActionAdapter mAdapter;

    //The action in question and the associated reminder
    private Action mAction;

    private int mGetActionRC;
    private int mDeleteBehaviorRC;

    private boolean mFromGcm;
    private boolean mUserAction;


    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mApp = (CompassApplication)getApplication();

        //Get the action, upcoming action and message from the intent. Only one of them
        //  will be actually something other than null
        Action action = getIntent().getParcelableExtra(ACTION_KEY);
        UpcomingAction upcomingAction = getIntent().getParcelableExtra(UPCOMING_ACTION_KEY);
        GcmMessage gcmMessage = getIntent().getParcelableExtra(GCM_MESSAGE_KEY);

        getRecyclerView().addItemDecoration(new ItemSpacing(this, 8));

        //If the action exists do some initial setup
        if (action != null){
            setAction(action);
        }
        //Otherwise fetch it
        else{
            setColor(getResources().getColor(R.color.primary));

            String url = "";
            if (gcmMessage != null){
                if (gcmMessage.isUserActionMessage()){
                    Log.d(TAG, "Fetching UserAction #" + gcmMessage.getUserAction().getId());
                    url = API.URL.getUserAction(gcmMessage.getUserAction().getId());
                    mUserAction = true;
                }
                else if (gcmMessage.isCustomActionMessage()){
                    Log.d(TAG, "Fetching CustomAction #" + gcmMessage.getCustomAction().getId());
                    url = API.URL.getCustomAction(gcmMessage.getCustomAction().getId());
                    mUserAction = false;
                }
                mFromGcm = true;
            }
            else if (upcomingAction != null){
                if (upcomingAction.isUserAction()){
                    Log.d(TAG, "Fetching UserAction #" + upcomingAction.getId());
                    url = API.URL.getUserAction(upcomingAction.getId());
                    mUserAction = true;
                }
                else if (upcomingAction.isCustomAction()){
                    Log.d(TAG, "Fetching CustomAction #" + upcomingAction.getId());
                    url = API.URL.getCustomAction(upcomingAction.getId());
                    mUserAction = false;
                }
            }
            mGetActionRC = HttpRequest.get(this, url);
        }
    }

    private void setAction(@NonNull Action action){
        mAction = action;

        //Set the category
        if (mAction instanceof UserAction){
            UserAction userAction = (UserAction)mAction;
            setCategory(mApp.getAvailableCategories().get(userAction.getPrimaryCategoryId()));
        }
        else{
            setCategory(null);
        }

        //Set the adapter
        mAdapter = new NewActionAdapter(this, this, mAction);
        setAdapter(mAdapter);

        //Refresh the menu
        invalidateOptionsMenu();
    }

    private void setCategory(@Nullable TDCCategory category){
        View header = inflateHeader(R.layout.header_hero);
        ImageView image = (ImageView)header.findViewById(R.id.header_hero_image);

        if (category != null){
            //mAdapter.setCategory(category);
            setColor(Color.parseColor(category.getColor()));
            if (category.getImageUrl() == null || category.getImageUrl().isEmpty()){
                image.setImageResource(R.drawable.compass_master_illustration);
            }
            else{
                ImageLoader.Options options = new ImageLoader.Options()
                        .setPlaceholder(R.drawable.compass_master_illustration);
                ImageLoader.loadBitmap(image, category.getImageUrl(), options);
            }
        }
        else{
            image.setImageResource(R.drawable.compass_master_illustration);
        }
    }

    private void fireTour(View target){
        Queue<Tour.Tooltip> tooltips = new LinkedList<>();
        for (Tour.Tooltip tooltip:Tour.getTooltipsFor(Tour.Section.ACTION)){
            if (tooltip == Tour.Tooltip.ACTION_GOT_IT){
                tooltip.setTarget(target);
                tooltips.add(tooltip);
            }
        }
        Tour.display(this, tooltips);
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetActionRC){
            Log.d(TAG, "Action fetched, parsing");
            if (mUserAction){
                Parser.parse(result, UserAction.class, this);
            }
            else{
                Parser.parse(result, CustomAction.class, this);
            }
        }
        else if (requestCode == mDeleteBehaviorRC){
            // We deleted some content so there's really nothing to show.
            Log.d("XXX", "Deleted Behavior");
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        //mAdapter.displayError("Couldn't retrieve activity information");
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        //no-op
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof Action){
            setAction((Action)result);
        }
        else{
            Log.e(TAG, "Result ain't an instance of Action");
        }
    }

    @Override
    public void onParseFailed(int requestCode){
        Log.e(TAG, "Action couldn't be parsed");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //We need to check for null action here because sometimes the action needs to
        //  be fetched from the backend. If the action has not been fetched yet, the
        //  overflow button doesn't make sense
        if (mAction == null || !mAction.isEditable()){
            return false;
        }
        if (mFromGcm){
            if (mAction.hasTrigger() && mAction.getTrigger().isEnabled()){
                getMenuInflater().inflate(R.menu.menu_action_reminder, menu);
            }
            else{
                getMenuInflater().inflate(R.menu.menu_action_reminder_disabled, menu);
            }
        }
        else{
            if (mAction.hasTrigger() && mAction.getTrigger().isEnabled()){
                getMenuInflater().inflate(R.menu.menu_action, menu);
            }
            else{
                getMenuInflater().inflate(R.menu.menu_action_disabled, menu);
            }
        }
        return true;
    }

    @Override
    public boolean menuItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_view_goal:
                viewGoal();
                break;

            case R.id.action_trigger:
                onRescheduleClick();
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
    public void onBackPressed(){
        setResult(RESULT_OK, new Intent().putExtra(ACTION_KEY, mAction));
        finish();
    }

    @Override
    protected void onHomeTapped(){
        setResult(RESULT_OK, new Intent().putExtra(ACTION_KEY, mAction));
        finish();
    }

    private void viewGoal(){
        if (mAction instanceof UserAction){
            startActivity(new Intent(this, ReviewActionsActivity.class)
                    .putExtra(ReviewActionsActivity.USER_ACTION_KEY, mAction));
        }
        else{
            long customGoalId = ((CustomAction)mAction).getCustomGoalId();
            startActivity(new Intent(this, CustomContentActivity.class)
                    .putExtra(CustomContentActivity.CUSTOM_GOAL_ID_KEY, customGoalId));
        }
    }

    public void onRescheduleClick(){
        if (mAction != null){
            Intent reschedule = new Intent(this, TriggerActivity.class)
                    .putExtra(TriggerActivity.GOAL_TITLE_KEY, mAction.getGoalTitle())
                    .putExtra(TriggerActivity.ACTION_KEY, mAction);
            startActivityForResult(reschedule, RESCHEDULE_REQUEST_CODE);
        }
    }

    /**
     * If the user is viewing a UserAction whose externalResourceName is "datetime", then the
     * externalResource is a datetime string (of the form YYYY-mm-dd hh:mm:ss). This is likely
     * for a specific scheduled event, so we'll give them an option to add to their Calendar.
     */
    public void sendToCalendar(){
        if (mAction instanceof UserAction) {
            UserAction userAction = (UserAction) mAction;
            if(userAction.getAction().hasDatetimeResource()) {

                // NOTE: Let's save this as a positive action, prior to adding to their calendar.
                startService(new Intent(this, ActionReportService.class)
                        .putExtra(ActionReportService.ACTION_KEY, mAction)
                        .putExtra(ActionReportService.STATE_KEY, ActionReportService.STATE_COMPLETED));

                String externalResource = userAction.getExternalResource();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.getDefault());
                ParsePosition pos = new ParsePosition(0);
                Date startDate = (Date) sdf.parseObject(externalResource, pos);

                // Convert the parsed date into milliseconds and generate a duration,
                // (a default of 2 hours should be good) then ask to save to the calendar.
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDate);

                // Generate a start/end duration for the calendar's event.
                // We'll just make all events a 2-hour duration.
                long start = cal.getTimeInMillis();
                cal.add(Calendar.HOUR_OF_DAY, 2);
                long end = cal.getTimeInMillis();

                // launch an intent for the calendar
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("title", userAction.getTitle());
                intent.putExtra("description", userAction.getDescription());
                intent.putExtra("beginTime", start);
                intent.putExtra("endTime", end);
                startActivity(intent);
            }
        }
    }

    /**
     * Disables the current action's trigger.
     */
    private void disableTrigger(){
        mAction.getTrigger().setEnabled(false);
        HttpRequest.put(null, API.URL.putTrigger(mAction), API.BODY.putTrigger(mAction.getTrigger()));
        setResult(RESULT_OK, new Intent().putExtra(ACTION_KEY, mAction));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case RESCHEDULE_REQUEST_CODE:
                    mAction = data.getParcelableExtra(TriggerActivity.ACTION_KEY);
                    setResult(RESULT_OK, new Intent().putExtra(ACTION_KEY, mAction));

                //In either case, the activity should finish after a second
                case SNOOZE_REQUEST_CODE:
                    //NotificationUtil.cancel(this, NotificationUtil.USER_ACTION_TAG, getActionId());
                    finish();
            }
        }
    }

    @Override
    public void onContentCardLoaded(){
        if (!Tour.getTooltipsFor(Tour.Section.ACTION).isEmpty()){
            getRecyclerView().scrollToPosition(3);
            fireTour(mAdapter.getGotItButton());
        }
    }

    @Override
    public void onGoalClick(){

    }

    @Override
    public void onGotItClick(){
        if (mAction != null){
            startService(new Intent(this, ActionReportService.class)
                    .putExtra(ActionReportService.ACTION_KEY, mAction)
                    .putExtra(ActionReportService.STATE_KEY, ActionReportService.STATE_COMPLETED));

            setResult(RESULT_OK, new Intent().putExtra(DID_IT_KEY, true)
                    .putExtra(ACTION_KEY, mAction));

            Toast.makeText(this, R.string.action_completed_toast, Toast.LENGTH_SHORT).show();
            CompassApplication application = (CompassApplication)getApplication();
            if(application.getFeedData() == null) {
                startActivity(new Intent(this, LauncherActivity.class));
            }
            finish();
        }
    }

    @Override
    public void onDeleteBehaviorClick(){
        if (mAction instanceof UserAction){
            String url = API.URL.deleteBehavior(((UserAction)mAction).getUserBehaviorId());
            mDeleteBehaviorRC = HttpRequest.delete(null, url);
            finish();
        }
    }
}
