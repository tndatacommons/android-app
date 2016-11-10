package org.tndata.android.compass.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ActionAdapter;
import org.tndata.compass.model.Action;
import org.tndata.compass.model.CustomAction;
import org.tndata.compass.model.ResultSet;
import org.tndata.compass.model.TDCCategory;
import org.tndata.compass.model.UserAction;
import org.tndata.android.compass.parser.Parser;
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
        implements ActionAdapter.Listener, HttpRequest.RequestCallback, Parser.ParserCallback{

    private static final String TAG = "ActionActivity";

    public static final String ACTION_KEY = "org.tndata.compass.ActionActivity.Action";
    public static final String DID_IT_KEY = "org.tndata.compass.ActionActivity.DidIt";

    private static final int SNOOZE_REQUEST_CODE = 61428;
    private static final int RESCHEDULE_REQUEST_CODE = 61429;
    private static final int REWARD_REQUEST_CODE = 4298;

    private static final int GOAL_RC = 6390;


    //Poor man's "time in activity" timer.
    private long mStartTime;

    //References to the app class and the adapter
    private CompassApplication mApp;
    private ActionAdapter mAdapter;

    //The action in question
    private Action mAction;

    private int mGetCategoryRC;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //CompassUtil.log(this, "ActionActivity", "Action Activity is firing up");

        mApp = (CompassApplication)getApplication();

        //Get the action, upcoming action and message from the intent. Only one of them
        //  will be actually something other than null
        Action action = getIntent().getParcelableExtra(ACTION_KEY);

        getRecyclerView().addItemDecoration(new ItemSpacing(this, 8));

        //If the action exists set it up
        if (action != null){
            //CompassUtil.log(this, "Gcm Message - ActionActivity", "Action: " + action.toString());
            setAction(action);
        }
        //Otherwise, finish the activity, as this should never happen
        else{
            //CompassUtil.log(this, "Gcm Message - ActionActivity", "Action is null, why?");
            finish();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        //We want to get a rough idea of the amount of time the user spends in this
        //  activity prior to tapping "Got It". Therefore, we'll grab a timestamp here,
        //  then again when they tap the button, and log the difference.
        mStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onStop(){
        int time = (int)(System.currentTimeMillis()-mStartTime)/1000;
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName(mAction.getTitle())
                .putContentType("Action")
                .putContentId("" + mAction.getId())
                .putCustomAttribute("Duration", time));
        super.onStop();
    }

    /**
     * Sets the action to be displayed and initializes the adapter.
     *
     * @param action the action to be displayed.
     */
    private void setAction(@NonNull Action action){
        mAction = action;

        //Set the category
        if (mAction instanceof UserAction){
            UserAction userAction = (UserAction)mAction;
            long categoryId = userAction.getPrimaryCategoryId();
            TDCCategory category = mApp.getAvailableCategories().get(categoryId);
            if (category == null){
                mGetCategoryRC = HttpRequest.get(this, API.URL.getCategory(categoryId));
            }
            setCategory(category);
        }
        else{
            setCategory(null);
        }

        //Set the adapter
        mAdapter = new ActionAdapter(this, this, mAction);
        setAdapter(mAdapter);

        //Refresh the menu
        invalidateOptionsMenu();
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetCategoryRC){
            Parser.parse(result, TDCCategory.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        //no-op
    }

    @Override
    public void onProcessResult(int requestCode, ResultSet result){
        //no-op
    }

    @Override
    public void onParseSuccess(int requestCode, ResultSet result){
        if (result instanceof TDCCategory){
            TDCCategory category = (TDCCategory)result;
            setCategory(category);
            mAdapter.setCategory(category);
        }
    }

    @Override
    public void onParseFailed(int requestCode){
        //no-op
    }

    /**
     * Sets the header with the provided category.
     *
     * @param category the category to be displayed.
     */
    @SuppressWarnings("deprecation")
    private void setCategory(@Nullable TDCCategory category){
        View header = inflateHeader(R.layout.header_hero);
        ImageView image = (ImageView)header.findViewById(R.id.header_hero_image);

        if (category != null){
            //When a category is available use its attributes to set the header up
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
            setColor(getResources().getColor(R.color.primary));
            image.setImageResource(R.drawable.compass_master_illustration);
        }
    }

    /**
     * Fires the tour.
     *
     * @param target the target view, the got it button in this case.
     */
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
    public boolean onCreateOptionsMenu(Menu menu){
        //We need to check for null action here because sometimes the action needs to
        //  be fetched from the backend. If the action has not been fetched yet, the
        //  overflow button doesn't make sense
        if (mAction == null || !mAction.isEditable()){
            return false;
        }

        if (mAction.hasTrigger() && mAction.getTrigger().isEnabled()){
            getMenuInflater().inflate(R.menu.menu_action, menu);
        }
        else{
            getMenuInflater().inflate(R.menu.menu_action_disabled, menu);
        }

        return true;
    }

    @Override
    public boolean menuItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_view_goal:
                viewGoal();
                break;

            case R.id.action_reschedule:
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
        if (mAction instanceof UserAction){
            UserAction userAction = (UserAction) mAction;
            if (userAction.getAction().hasDatetimeResource()){

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
        if (requestCode == GOAL_RC){
            if (resultCode == MyGoalActivity.GOAL_REMOVED_RC){
                setResult(resultCode, data);
                finish();
            }
            else if (resultCode == CustomContentActivity.GOAL_REMOVED_RC){
                setResult(resultCode, data);
                finish();
            }
        }
        else if (resultCode == RESULT_OK){
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
        else if (requestCode == REWARD_REQUEST_CODE){
            finish();
            if (mApp.getFeedData() == null){
                startActivity(new Intent(this, LauncherActivity.class));
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
        Intent intent;
        if (mAction instanceof UserAction){
            long userGoalId = ((UserAction)mAction).getPrimaryUserGoalId();
            intent = MyGoalActivity.getIntent(this, userGoalId);
        }
        else {
            long customGoalId = ((CustomAction) mAction).getCustomGoalId();
            intent = new Intent(this, CustomContentActivity.class)
                    .putExtra(CustomContentActivity.CUSTOM_GOAL_ID_KEY, customGoalId);
        }
        startActivityForResult(intent, GOAL_RC);
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
            startActivityForResult(RewardActivity.getIntent(this, null), REWARD_REQUEST_CODE);
        }
    }
}
