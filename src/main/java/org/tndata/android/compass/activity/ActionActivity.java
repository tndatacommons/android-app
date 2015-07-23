package org.tndata.android.compass.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;

import org.tndata.android.compass.model.Action;


/**
 * Displays an action after clicking a notification and allows the user to report
 * whether they did it or to cancel or snooze the action.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ActionActivity extends ActionBarActivity{
    //The action in question
    private Action mAction;

    //UI components
    private ImageView mActionImage;
    private TextView mActionTitle;
    private TextView mActionDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
}
