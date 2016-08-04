package org.tndata.android.compass.service;

import android.app.IntentService;
import android.content.Intent;

import org.json.JSONObject;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Reminder;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.NotificationUtil;

import es.sandwatch.httprequests.HttpRequest;


/**
 * Service that marks actions as complete in the backend.
 *
 * @author Ismael Alonso
 * @version 2.0.1
 */
public class ActionReportService extends IntentService{
    private static final String TAG = "ActionReportService";

    public static final String ACTION_KEY = "org.tndata.compass.ActionReport.Action";
    public static final String STATE_KEY = "org.tndata.compass.ActionReport.State";
    public static final String LENGTH_KEY = "org.tndata.compass.ActionReport.Length";

    public static final String STATE_COMPLETED = "completed";
    public static final String STATE_SNOOZED = "snoozed";
    public static final String STATE_DISMISSED = "dismissed";

    public static final String LENGTH_HOUR = "1h";
    public static final String LENGTH_DAY = "1d";
    public static final String LENGTH_CUSTOM = "custom";
    public static final String LENGTH_LOCATION = "location";


    /**
     * Creates an IntentService. Invoked by your subclass's constructor.
     */
    public ActionReportService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent){
        Action action = intent.getParcelableExtra(ACTION_KEY);
        Reminder reminder = intent.getParcelableExtra(NotificationUtil.REMINDER_KEY);

        String notificationTag;
        long actionId;
        String url;
        if (reminder != null){
            url = API.URL.postActionReport(reminder);
            if (reminder.isUserAction()){
                notificationTag = NotificationUtil.USER_ACTION_TAG;
                actionId = reminder.getUserMappingId();
            }
            else{
                notificationTag = NotificationUtil.CUSTOM_ACTION_TAG;
                actionId = reminder.getObjectId();
            }
        }
        else{
            actionId = action.getId();
            url = API.URL.postActionReport(action);
            if (action instanceof UserAction){
                notificationTag = NotificationUtil.USER_ACTION_TAG;
            }
            else{
                notificationTag = NotificationUtil.CUSTOM_ACTION_TAG;
            }
        }

        NotificationUtil.cancel(this, notificationTag, actionId);

        JSONObject body;
        String state = intent.getStringExtra(STATE_KEY);
        body = API.BODY.postActionReport(state, intent.getStringExtra(LENGTH_KEY));

        HttpRequest.post(null, url, body);
    }
}
