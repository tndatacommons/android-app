package org.tndata.android.compass.task;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.BuildConfig;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.model.Survey;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Task used to save a survey response.
 *
 * @author Edited by Ismael Alonso
 * @version 2.0.0
 */
public class SurveyResponseTask extends AsyncTask<Survey, Void, List<Survey>>{
    private static final String TAG = "SurveyResponseTask";


    private Context mContext;
    private SurveyResponseListener mCallback;


    /**
     * Constructor.
     *
     * @param context the context.
     * @param callback the callback object.
     */
    public SurveyResponseTask(@NonNull Context context, @Nullable SurveyResponseListener callback){
        mContext = context;
        mCallback = callback;
    }

    @Override
    protected List<Survey> doInBackground(Survey... params){
        //Retrieve the token. it may be used multiple times
        String token = ((CompassApplication)((Activity)mContext).getApplication()).getToken();

        List<Survey> savedSurveys = new ArrayList<>();
        for (Survey survey:params){
            Log.d(TAG, "Saving survey...");

            //The url is generated. Java can't handle self signed certs cleanly, so
            //  disable SSL in staging
            String url = survey.getResponseUrl();
            if (BuildConfig.DEBUG && url.startsWith("https")){
                url = "http" + url.substring(5);
            }
            Log.d(TAG, "url: " + url);

            //Headers
            Map<String, String> headers = new HashMap<>();
            headers.put("Accept", "application/json");
            headers.put("Content-type", "application/json");
            headers.put("Authorization", "Token " + token);

            try{
                //Create the body
                JSONObject body = new JSONObject();
                body.put("question", survey.getId());
                if (survey.getQuestionType().equalsIgnoreCase(Constants.SURVEY_OPENENDED)){
                    body.put("response", survey.getResponse());
                }
                else{
                    body.put("selected_option", survey.getSelectedOption().getId());
                }
                Log.d(TAG, "Submission:");
                Log.d(TAG, body.toString(2));

                //Create a stream
                InputStream stream = NetworkHelper.httpPostStream(url, headers, body.toString());
                if (stream == null){
                    //In this case, we have more surveys to save, so the whole process
                    //  cannot fail if one survey is bad
                    continue;
                }

                String line, result = "";
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                while ((line = reader.readLine()) != null){
                    result += line;
                }
                reader.close();

                Log.d(TAG, "Response:");
                Log.d(TAG, new JSONObject(result).toString(2));

                savedSurveys.add(survey);
            }
            catch (JSONException|IOException x){
                x.printStackTrace();
            }
        }
        return savedSurveys;
    }

    @Override
    protected void onPostExecute(List<Survey> result){
        if (mCallback != null){
            mCallback.onSurveyResponseRecorded(result);
        }
    }

    /**
     * Callback interface for the SurveyResponseTask class.
     *
     * @author Edited by Ismael Alonso
     * @version 2.0.0
     */
    public interface SurveyResponseListener{
        void onSurveyResponseRecorded(List<Survey> surveys);
    }
}
