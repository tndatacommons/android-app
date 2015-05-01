package org.tndata.android.grow.task;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.grow.GrowApplication;
import org.tndata.android.grow.model.Survey;
import org.tndata.android.grow.util.Constants;
import org.tndata.android.grow.util.NetworkHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SurveyResponseTask extends AsyncTask<Survey, Void, Survey> {
    private Context mContext;
    private SurveyResponseListener mCallback;

    public interface SurveyResponseListener {
        public void surveyResponseRecorded(Survey survey);
    }

    public SurveyResponseTask(Context context, SurveyResponseListener callback) {
        mContext = context;
        mCallback = callback;
    }

    @Override
    protected Survey doInBackground(Survey... params) {
        String token = ((GrowApplication) ((Activity) mContext)
                .getApplication()).getToken();

        Survey survey = params[0];
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + token);
        JSONObject body = new JSONObject();
        try {
            body.put("question", survey.getId());
            if (survey.getQuestionType().equalsIgnoreCase(Constants.SURVEY_OPENENDED)) {
                body.put("response", survey.getResponse());
            } else {
                body.put("selected_option", survey.getSelectedOption().getId());
            }
            Log.d("Survey submission", body.toString(2));
            Log.d("Survey url", survey.getResponseUrl());
        } catch (JSONException e1) {
            e1.printStackTrace();
            return null;
        }

        InputStream stream = NetworkHelper.httpPostStream(survey.getResponseUrl(), headers,
                body.toString());
        if (stream == null) {
            return null;
        }
        String result = "";
        String createResponse = "";
        try {

            BufferedReader bReader = new BufferedReader(new InputStreamReader(
                    stream, "UTF-8"));

            String line = null;
            while ((line = bReader.readLine()) != null) {
                result += line;
            }
            bReader.close();

            createResponse = Html.fromHtml(result).toString();

            JSONObject jObject = new JSONObject(createResponse);
            Log.d("user survey response", jObject.toString(2));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return survey;
    }

    @Override
    protected void onPostExecute(Survey survey) {
        mCallback.surveyResponseRecorded(survey);
    }
}
