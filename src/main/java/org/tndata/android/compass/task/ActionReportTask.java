package org.tndata.android.compass.task;

import android.app.Service;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
 * Worker task that marks a set of actions as complete in the database.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ActionReportTask extends AsyncTask<String, Void, Void>{
    private static final String TAG = "ActionReportTask";


    public final Context mContext;
    public final CompleteActionInterface mInterface;


    /**
     * Constructor.
     *
     * @param context the application context.
     * @param actionInterface the interface to the object containing the action queue.
     */
    public ActionReportTask(Context context, CompleteActionInterface actionInterface){
        mContext = context;
        mInterface = actionInterface;
    }

    @Override
    protected Void doInBackground(String... params){
        while (!mInterface.isQueueEmpty()){
            int actionId = mInterface.dequeueAction();
            String url = Constants.BASE_URL + "users/actions/" + actionId + "/complete/";

            CompassApplication application;
            application= (CompassApplication)((Service)mContext).getApplication();
            String token = application.getToken();

            Map<String, String> headers = new HashMap<>();
            headers.put("Accept", "application/json");
            headers.put("Content-type", "application/json");
            headers.put("Authorization", "Token " + token);

            JSONObject body = new JSONObject();
            try{
                body.put("state", mInterface.dequeueState());
            }
            catch (JSONException e1){
                e1.printStackTrace();
                return null;
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
        return null;
    }

    @Override
    protected void onPostExecute(Void result){
        mInterface.onTaskComplete();
    }


    /**
     * An interface between the CompleteActionTask and the implementing classes. Provides
     * methods to determine whether there is still work to be done, retrieve it and callback
     * methods for when the task is done doing all the work.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface CompleteActionInterface{
        /**
         * Tells whether the action queue is empty.
         *
         * @return true if it is empty, false otherwise.
         */
        boolean isQueueEmpty();

        /**
         * Dequeues an action from the action queue.
         *
         * @return the next action in the queue.
         */
        int dequeueAction();

        /**
         * Dequeues a state from the state queue.
         *
         * @return the next state in the queue.
         */
        String dequeueState();

        /**
         * Callback method called when the task isn't assigned any more work.
         */
        void onTaskComplete();
    }
}
