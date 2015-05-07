package org.tndata.android.grow.task;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tndata.android.grow.model.Survey;
import org.tndata.android.grow.model.SurveyOptions;
import org.tndata.android.grow.util.Constants;
import org.tndata.android.grow.util.NetworkHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GetUserProfileTask extends AsyncTask<String, Void, ArrayList<Survey>> {
    private UserProfileTaskInterface mCallback;

    public interface UserProfileTaskInterface {
        public void userProfileFound(ArrayList<Survey> surveys);
    }

    public GetUserProfileTask(UserProfileTaskInterface callback) {
        mCallback = callback;
    }

    @Override
    protected ArrayList<Survey> doInBackground(String... params) {
        String token = params[0];
        String url = Constants.BASE_URL + "userprofiles/";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + token);
        InputStream stream = NetworkHelper.httpGetStream(url, headers);
        if (stream == null) {
            return null;
        }
        String result = "";
        String profileResponse = "";
        try {

            BufferedReader bReader = new BufferedReader(new InputStreamReader(
                    stream, "UTF-8"));

            String line = null;
            while ((line = bReader.readLine()) != null) {
                result += line;
            }
            bReader.close();

            profileResponse = Html.fromHtml(result).toString();

            JSONObject response = new JSONObject(profileResponse);
            ArrayList<Survey> surveys = new ArrayList<Survey>();
            JSONArray jArray = response.getJSONArray("results").getJSONObject(0).getJSONArray
                    ("bio");
            Log.d("User Profile", jArray.toString(2));
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject surveyObject = jArray.getJSONObject(i);
                Survey survey = new Survey();
                survey.setId(surveyObject.getInt("question_id"));
                survey.setQuestionType(surveyObject.getString("question_type"));
                survey.setResponseUrl(surveyObject.getString("response_url"));
                survey.setText(surveyObject.getString("question_text"));
                if (survey.getQuestionType().equalsIgnoreCase(Constants.SURVEY_BINARY) || survey
                        .getQuestionType().equalsIgnoreCase(Constants.SURVEY_LIKERT) || survey
                        .getQuestionType().equalsIgnoreCase(Constants.SURVEY_MULTICHOICE)) {
                    SurveyOptions options = new SurveyOptions();
                    options.setText(surveyObject.optString("selected_option_text"));
                    options.setId(surveyObject.optInt("selected_option"));
                    survey.setSelectedOption(options);
                } else {
                    survey.setInputType(surveyObject.optString("question_input_type"));
                    survey.setResponse(surveyObject.optString("response"));
                }
                surveys.add(survey);
            }
            return surveys;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Survey> result) {
        mCallback.userProfileFound(result);
    }
}
