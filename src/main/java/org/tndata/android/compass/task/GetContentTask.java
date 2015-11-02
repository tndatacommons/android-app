package org.tndata.android.compass.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import org.tndata.android.compass.util.NetworkHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


/**
 * Task to retrieve generic content from the API. This is a test and a proof of concept.
 * The structure of the parameter vararg is the following:
 *     -Url
 *     -Token
 *
 * @author Ismael Alonso
 * @version 0.1 (B)
 */
public class GetContentTask extends AsyncTask<String, Void, Boolean>{
    private static final String TAG = "GetContentTask";


    private GetContentListener mListener;
    private int mRequestCode;


    public GetContentTask(@NonNull GetContentListener listener, int requestCode){
        mListener = listener;
        mRequestCode = requestCode;
    }

    @Override
    protected Boolean doInBackground(String... params){
        if (params.length < 2){
            Log.e(TAG, "Wrong parameter format");
            return false;
        }

        //Retrieve the arguments
        String url = params[0];
        String token = params[1];

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + token);

        InputStream stream = NetworkHelper.httpGetStream(url, headers);
        if (stream == null){
            Log.e(TAG, "Bad stream");
            return false;
        }

        try{
            String line, result = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            while ((line = reader.readLine()) != null){
                result += line;
            }
            reader.close();

            mListener.onContentRetrieved(mRequestCode, result);
            return true;
        }
        catch (IOException iox){
            iox.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean success){
        if (success){
            mListener.onRequestComplete(mRequestCode);
        }
        else{
            mListener.onRequestFailed(mRequestCode);
        }
    }


    public interface GetContentListener{
        void onContentRetrieved(int requestCode, String content);
        void onRequestComplete(int requestCode);
        void onRequestFailed(int requestCode);
    }
}
