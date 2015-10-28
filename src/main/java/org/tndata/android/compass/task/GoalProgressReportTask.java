package org.tndata.android.compass.task;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * Task to report progress on goals to the API through the daily check in.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class GoalProgressReportTask extends AsyncTask<GoalProgressReportTask.GoalProgress, Void, Void>{
    private GoalProgressReportCallback mCallback;
    private String mToken;

    private boolean mSuccess[];


    /**
     * Constructor.
     *
     * @param token the user token.
     */
    public GoalProgressReportTask(String token){
        this(null, token);
    }

    /**
     * Constructor.
     *
     * @param callback the callback object.
     * @param token the user token.
     */
    public GoalProgressReportTask(@Nullable GoalProgressReportCallback callback, String token){
        mCallback = callback;
        mToken = token;
    }

    @Override
    protected Void doInBackground(GoalProgress... params){
        String url = Constants.BASE_URL + "users/goals/progress/";

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + mToken);

        mSuccess = new boolean[params.length];
        int i = -1;
        for (GoalProgress goalProgress:params){
            i++;
            JSONObject body = new JSONObject();
            try{
                body.put("goal", goalProgress.mId);
                body.put("daily_checkin", goalProgress.mProgress);
            }
            catch (JSONException jx){
                jx.printStackTrace();
                mSuccess[i] = false;
            }

            InputStream stream = NetworkHelper.httpPostStream(url, headers, body.toString());
            if (stream != null){
                try{
                    stream.close();
                }
                catch (IOException iox){
                    iox.printStackTrace();
                }
            }
            mSuccess[i] = stream!=null;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result){
        if (mCallback != null){
            mCallback.onGoalReported(mSuccess);
        }
    }


    /**
     * Data holder class for a goal progress.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public class GoalProgress{
        private final int mId;
        private final int mProgress;


        /**
         * Constructor.
         *
         * @param id the ID of the goal to be reported.
         * @param progress the progress as reported by the user.
         */
        public GoalProgress(int id, int progress){
            mId = id;
            mProgress = progress;
        }
    }


    /**
     * Callback interface for the task.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface GoalProgressReportCallback{
        /**
         * Called when the task is done reporting all of the delivered goals.
         *
         * @param success an array containing information about which of the provided goals
         *                were reported successfully.
         */
        void onGoalReported(boolean[] success);
    }
}
