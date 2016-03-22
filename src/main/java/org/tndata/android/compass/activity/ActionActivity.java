package org.tndata.android.compass.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ActionAdapter;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.CustomAction;
import org.tndata.android.compass.model.CustomGoal;
import org.tndata.android.compass.model.GoalContent;
import org.tndata.android.compass.model.UpcomingAction;
import org.tndata.android.compass.model.UserAction;
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
    private CustomGoal mCustomGoal;
    private GoalContent mGoal;
    private CategoryContent mCategory;
    private UpcomingAction mUpcomingAction;
    private Reminder mReminder;

    private ActionAdapter mAdapter;

    private int mGetActionRC;
    private int mGetCustomGoalRC;
    private int mGetGoalRC;
    private int mGetCategoryRC;


    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Retrieve the action and mark the reminder and upcoming action as nonexistent
        mAction = getIntent().getParcelableExtra(ACTION_KEY);
        mUpcomingAction = null;
        mReminder = null;

        setColor(getResources().getColor(R.color.grow_primary));

        //If the action exists do some initial setup and fetching
        if (mAction != null){
            if (mAction instanceof UserAction){
                UserAction userAction = (UserAction)mAction;
                fetchCategory(userAction);
            }
            else{
                setHeader();
            }
            fetchGoal(mAction);
        }

        //If the action wasn't provided via the intent it needs to be fetched
        if (mAction == null){
            mUpcomingAction = getIntent().getParcelableExtra(UPCOMING_ACTION_KEY);
            mReminder = (Reminder)getIntent().getSerializableExtra(REMINDER_KEY);
            fetchAction();
        }

        //Create and set the adapter
        mAdapter = new ActionAdapter(this, this, mReminder != null);
        setAdapter(mAdapter);
    }

    /**
     * Sets up the header of the activity
     */
    private void setHeader(){
        View header = inflateHeader(R.layout.header_hero);
        ImageView image = (ImageView)header.findViewById(R.id.header_hero_image);
        if (mCategory == null){
            image.setImageResource(R.drawable.compass_master_illustration);
        }
        else{
            ImageLoader.Options options = new ImageLoader.Options().setUsePlaceholder(false);
            ImageLoader.loadBitmap(image, mCategory.getImageUrl(), options);
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
     * Fetches a goal from the backend.
     *
     * @param action the action whose goal is to be fetched.
     */
    private void fetchGoal(Action action){
        if (action instanceof UserAction){
            UserAction userAction = (UserAction)action;
            mGetGoalRC = HttpRequest.get(this, API.getGoalUrl(userAction.getPrimaryGoalId()));
        }
        else{
            CustomAction customAction = (CustomAction)action;
            mGetCustomGoalRC = HttpRequest.get(this, API.getCustomGoalUrl(customAction.getCustomGoalId()));
        }
    }

    /**
     * Fetches a category from the backend.
     *
     * @param userAction the user action whose category is to be fetched.
     */
    private void fetchCategory(UserAction userAction){
        mGetCategoryRC = HttpRequest.get(this, API.getCategoryUrl(userAction.getPrimaryCategoryId()));
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
        else if (requestCode == mGetCustomGoalRC){
            Parser.parse(result, CustomGoal.class, this);
        }
        else if (requestCode == mGetGoalRC){
            Parser.parse(result, GoalContent.class, this);
        }
        else if (requestCode == mGetCategoryRC){
            Parser.parse(result, CategoryContent.class, this);
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
        else if (result instanceof CustomGoal){
            mCustomGoal = (CustomGoal)result;
        }
        else if (result instanceof GoalContent){
            mGoal = (GoalContent)result;
        }
        else if (result instanceof CategoryContent){
            mCategory = (CategoryContent)result;
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof UserAction){
            fetchGoal(mAction);
            fetchCategory((UserAction)mAction);
        }
        else if (result instanceof CustomAction){
            fetchGoal(mAction);
        }
        else if (result instanceof CustomGoal){
            setHeader();
            mAdapter.setAction(mAction, null);
            invalidateOptionsMenu();
        }
        else if (result instanceof GoalContent){
            mAdapter.setAction(mAction, null);
            if (mCategory != null){
                mAdapter.setCategory(mCategory);
            }
            invalidateOptionsMenu();
        }
        else if (result instanceof CategoryContent){
            setColor(Color.parseColor(mCategory.getColor()));
            setHeader();
            if (mGoal != null){
                mAdapter.setCategory(mCategory);
            }
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

    private void viewGoal(){

    }

    @Override
    public void onBackPressed(){
        setResult(RESULT_OK, new Intent().putExtra(ACTION_KEY, (Parcelable)mAction));
        finish();
    }

    @Override
    public void onIDidItClick(){
        if (mAction != null){
            startService(new Intent(this, ActionReportService.class)
                    .putExtra(ActionReportService.ACTION_KEY, (Parcelable)mAction)
                    .putExtra(ActionReportService.STATE_KEY, ActionReportService.STATE_COMPLETED));

            setResult(RESULT_OK, new Intent().putExtra(DID_IT_KEY, true)
                    .putExtra(ACTION_KEY, (Parcelable)mAction));
            finish();
        }
    }

    @Override
    public void onRescheduleClick(){
        if (mAction != null){
            String goalTitle = mGoal != null ? mGoal.getTitle() : mCustomGoal.getTitle();
            Intent reschedule = new Intent(this, TriggerActivity.class)
                    .putExtra(TriggerActivity.GOAL_TITLE_KEY, goalTitle)
                    .putExtra(TriggerActivity.ACTION_KEY, (Parcelable)mAction);
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
        HttpRequest.put(null, API.getPutTriggerUrl(mAction), API.getPutTriggerBody("", "", ""));
        mAction.setTrigger(null);
        invalidateOptionsMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case RESCHEDULE_REQUEST_CODE:
                    mAction = data.getParcelableExtra(TriggerActivity.ACTION_KEY);
                    setResult(RESULT_OK, new Intent().putExtra(ACTION_KEY, (Parcelable)mAction));

                //In either case, the activity should finish after a second
                case SNOOZE_REQUEST_CODE:
                    NotificationUtil.cancel(this, NotificationUtil.USER_ACTION_TAG, getActionId());
                    finish();
            }
        }
    }
}
