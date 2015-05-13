package org.tndata.android.grow.task;

import android.os.AsyncTask;
import android.text.Html;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.tndata.android.grow.model.Survey;
import org.tndata.android.grow.util.Constants;
import org.tndata.android.grow.util.NetworkHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


public class SurveyFinderTask extends AsyncTask<String, Void, Survey> {
    private SurveyFinderInterface mCallback;
    private static Gson gson = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.IDENTITY).create();

    public interface SurveyFinderInterface {
        public void surveyFound(Survey survey);
    }

    public SurveyFinderTask(SurveyFinderInterface callback) {
        mCallback = callback;
    }

    @Override
    protected Survey doInBackground(String... params) {
        String token = params[0];
        String url = Constants.BASE_URL + "survey/";
        if (params.length > 1) {
            url += params[1]; // will add a not-so-random survey
        }
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + token);
        InputStream stream = NetworkHelper.httpGetStream(url, headers);
        if (stream == null) {
            return null;
        }
        String result = "";
        String surveyResponse;
        try {

            BufferedReader bReader = new BufferedReader(new InputStreamReader(
                    stream, "UTF-8"));

            String line;
            while ((line = bReader.readLine()) != null) {
                result += line;
            }
            bReader.close();

            surveyResponse = Html.fromHtml(result).toString();

            return gson.fromJson(surveyResponse, Survey.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Survey survey) {
        if(survey != null) {
            mCallback.surveyFound(survey);
        }
    }
}
