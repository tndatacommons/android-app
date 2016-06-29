package org.tndata.android.compass.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.model.GcmMessage;
import org.tndata.android.compass.parser.ParserMethods;
import org.tndata.android.compass.receiver.GcmBroadcastReceiver;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.NotificationUtil;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * <p/>
 * NOTE: Messages received from GCM will have a format like the following:
 * <p/>
 * {
 * "title":"Demo"
 * "message":"Don't forget to review your Notifications for today",
 * "object_id":32,
 * "object_type":"action",  // an Action
 * }
 */
public class GcmIntentService extends IntentService{
    private static final String TAG = "GcmIntentService";

    public static final String FROM_GCM_KEY = "org.tndata.Compass.GcmIntentService.FromGcm";
    public static final String MESSAGE_KEY = "org.tndata.Compass.GcmIntentService.Message";

    public static final String MESSAGE_TYPE_ACTION = "action";
    public static final String MESSAGE_TYPE_CUSTOM_ACTION = "customaction";
    public static final String MESSAGE_TYPE_ENROLLMENT = "package enrollment";


    public GcmIntentService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent){
        boolean isFromGcm = intent.getBooleanExtra(FROM_GCM_KEY, false);
        String gcmMessage = intent.getStringExtra(MESSAGE_KEY);

        //IntentServices are executed in the background, so it is safe to do this
        GcmMessage message = ParserMethods.sGson.fromJson(gcmMessage, GcmMessage.class);
        message.setGcmMessage(gcmMessage);
        NotificationUtil.generateNotification(this, message);

                    try{
                        JSONObject jsonObject = new JSONObject(gcmMessage);
                        if (jsonObject.optBoolean("production") == !API.STAGING){
                            sendNotification(
                                    jsonObject.optString("id"),
                                    jsonObject.optString("message"),
                                    jsonObject.optString("title"),
                                    jsonObject.optString("object_type"),
                                    jsonObject.optString("object_id"),
                                    jsonObject.optString("user_mapping_id")
                            );
                        }
                    }
                    catch (JSONException jsonx){
                        jsonx.printStackTrace();
                    }

        if (isFromGcm){
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    // Put the message into a notification and post it.
    private void sendNotification(String id, String msg, String title, String objectType,
                                  String objectId, String mappingId){

        Log.d(TAG, "object_type = " + objectType);
        Log.d(TAG, "object_id = " + objectId);

        switch (objectType.toLowerCase()){
            case MESSAGE_TYPE_ACTION:
            case MESSAGE_TYPE_CUSTOM_ACTION:
                try{
                    NotificationUtil.putActionNotification(this,Integer.valueOf(id), title, msg,
                            Integer.valueOf(objectId), Integer.valueOf(mappingId));
                }
                catch (NumberFormatException nfx){
                    nfx.printStackTrace();
                }
                break;

            case MESSAGE_TYPE_ENROLLMENT:
                try{
                    NotificationUtil.putEnrollmentNotification(this, Integer.valueOf(objectId),
                            title, msg);
                }
                catch (NumberFormatException nfx){
                    nfx.printStackTrace();
                }
                break;
        }
    }
}
