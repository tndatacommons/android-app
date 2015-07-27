package org.tndata.android.compass.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.task.GetUserActionsTask;
import org.tndata.android.compass.util.ImageLoader;

import java.util.ArrayList;


/**
 * Displays an action after clicking a notification and allows the user to report
 * whether they did it or to cancel or snooze the action.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ActionActivity
        extends ActionBarActivity
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

        int actionId = getIntent().getIntExtra(ACTION_ID_KEY, -1);
        Log.d("ActionActivity", "action: " + actionId);
        new GetUserActionsTask(this).execute(((CompassApplication)getApplication()).getToken(),
                "action:" + actionId);
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        Log.d("ActionActivity", "onNewIntent");
        int actionId = getIntent().getIntExtra(ACTION_ID_KEY, -1);
        Log.d("ActionActivity", "action: " + actionId);
        new GetUserActionsTask(this).execute(((CompassApplication)getApplication()).getToken(),
                "action:" + actionId);
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

    private void later(){
        finish();
    }

    private void didIt(){
        //TODO when api supports it
    }

    @Override
    public void actionsLoaded(ArrayList<Action> actions){
        if (actions.size() > 0){
            mAction = actions.get(0);

            //Populate UI
            ImageLoader.loadBitmap(mActionImage, mAction.getIconUrl(), false);
            mActionTitle.setText(mAction.getTitle());
            mActionDescription.setText(mAction.getDescription());
        }
    }
}
