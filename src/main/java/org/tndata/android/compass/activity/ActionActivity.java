package org.tndata.android.compass.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.service.CompleteActionService;
import org.tndata.android.compass.task.GetUserActionsTask;
import org.tndata.android.compass.util.CompassTagHandler;
import org.tndata.android.compass.util.ImageLoader;

import java.util.ArrayList;


/**
 * Displays an action after clicking a notification and allows the user to report
 * whether they did it or snooze the action.
 *
 * @author Ismael Alonso
 * @version 1.1.0
 */
public class ActionActivity
        extends AppCompatActivity
        implements
                View.OnClickListener,
                GetUserActionsTask.GetUserActionsListener{

    public static final String ACTION_ID_KEY = "action_id";

    //The action in question
    private Action mAction;

    //UI components
    private ImageView mActionImage;
    private TextView mActionTitle;
    private TextView mActionDescription;
    private TextView mMoreInfoHeader;
    private TextView mMoreInfo;
    private ViewSwitcher mTickSwitcher;

    //Firewall
    private boolean mActionComplete;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

        //Set the action to null, this indicates that it has not been fetched
        mAction = null;

        //Fetch UI components
        RelativeLayout circleView = (RelativeLayout)findViewById(R.id.action_circle_view);
        mActionImage = (ImageView)findViewById(R.id.action_image);
        mActionTitle = (TextView)findViewById(R.id.action_title);
        mActionDescription = (TextView)findViewById(R.id.action_description);
        mMoreInfoHeader = (TextView)findViewById(R.id.action_more_info_header);
        mMoreInfo = (TextView)findViewById(R.id.action_more_info);
        mTickSwitcher = (ViewSwitcher)findViewById(R.id.action_tick_switcher);

        //Animate the switcher.
        mTickSwitcher.setInAnimation(this, R.anim.action_switcher_fade_in);
        mTickSwitcher.setOutAnimation(this, R.anim.action_switcher_fade_out);

        //Listeners
        findViewById(R.id.action_later).setOnClickListener(this);
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

        mActionComplete = false;

        fetchAction(getIntent().getIntExtra(ACTION_ID_KEY, -1));
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
        Log.d("ActionActivity", "action: " + actionId);
        CompassApplication application = (CompassApplication)getApplication();
        String token = application.getToken();
        if (token == null || token.isEmpty()){
            // Read from shared preferences instead.
            SharedPreferences settings = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            token = settings.getString("auth_token", "");
        }

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
            case R.id.action_later:
                later();
                break;

            case R.id.action_did_it:
                didIt();
                break;
        }
    }

    /**
     * Later clicked.
     */
    private void later(){
        finish();
    }

    /**
     * I did it clicked.
     */
    private void didIt(){
        if (!mActionComplete){
            mActionComplete = true;
            mTickSwitcher.showNext();
            Intent completeAction = new Intent(this, CompleteActionService.class);
            completeAction.putExtra(CompleteActionService.ACTION_KEY, mAction.getMappingId());
            startService(completeAction);

            //Finish the activity after one second
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    finish();
                }
            }, 1000);
        }
    }

    @Override
    public void actionsLoaded(ArrayList<Action> actions){
        if (actions.size() > 0){
            mAction = actions.get(0);

            //Populate UI
            ImageLoader.loadBitmap(mActionImage, mAction.getIconUrl(), false);
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
        }
    }
}
