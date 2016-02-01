package org.tndata.android.compass.activity;

import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.ActionContent;
import org.tndata.android.compass.model.CustomAction;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.parser.ContentParser;
import org.tndata.android.compass.service.ActionReportService;
import org.tndata.android.compass.model.Reminder;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.CompassTagHandler;
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
        extends AppCompatActivity
        implements
                View.OnClickListener,
                NetworkRequest.RequestCallback{

    public static final String ACTION_KEY = "org.tndata.compass.ActionActivity.Action";
    public static final String ACTION_ID_KEY = "org.tndata.compass.ActionActivity.ActionId";
    public static final String REMINDER_KEY = "org.tndata.compass.ActionActivity.Reminder";

    public static final String DID_IT_KEY = "org.tndata.compass.ActionActivity.DidIt";

    private static final int SNOOZE_REQUEST_CODE = 61428;
    private static final int RESCHEDULE_REQUEST_CODE = 61429;


    private CompassApplication mApplication;

    //The action in question and the associated reminder
    private Action mAction;
    private Reminder mReminder;

    //UI components
    private ImageView mActionImage;
    private TextView mActionTitle;
    private TextView mActionDescription;
    private TextView mMoreInfoHeader;
    private TextView mMoreInfo;
    private View mButtonWrapper;
    private TextView mDidIt;
    private TextView mDoItNow;
    private ViewSwitcher mTickSwitcher;

    //Firewall
    private boolean mActionNeededFetching;
    private boolean mActionUpdated;


    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

        mApplication = (CompassApplication)getApplication();

        //Retrieve the action and mark the reminder as nonexistent
        mAction = (UserAction)getIntent().getSerializableExtra(ACTION_KEY);
        mReminder = null;

        //Set up the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.action_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //Fetch UI components
        FrameLayout heroContainer = (FrameLayout)findViewById(R.id.action_hero_container);
        RelativeLayout circleView = (RelativeLayout)findViewById(R.id.action_circle_view);
        mActionImage = (ImageView)findViewById(R.id.action_image);
        mActionTitle = (TextView)findViewById(R.id.action_title);
        mActionDescription = (TextView)findViewById(R.id.action_description);
        mMoreInfoHeader = (TextView)findViewById(R.id.action_more_info_header);
        mMoreInfo = (TextView)findViewById(R.id.action_more_info);
        TextView timeOption = (TextView)findViewById(R.id.action_time_option);
        mTickSwitcher = (ViewSwitcher)findViewById(R.id.action_tick_switcher);
        mButtonWrapper = findViewById(R.id.action_button_wrapper);
        mDidIt = (TextView)findViewById(R.id.action_did_it);
        mDoItNow = (TextView)findViewById(R.id.action_do_it_now);

        heroContainer.getLayoutParams().height = CompassUtil.getScreenWidth(this)*2/3;

        //Animate the switcher.
        mTickSwitcher.setInAnimation(this, R.anim.action_switcher_fade_in);
        mTickSwitcher.setOutAnimation(this, R.anim.action_switcher_fade_out);

        //Listeners
        timeOption.setOnClickListener(this);
        mDidIt.setOnClickListener(this);

        //Circle view
        GradientDrawable gradientDrawable = (GradientDrawable)circleView.getBackground();
        gradientDrawable.setColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            circleView.setBackground(gradientDrawable);
        }
        else{
            circleView.setBackgroundDrawable(gradientDrawable);
        }

        mActionUpdated = false;

        //If the action wasn't provided via the intent it needs to be fetched
        if (mAction == null){
            mActionNeededFetching = true;
            timeOption.setText(R.string.action_snooze);
            mReminder = (Reminder)getIntent().getSerializableExtra(REMINDER_KEY);
            fetchAction();
        }
        else{
            mActionNeededFetching = false;
            timeOption.setText(R.string.action_reschedule);
            if (mAction instanceof UserAction){
                populateUI((UserAction)mAction);
            }
            else if (mAction instanceof CustomAction){
                populateUI((CustomAction)mAction);
            }
        }
    }

    /**
     * Retrieves an action from the API
     */
    private void fetchAction(){
        if (mReminder.getObjectTypeId() == Reminder.TYPE_USER_ACTION_ID){
            int mappingId = mReminder.getUserMappingId();
            Log.d("ActionActivity", "Fetching UserAction: " + mappingId);
            NetworkRequest.get(this, this, API.getActionUrl(mappingId), mApplication.getToken());
        }
        else if (mReminder.getObjectTypeId() == Reminder.TYPE_CUSTOM_ACTION_ID){
            int customId = mReminder.getObjectId();
            Log.d("ActionActivity", "Fetching UserAction: " + customId);
            NetworkRequest.get(this, this, API.getCustomActionUrl(customId), mApplication.getToken());
        }
    }

    /**
     * Takes the action's parameters and populates the UI with them.
     */
    private void populateUI(UserAction userAction){
        ImageLoader.loadBitmap(mActionImage, userAction.getIconUrl(), new ImageLoader.Options());
        mActionTitle.setText(mAction.getTitle());
        if (!userAction.getHTMLDescription().isEmpty()){
            mActionDescription.setText(Html.fromHtml(userAction.getHTMLDescription(), null,
                    new CompassTagHandler(this)));
        }
        else{
            mActionDescription.setText(userAction.getDescription());
        }
        mActionDescription.setText(userAction.getDescription());

        ActionContent action = userAction.getAction();
        if (!action.getMoreInfo().isEmpty()){
            mMoreInfoHeader.setVisibility(View.VISIBLE);
            mMoreInfo.setVisibility(View.VISIBLE);
            if (!action.getHTMLMoreInfo().isEmpty()){
                mMoreInfo.setText(Html.fromHtml(action.getHTMLMoreInfo(), null,
                        new CompassTagHandler(this)));
            }
            else{
                mMoreInfo.setText(action.getMoreInfo());
            }
        }

        mButtonWrapper.setVisibility(View.VISIBLE);
        if (!action.getExternalResource().isEmpty()){
            mDoItNow.setOnClickListener(this);
            if (action.getExternalResourceName().isEmpty() ||
                    action.getExternalResourceName().length() > 12){
                mDoItNow.setText(R.string.action_do_it_now);
            }
            else{
                mDoItNow.setText(action.getExternalResourceName());
            }
        }
        else{
            if (mActionNeededFetching){
                ViewGroup.LayoutParams params = mDoItNow.getLayoutParams();
                params.width = mDidIt.getWidth();
                mDoItNow.setLayoutParams(params);
            }
            else{
                mDidIt.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
                    @Override
                    @SuppressWarnings("deprecation")
                    public void onGlobalLayout(){
                        ViewGroup.LayoutParams params = mDoItNow.getLayoutParams();
                        params.width = mDidIt.getWidth();
                        mDoItNow.setLayoutParams(params);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
                            mDidIt.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        else{
                            mDidIt.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                });
            }
        }
    }

    private void populateUI(CustomAction customAction){
        mActionTitle.setText(customAction.getTitle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //We need to check for null action here because sometimes the action needs to
        //  be fetched from the backend. If the action has not been fetched yet, the
        //  overflow button doesn't make sense
        if (mActionNeededFetching && mAction != null && mAction.isEditable()){
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
    public boolean onOptionsItemSelected(MenuItem item){
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
                case RESCHEDULE_REQUEST_CODE:
                    //If the activity was was fired from an action notification and the
                    //  associated action was rescheduled, the notification needs to be
                    //  dismissed
                    if (mActionNeededFetching){
                        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE))
                                .cancel(NotificationUtil.USER_ACTION_TAG,
                                        mReminder.getObjectId());
                    }

                //In either case, the activity should finish after a second
                case SNOOZE_REQUEST_CODE:
                    mActionUpdated = true;

                    //Display the check mark and finish the activity after one second
                    mTickSwitcher.showNext();
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run(){
                            setResult(RESULT_OK, new Intent().putExtra(DID_IT_KEY, false));
                            finish();
                        }
                    }, 1000);
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

            //Display the check mark and finish the activity after one second
            mTickSwitcher.showNext();
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    setResult(RESULT_OK, new Intent().putExtra(DID_IT_KEY, true));
                    finish();
                }
            }, 1000);
        }
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        mAction = ContentParser.parseUserAction(result);
        populateUI((UserAction)mAction);
        invalidateOptionsMenu();
    }

    @Override
    public void onRequestFailed(int requestCode, String message){
        finish();
    }
}
