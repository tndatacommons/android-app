package org.tndata.android.compass.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ActionAdapter;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.CustomAction;
import org.tndata.android.compass.model.Reminder;
import org.tndata.android.compass.model.TDCAction;
import org.tndata.android.compass.model.UpcomingAction;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.model.UserCategory;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.service.ActionReportService;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.ImageLoader;
import org.tndata.android.compass.util.NotificationUtil;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    private UserCategory mUserCategory;
    private UpcomingAction mUpcomingAction;
    private Reminder mReminder;

    private ActionAdapter mAdapter;

    private int mGetActionRC;
    private int mGetUserCategoryRC;
    private int mDeleteBehaviorRC;


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
        if (mUserCategory == null || mUserCategory.getCategory().getImageUrl().isEmpty()){
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
        else if (requestCode == mDeleteBehaviorRC) {
            // We deleted some content so there's really nothing to show.
            Log.d("XXX", "Deleted Behavior");
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
        if (mAction instanceof UserAction){
            startActivity(new Intent(this, ReviewActionsActivity.class)
                    .putExtra(ReviewActionsActivity.USER_ACTION_KEY, mAction));
        }
        else{
            long customGoalId = ((CustomAction)mAction).getCustomGoalId();
            startActivity(new Intent(this, CustomContentManagerActivity.class)
                    .putExtra(CustomContentManagerActivity.CUSTOM_GOAL_ID_KEY, customGoalId));
        }
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

    @Override
    public void onBehaviorInfoClick() {
        if(isUserAction()) {
            // Display a dialog that show's the behavior's Title & Description, with buttons
            // to dismiss the dialog or to delete the behavior. If the user chooses to delete
            // the behavior, we ask for a confirmation before sending the Http request.
            final long userBehaviorId = ((UserAction)mAction).getUserBehaviorId();
            TDCAction action = ((UserAction)mAction).getAction();

            ViewGroup rootView = (ViewGroup)findViewById(android.R.id.content);
            LayoutInflater inflater = LayoutInflater.from(this);
            View dialogRootView = inflater.inflate(R.layout.dialog_behavior, rootView, false);
            TextView behaviorTitle = (TextView)dialogRootView.findViewById(R.id.dialog_behavior_title);
            final TextView behaviorDescription = (TextView)dialogRootView.findViewById(R.id.dialog_behavior_description);

            behaviorTitle.setText(action.getBehaviorTitle());
            behaviorDescription.setText(action.getBehaviorDescription());

            final Button cancelButton = (Button)dialogRootView.findViewById(R.id.behavior_dialog_cancel);
            final Button removeButton = (Button)dialogRootView.findViewById(R.id.behavior_dialog_remove);
            final Button confirmButton = (Button)dialogRootView.findViewById(R.id.behavior_dialog_confirm);
            final Button cancelConfirmButton = (Button)dialogRootView.findViewById(R.id.behavior_dialog_cancel_confirm);

            final AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(dialogRootView)
                    .create();

            // I want to close this activity if the user deletes the parent behavior, and
            // I also use this activity as the callback for the Http request.
            final ActionActivity parent = this;

            View.OnClickListener buttonHandler = new View.OnClickListener() {
                public void onClick(View v) {
                    switch(v.getId()) {
                        case R.id.behavior_dialog_cancel_confirm:
                        case R.id.behavior_dialog_cancel:
                            dialog.dismiss();
                            break;
                        case R.id.behavior_dialog_remove:
                            behaviorDescription.setText(getString(R.string.behavior_dialog_confirm_message));
                            removeButton.setVisibility(View.GONE);
                            cancelButton.setVisibility(View.GONE);
                            confirmButton.setVisibility(View.VISIBLE);
                            cancelConfirmButton.setVisibility(View.VISIBLE);
                            break;
                        case R.id.behavior_dialog_confirm:
                            // Send the Delete http request and close the activity.
                            String url = API.getDeleteBehaviorUrl(userBehaviorId);
                            mDeleteBehaviorRC = HttpRequest.delete(parent, url);
                            dialog.dismiss();
                            parent.finish();
                            break;
                    }
                }
            };

            // Handler for buttons in the dialog
            cancelConfirmButton.setOnClickListener(buttonHandler);
            confirmButton.setOnClickListener(buttonHandler);
            removeButton.setOnClickListener(buttonHandler);
            cancelButton.setOnClickListener(buttonHandler);
            dialog.show();
        }
    }

    /**
     * Detect if the action belongs to a user and has a "datetime" external resource.
     * See also: sendToCalendar.
     *
     * @return boolean
     */
    private boolean hasDatetimeExternalResource() {
        if(isUserAction()) {
            UserAction userAction = (UserAction) mAction;
            String externalResource = userAction.getExternalResource();
            String externalResourceName = userAction.getExternalResourceName();

            // ensure the resource name == "datetime" and the resource value exists.
            return externalResourceName.equals("datetime") && !externalResource.isEmpty();
        }
        return false;
    }

    /**
     * If the user is viewing a UserAction whose externalResourceName is "datetime", then the
     * externalResource is a datetime string (of the form YYYY-mm-dd hh:mm:ss). This is likely
     * for a specific scheduled event, so we'll give them an option to add to their Calendar.
     *
     */
    public void sendToCalendar() {
        if(isUserAction() && hasDatetimeExternalResource()) {
            UserAction userAction = (UserAction) mAction;
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
