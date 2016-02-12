package org.tndata.android.compass.parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.model.SearchResult;
import org.tndata.android.compass.model.Survey;
import org.tndata.android.compass.model.SurveyOption;
import org.tndata.android.compass.util.Constants;

import java.util.ArrayList;
import java.util.List;


/**
 * Collection of methods to parse data that has to do with users, profiles or user data sets.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public final class LegacyParser extends ParserMethods{
    public static List<Survey> parseProfileBio(String src){
        List<Survey> surveys = new ArrayList<>();
        try{
            JSONObject object = new JSONObject(src);
            JSONArray bio = object.getJSONArray("results").getJSONObject(0).getJSONArray("bio");
            Log.d("User Profile", bio.toString(2));
            for (int i = 0; i < bio.length(); i++){
                JSONObject surveyObject = bio.getJSONObject(i);
                Survey survey = new Survey();
                survey.setId(surveyObject.getInt("question_id"));
                survey.setQuestionType(surveyObject.getString("question_type"));
                survey.setResponseUrl(surveyObject.getString("response_url"));
                survey.setText(surveyObject.getString("question_text"));

                if (survey.getQuestionType().equalsIgnoreCase(Constants.SURVEY_BINARY)
                        || survey.getQuestionType().equalsIgnoreCase(Constants.SURVEY_LIKERT)
                        || survey.getQuestionType().equalsIgnoreCase(Constants.SURVEY_MULTICHOICE)){

                    SurveyOption options = new SurveyOption();
                    options.setText(surveyObject.optString("selected_option_text"));
                    options.setId(surveyObject.optInt("selected_option"));
                    survey.setSelectedOption(options);
                }
                else{
                    survey.setInputType(surveyObject.optString("question_input_type"));
                    survey.setResponse(surveyObject.optString("response"));
                }
                surveys.add(survey);
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return surveys;
    }

    public static List<SearchResult> parseSearchResults(String src){
        List<SearchResult> results = new ArrayList<>();
        try{
            JSONArray resultArray = new JSONObject(src).getJSONArray("results");
            for (int i = 0; i < resultArray.length(); i++){
                results.add(sGson.fromJson(resultArray.getString(i), SearchResult.class));
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return results;
    }
}
