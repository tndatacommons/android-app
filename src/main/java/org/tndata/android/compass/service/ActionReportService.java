package org.tndata.android.compass.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.model.Reminder;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;
import org.tndata.android.compass.util.NotificationUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * Service that marks actions as complete in the backend.
 *
 * @author Ismael Alonso
 * @version 2.0.1
 */
public class ActionReportService extends IntentService{
    private static final String TAG = "ActionReportService";

    public static final String ACTION_ID_KEY = "org.tndata.compass.CompleteAction.ActionId";
    public static final String MAPPING_ID_KEY = "org.tndata.compass.CompleteAction.MappingId";
    public static final String STATE_KEY = "org.tndata.compass.CompleteAction.State";
    public static final String LENGTH_KEY = "org.tndata.compass.CompleteAction.Length";

    public static final String STATE_COMPLETED = "completed";
    public static final String STATE_UNCOMPLETED = "uncompleted";
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
        int mappingId = intent.getIntExtra(MAPPING_ID_KEY, -1);
        int actionId = intent.getIntExtra(ACTION_ID_KEY, -1);
        Reminder reminder = (Reminder)intent.getSerializableExtra(NotificationUtil.REMINDER_KEY);

        if (reminder != null){
            mappingId = reminder.getUserMappingId();
            actionId = reminder.getObjectId();
        }

        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(NotificationUtil.NOTIFICATION_TYPE_ACTION_TAG, actionId);

        String url = Constants.BASE_URL + "users/actions/" + mappingId + "/complete/";

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + ((CompassApplication)getApplication()).getToken());

        JSONObject body = new JSONObject();
        try{
            String state = intent.getStringExtra(STATE_KEY);
            body.put("state", state);
            if (state.equals(STATE_SNOOZED)){
                body.put("length", intent.getStringExtra(LENGTH_KEY));
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        Log.d(TAG, body.toString());

        //Post to the URL with the given headers and an empty body object
        InputStream stream = NetworkHelper.httpPostStream(url, headers, body.toString());
        if (stream != null){
            try{
                stream.close();
            }
            catch (IOException iox){
                iox.printStackTrace();
            }
        }
    }
}
