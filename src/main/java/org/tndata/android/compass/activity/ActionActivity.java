package org.tndata.android.compass.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ActionAdapter;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.CustomAction;
import org.tndata.android.compass.model.UpcomingAction;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.model.UserCategory;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.service.ActionReportService;
import org.tndata.android.compass.model.Reminder;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.ImageLoader;
import org.tndata.android.compass.util.NotificationUtil;

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
                ActionAdapter.ActionAdapterListener,
                HttpRequest.RequestCallback,
                Parser.ParserCallback{

    public static final String ACTION_KEY = "org.tndata.compass.ActionActivity.Action";
    public static final String UPCOMING_ACTION_KEY = "org.tndata.compass.ActionActivity.Upcoming";
    public static final String REMINDER_KEY = "org.tndata.compass.ActionActivity.Reminder";

    public static final String DID_IT_KEY = "org.tndata.compass.ActionActivity.DidIt";

    private static final int SNOOZE_REQUEST_CODE = 61428;
    private static final int RESCHEDULE_REQUEST_CODE = 61429;


    //The action in question and the associated reminder
    private Action mAction;
    //private Goal mGoal;
    private UserCategory mUserCategory;
    private UpcomingAction mUpcomingAction;
    private Reminder mReminder;

    private ActionAdapter mAdapter;

    private int mGetActionRC;
    private int mGetUserCategoryRC;


    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Get the action, upcoming action and reminder from the intent. Only one of them
        //  will be actually something other than null
        mAction = getIntent().getParcelableExtra(ACTION_KEY);
        mUpcomingAction = getIntent().getParcelableExtra(UPCOMING_ACTION_KEY);
        mReminder = getIntent().getParcelableExtra(REMINDER_KEY);

        //Set a placeholder color
        setColor(getResources().getColor(R.color.primary));

        //Create and set the adapter
        mAdapter = new ActionAdapter(this, this, mReminder != null);
        setAdapter(mAdapter);

        //If the action exists do some initial setup and fetching
        if (mAction != null){
            if (mAction instanceof UserAction){
                UserAction userAction = (UserAction)mAction;
                fetchCategory(userAction);
            }
            else{
                setHeader();
            }
            mAdapter.setAction(mAction);
        }
        else{
            fetchAction();
        }
    }

    /**
     * Sets up the header of the activity
     */
    private void setHeader(){
        View header = inflateHeader(R.layout.header_hero);
        ImageView image = (ImageView)header.findViewById(R.id.header_hero_image);
        if (mUserCategory == null){
            image.setImageResource(R.drawable.compass_master_illustration);
        }
        else{
            if (mUserCategory.getCategory().getImageUrl() == null){
                image.setImageResource(R.drawable.compass_master_illustration);
            }
            else{
                ImageLoader.Options options = new ImageLoader.Options().setUsePlaceholder(false);
                ImageLoader.loadBitmap(image, mUserCategory.getCategory().getImageUrl(), options);
            }
        }
    }

    /**
     * Tells whether the action is a user action.
     *
     * @return true if it is a user action, false otherwise.
     */
    @SuppressWarnings("RedundantIfStatement")
    private boolean isUserAction(){
        if (mAction != null && mAction instanceof UserAction){
            return true;
        }
        if (mUpcomingAction != null && mUpcomingAction.isUserAction()){
            return true;
        }
        if (mReminder != null && mReminder.isUserAction()){
            return true;
        }
        return false;
    }

    /**
     * Tells whether the action is a custom action.
     *
     * @return true if it is a custom action, false otherwise.
     */
    @SuppressWarnings("RedundantIfStatement")
    private boolean isCustomAction(){
        if (mAction != null && mAction instanceof CustomAction){
            return true;
        }
        if (mUpcomingAction != null && mUpcomingAction.isCustomAction()){
            return true;
        }
        if (mReminder != null && mReminder.isCustomAction()){
            return true;
        }
        return false;
    }

    /**
     * Gets the id of the action. This will be the mapping id in the case of a user action
     * and the object id in case of a user action.
     *
     * @return the relevant id for the action.
     */
    private int getActionId(){
        if (mAction != null){
            return (int)mAction.getId();
        }
        if (mUpcomingAction != null){
            return (int)mUpcomingAction.getId();
        }
        if (mReminder != null){
            if (mReminder.isUserAction()){
                return mReminder.getUserMappingId();
            }
            else if (mReminder.isCustomAction()){
                return mReminder.getObjectId();
            }
        }
        return -1;
    }

    /**
     * Retrieves an action from the API
     */
    private void fetchAction(){
        int id = getActionId();
        if (isUserAction()){
            Log.d("ActionActivity", "Fetching UserAction: " + id);
            mGetActionRC = HttpRequest.get(this, API.getActionUrl(id));
        }
        else if (isCustomAction()){
            Log.d("ActionActivity", "Fetching CustomAction: " + id);
            mGetActionRC = HttpRequest.get(this, API.getCustomActionUrl(id));
        }
    }

    /**
     * Fetches a category from the backend.
     *
     * @param userAction the user action whose category is to be fetched.
     */
    private void fetchCategory(UserAction userAction){
        long userCategoryId = userAction.getPrimaryCategoryId();
        mGetUserCategoryRC = HttpRequest.get(this, API.getUserCategoryUrl(userCategoryId));
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetActionRC){
            if (isUserAction()){
                Parser.parse(result, UserAction.class, this);
            }
            else if (isCustomAction()){
                Parser.parse(result, CustomAction.class, this);
            }
        }
        else if (requestCode == mGetUserCategoryRC){
            Parser.parse(result, ParserModels.UserCategoryResultSet.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        mAdapter.displayError("Couldn't retrieve activity information");
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof Action){
            mAction = (Action)result;
        }
        else if (result instanceof ParserModels.UserCategoryResultSet){
            mUserCategory = ((ParserModels.UserCategoryResultSet)result).results.get(0);
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof UserAction){
            mAdapter.setAction(mAction);
            fetchCategory((UserAction)mAction);
            invalidateOptionsMenu();
        }
        else if (result instanceof CustomAction){
            mAdapter.setAction(mAction);
            invalidateOptionsMenu();
        }
        else if (result instanceof ParserModels.UserCategoryResultSet){
            setColor(Color.parseColor(mUserCategory.getColor()));
            setHeader();
            mAdapter.setCategory(mUserCategory.getCategory());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //We need to check for null action here because sometimes the action needs to
        //  be fetched from the backend. If the action has not been fetched yet, the
        //  overflow button doesn't make sense
        if (mAction == null || !mAction.isEditable()){
            return false;
        }
        if (mReminder != null){
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
        //TODO Fix this crap
        /*if (mGoal != null){
            if (mGoal instanceof UserGoal){
                startActivity(new Intent(this, ReviewActionsActivity.class)
                        .putExtra(ReviewActionsActivity.USER_GOAL_KEY, mGoal));
            }
            else{
                startActivity(new Intent(this, CustomContentManagerActivity.class)
                        .putExtra(CustomContentManagerActivity.CUSTOM_GOAL_KEY, mGoal));
            }
        }*/
    }

    @Override
    public void onIDidItClick(){
        if (mAction != null){
            startService(new Intent(this, ActionReportService.class)
                    .putExtra(ActionReportService.ACTION_KEY, mAction)
                    .putExtra(ActionReportService.STATE_KEY, ActionReportService.STATE_COMPLETED));

            setResult(RESULT_OK, new Intent().putExtra(DID_IT_KEY, true)
                    .putExtra(ACTION_KEY, mAction));
            finish();
        }
    }

    @Override
    public void onRescheduleClick(){
        if (mAction != null){
            Intent reschedule = new Intent(this, TriggerActivity.class)
                    .putExtra(TriggerActivity.GOAL_TITLE_KEY, mAction.getGoalTitle())
                    .putExtra(TriggerActivity.ACTION_KEY, mAction);
            startActivityForResult(reschedule, RESCHEDULE_REQUEST_CODE);
        }
    }

    @Override
    public void onSnoozeClick(){
        if (mAction != null){
            Intent snoozeIntent = new Intent(this, SnoozeActivity.class)
                    .putExtra(NotificationUtil.REMINDER_KEY, mReminder);
            startActivityForResult(snoozeIntent, SNOOZE_REQUEST_CODE);
        }
    }

    /**
     * Disables the current action's trigger.
     */
    private void disableTrigger(){
        mAction.getTrigger().setEnabled(false);
        HttpRequest.put(null, API.getPutTriggerUrl(mAction), API.getPutTriggerBody(mAction.getTrigger()));
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
                    NotificationUtil.cancel(this, NotificationUtil.USER_ACTION_TAG, getActionId());
                    finish();
            }
        }
    }
}
