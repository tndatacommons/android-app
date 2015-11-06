package org.tndata.android.compass.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.service.ActionReportService;
import org.tndata.android.compass.model.Reminder;
import org.tndata.android.compass.task.GetUserActionsTask;
import org.tndata.android.compass.util.CompassTagHandler;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.ImageLoader;

import java.util.List;


/**
 * Displays an action after clicking a notification and allows the user to report
 * whether they did it or snooze the action.
 *
 * @author Ismael Alonso
 * @version 1.2.0
 */
public class ActionActivity
        extends AppCompatActivity
        implements
                View.OnClickListener,
                GetUserActionsTask.GetUserActionsCallback{

    public static final String ACTION_KEY = "org.tndata.compass.ActionActivity.action";
    public static final String ACTION_ID_KEY = "org.tndata.compass.ActionActivity.action_id";
    public static final String REMINDER_KEY = "org.tndata.compass.ActionActivity.reminder";

    public static final String DID_IT_KEY = "org.tndata.compass.ActionActivity.did_it";

    private static final int REQUEST_CODE = 61428;


    //The action in question and the associated reminder
    private Action mAction;
    private Reminder mReminder;

    //UI components
    private FrameLayout mHeroContainer;
    private ImageView mActionImage;
    private TextView mActionTitle;
    private TextView mActionDescription;
    private TextView mMoreInfoHeader;
    private TextView mMoreInfo;
    private TextView mDoItNow;
    private ViewSwitcher mTickSwitcher;

    //Firewall
    private boolean mActionUpdated;


    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

        //Retrieve the action and mark the reminder as nonexistent
        mAction = (Action)getIntent().getSerializableExtra(ACTION_KEY);
        mReminder = null;

        //Fetch UI components
        mHeroContainer = (FrameLayout)findViewById(R.id.action_hero_container);
        RelativeLayout circleView = (RelativeLayout)findViewById(R.id.action_circle_view);
        mActionImage = (ImageView)findViewById(R.id.action_image);
        mActionTitle = (TextView)findViewById(R.id.action_title);
        mActionDescription = (TextView)findViewById(R.id.action_description);
        mMoreInfoHeader = (TextView)findViewById(R.id.action_more_info_header);
        mMoreInfo = (TextView)findViewById(R.id.action_more_info);
        TextView timeOption = (TextView)findViewById(R.id.action_time_option);
        mTickSwitcher = (ViewSwitcher)findViewById(R.id.action_tick_switcher);
        mDoItNow = (TextView)findViewById(R.id.action_do_it_now);

        mHeroContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
            @Override
            @SuppressWarnings("deprecation")
            public void onGlobalLayout(){
                int width = mHeroContainer.getWidth();
                mHeroContainer.getLayoutParams().height = (width * 2) / 3;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
                    mHeroContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                else{
                    mHeroContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        //Animate the switcher.
        mTickSwitcher.setInAnimation(this, R.anim.action_switcher_fade_in);
        mTickSwitcher.setOutAnimation(this, R.anim.action_switcher_fade_out);

        //Listeners
        timeOption.setOnClickListener(this);
        findViewById(R.id.action_did_it).setOnClickListener(this);

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
            timeOption.setText(R.string.action_snooze);
            mReminder = (Reminder)getIntent().getSerializableExtra(REMINDER_KEY);
            fetchAction(getIntent().getIntExtra(ACTION_ID_KEY, -1));
        }
        else{
            timeOption.setText(R.string.action_reschedule);
            populateUI();
        }
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        Log.d("ActionActivity", "onNewIntent");
        fetchAction(getIntent().getIntExtra(ACTION_ID_KEY, -1));
    }

    /**
     * Retrieves an action from an id.
     *
     * @param actionId the id of the action to be fetched.
     */
    private void fetchAction(int actionId){
        Log.d("ActionActivity", "fetching action: " + actionId);
        String token = ((CompassApplication)getApplication()).getToken();

        if (token != null && !token.isEmpty()){
            new GetUserActionsTask(this).execute(token, "action:" + actionId);
        }
        else{
            //Something is wrong and we don't have an auth token for the user, so fail.
            Log.e("ActionActivity", "AUTH Token is null, giving up!");
            finish();
        }
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
                CompassUtil.doItNow(this, mAction.getExternalResource());
                break;
        }
    }

    /**
     * Snooze clicked. This opens the snooze menu.
     */
    private void snooze(){
        if (mAction != null && !mActionUpdated){
            Intent snoozeIntent = new Intent(this, SnoozeActivity.class)
                    .putExtra(SnoozeActivity.REMINDER_KEY, mReminder);
            startActivityForResult(snoozeIntent, REQUEST_CODE);
        }
    }

    /**
     * Reschedule clicked. This opens the trigger picker.
     */
    private void reschedule(){
        if (mAction != null && !mActionUpdated){
            Intent reschedule = new Intent(this, TriggerActivity.class)
                    .putExtra("action", mAction)
                    .putExtra("goal", mAction.getPrimaryGoal());
            startActivityForResult(reschedule, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_CODE){
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
        if (mAction!= null && !mActionUpdated){
            mActionUpdated = true;

            Intent completeAction = new Intent(this, ActionReportService.class)
                    .putExtra(ActionReportService.ACTION_MAPPING_ID_KEY, mAction.getMappingId())
                    .putExtra(ActionReportService.STATE_KEY, ActionReportService.STATE_COMPLETED);
            startService(completeAction);

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
    public void onActionsLoaded(List<Action> actions){
        if (actions.size() > 0){
            mAction = actions.get(0);
            populateUI();
        }
        else{
            //If the request did not return any actions, kill the activity
            finish();
        }
    }

    /**
     * Takes the action's parameters and populates the UI with them.
     */
    private void populateUI(){
        ImageLoader.loadBitmap(mActionImage, mAction.getIconUrl(), new ImageLoader.Options());
        mActionTitle.setText(mAction.getTitle());
        if (!mAction.getHTMLDescription().isEmpty()){
            mActionDescription.setText(Html.fromHtml(mAction.getHTMLDescription(), null, new CompassTagHandler(this)));
        }
        else{
            mActionDescription.setText(mAction.getDescription());
        }
        mActionDescription.setText(mAction.getDescription());

        if (!mAction.getMoreInfo().isEmpty()){
            mMoreInfoHeader.setVisibility(View.VISIBLE);
            mMoreInfo.setVisibility(View.VISIBLE);
            if (!mAction.getHTMLMoreInfo().isEmpty()){
                mMoreInfo.setText(Html.fromHtml(mAction.getHTMLMoreInfo(), null, new CompassTagHandler(this)));
            }
            else{
                mMoreInfo.setText(mAction.getMoreInfo());
            }
        }

        if (!mAction.getExternalResource().isEmpty()){
            mDoItNow.setVisibility(View.VISIBLE);
            mDoItNow.setOnClickListener(this);
        }
    }
}
