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
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.CustomAction;
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
 * @version 1.2.1
 */
public class ActionActivity
        extends MaterialActivity
        implements
                ActionAdapter.ActionAdapterListener,
                HttpRequest.RequestCallback,
                Parser.ParserCallback{

    public static final String UPCOMING_ACTION_KEY = "org.tndata.compass.ActionActivity.Upcoming";
    public static final String ACTION_KEY = "org.tndata.compass.ActionActivity.Action";
    public static final String REMINDER_KEY = "org.tndata.compass.ActionActivity.Reminder";

    public static final String DID_IT_KEY = "org.tndata.compass.ActionActivity.DidIt";

    private static final int SNOOZE_REQUEST_CODE = 61428;
    private static final int RESCHEDULE_REQUEST_CODE = 61429;


    //The action in question and the associated reminder
    private Action mAction;
    private CategoryContent mCategory;
    private UpcomingAction mUpcomingAction;
    private Reminder mReminder;

    private ActionAdapter mAdapter;

    private int mGetActionRC;
    private int mGetCategoryRC;

    //Firewall
    private boolean mActionUpdated;


    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Retrieve the action and mark the reminder and upcoming action as nonexistent
        mAction = (Action)getIntent().getSerializableExtra(ACTION_KEY);
        mUpcomingAction = null;
        mReminder = null;

        //If the action exists do some initial setup and fetching
        if (mAction != null){
            if (mAction instanceof UserAction){
                UserAction userAction = (UserAction)mAction;
                if (userAction.getPrimaryCategory() != null){
                    mCategory = userAction.getPrimaryCategory().getCategory();
                    setColor(Color.parseColor(mCategory.getColor()));
                    setHeader();
                }
                else{
                    setColor(getResources().getColor(R.color.grow_primary));
                    fetchCategory(userAction);
                }
            }
            else{
                setColor(getResources().getColor(R.color.grow_primary));
                setHeader();
            }
        }
        //Create and set the adapter
        mAdapter = new ActionAdapter(this, this, mAction, mCategory);
        setAdapter(mAdapter);

        mActionUpdated = false;

        //If the action wasn't provided via the intent it needs to be fetched
        if (mAction == null){
            mUpcomingAction = getIntent().getParcelableExtra(UPCOMING_ACTION_KEY);
            mReminder = (Reminder)getIntent().getSerializableExtra(REMINDER_KEY);
            setColor(getResources().getColor(R.color.grow_primary));
            fetchAction();
        }
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

    private int getActionId(){
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
        else if (result instanceof CategoryContent){
            mCategory = (CategoryContent)result;
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof UserAction){
            mAdapter.setAction(mAction, null);
            fetchCategory((UserAction)mAction);
            invalidateOptionsMenu();
        }
        else if (result instanceof CustomAction){
            mAction = (Action)result;
            setHeader();
            mAdapter.setAction(mAction, null);
            invalidateOptionsMenu();
        }
        else if (result instanceof CategoryContent){
            setColor(Color.parseColor(mCategory.getColor()));
            setHeader();
            mAdapter.setCategory(mCategory);
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
        return true;
    }

    @Override
    public boolean menuItemSelected(MenuItem item){
        switch (item.getItemId()){
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
    public void onIDidItClick(){
        if (mAction != null && !mActionUpdated){
            mActionUpdated = true;

            startService(new Intent(this, ActionReportService.class)
                    .putExtra(ActionReportService.ACTION_KEY, mAction)
                    .putExtra(ActionReportService.STATE_KEY, ActionReportService.STATE_COMPLETED));

            setResult(RESULT_OK, new Intent().putExtra(DID_IT_KEY, true));
            finish();
        }
    }

    @Override
    public void onRescheduleClick(){
        if (mAction != null && !mActionUpdated){
            Intent reschedule = new Intent(this, TriggerActivity.class)
                    .putExtra(TriggerActivity.ACTION_KEY, mAction)
                    .putExtra(TriggerActivity.GOAL_KEY, mAction.getGoal());
            startActivityForResult(reschedule, RESCHEDULE_REQUEST_CODE);
        }
    }

    @Override
    public void onSnoozeClick(){
        if (mAction != null && !mActionUpdated){
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
}
