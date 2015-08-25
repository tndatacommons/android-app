package org.tndata.android.compass.task;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * Task used to snooze a notification.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class SnoozeTask extends AsyncTask<Integer, Void, Void>{
    CompassApplication mApplication;

    /**
     * Constructor.
     *
     * @param application reference to the application class.
     */
    public SnoozeTask(CompassApplication application){
        mApplication = application;
    }

    @Override
    protected Void doInBackground(Integer... params){
        for (int notificationId:params){
            Map<String, String> headers = new HashMap<>();
            headers.put("Accept", "application/json");
            headers.put("Content-type", "application/json");
            headers.put("Authorization", "Token " + mApplication.getToken());

            String url = Constants.BASE_URL + "notifications/" + notificationId + "/";
            JSONObject body = new JSONObject();
            try{
                body.put("snooze", "1");
            }
            catch (JSONException jx){
                jx.printStackTrace();
                return null;
            }

            InputStream stream = NetworkHelper.httpPutStream(url, headers, body.toString());
            if (stream != null){
                try{
                    stream.close();
                }
                catch (IOException iox){
                    iox.printStackTrace();
                }
            }
        }
        return null;
    }
}
