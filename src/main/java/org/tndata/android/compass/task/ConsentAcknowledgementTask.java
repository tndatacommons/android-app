package org.tndata.android.compass.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * Task to acknowledge and accept a package consent.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ConsentAcknowledgementTask extends AsyncTask<Integer, Void, Boolean>{
    private static final String TAG = "ConsentAckTask";


    private String mToken;
    private ConsentAcknowledgementCallback mCallback;


    /**
     * Constructor.
     *
     * @param token the user's auth token.
     * @param callback the acknowledgement callback.
     */
    public ConsentAcknowledgementTask(@NonNull String token, @Nullable ConsentAcknowledgementCallback callback){
        mToken = token;
        mCallback = callback;
    }

    @Override
    protected Boolean doInBackground(Integer... params){
        //If there is nothing to ACK, finish
        if (params.length == 0){
            return false;
        }

        //Set the url
        String url = Constants.BASE_URL + "users/packages/" + params[0] + "/";

        //Create the headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + mToken);

        try{
            JSONObject body = new JSONObject();
            body.put("accepted", true);

            //Create a stream, if that fails, return false to signal failure
            InputStream stream = NetworkHelper.httpPutStream(url, headers, body.toString());
            if (stream != null){
                try{
                    stream.close();
                }
                catch (IOException iox){
                    iox.printStackTrace();
                }
                return true;
            }
            else{
                Log.d(TAG, "bad stream");
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean success){
        if (mCallback != null){
            if (success){
                mCallback.onAcknowledgementSuccessful();
            }
            else{
                mCallback.onAcknowledgementFailed();
            }
        }
    }


    /**
     * Callback interface for consent acknowledgement events.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface ConsentAcknowledgementCallback{
        /**
         * Called if the acknowledgement was successful.
         */
        void onAcknowledgementSuccessful();

        /**
         * Called if the acknowledgement failed.
         */
        void onAcknowledgementFailed();
    }
}
