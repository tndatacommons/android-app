package org.tndata.android.compass.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.util.ImageLoader;


/**
 * Displays an action after clicking a notification and allows the user to report
 * whether they did it or to cancel or snooze the action.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ActionActivity extends ActionBarActivity implements View.OnClickListener{
    //The action in question
    private Action mAction;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //TODO fetch the action (download?)
        mAction = new Action();

        //Fetch UI components
        ImageView actionImage = (ImageView)findViewById(R.id.action_image);
        TextView actionTitle = (TextView)findViewById(R.id.action_title);
        TextView actionDescription = (TextView)findViewById(R.id.action_description);

        //Populate UI
        //ImageLoader.loadBitmap(actionImage, mAction.getIconUrl(), false);
        actionTitle.setText(mAction.getTitle());
        actionDescription.setText(mAction.getDescription());

        //Listeners
        findViewById(R.id.action_later).setOnClickListener(this);
        findViewById(R.id.cancel_button).setOnClickListener(this);
        findViewById(R.id.action_did_it).setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.action_later:
                later();
                break;

            case R.id.action_cancel:
                cancel();
                break;

            case R.id.action_did_it:
                didIt();
                break;
        }
    }

    private void later(){

    }

    private void cancel(){

    }

    private void didIt(){

    }
}
