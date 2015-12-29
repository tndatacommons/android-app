package org.tndata.android.compass.parser;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.database.CompassDbHelper;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.Survey;
import org.tndata.android.compass.model.SurveyOptions;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Collection of methods to parse data that has to do with users, profiles or user data sets.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public final class UserDataParser extends ParserMethods{
    /**
     * Parses a user from a JSON string.
     *
     * @param src the source string.
     * @return the parsed User.
     */
    public static User parseUser(String src){
        User user = sGson.fromJson(src, User.class);
        try{
            JSONObject userObject = new JSONObject(src);
            Log.d("UserParser", userObject.toString(2));
            user.setError("");
            JSONArray errorArray = userObject.optJSONArray("non_field_errors");
            if (errorArray != null){
                user.setError(errorArray.optString(0));
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return user;
    }

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

                    SurveyOptions options = new SurveyOptions();
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



    /**
     * Parses the user data string provided by the api.
     *
     * @param context the context.
     * @param src the source string.
     * @return the data bundle containing the user data.
     */
    public static UserData parseUserData(Context context, String src){
        try{
            UserData userData = new UserData();

            JSONObject userJson = new JSONObject(src).getJSONArray("results").getJSONObject(0);

            //Parse the user-selected content, store in userData; wait till all data is set
            //  before syncing parent/child relationships
            Map<Integer, Category> categories = ContentParser.parseCategoryArray(userJson.getString("categories"));
            Map<Integer, Goal> goals = ContentParser.parseGoalArray(userJson.getString("goals"));
            Map<Integer, Behavior> behaviors = ContentParser.parseBehaviorArray(userJson.getString("behaviors"));
            Map<Integer, Action> actions = ContentParser.parseActionArray(userJson.getString("actions"), null);

            if (categories == null || goals == null || behaviors == null || actions == null){
                return null;
            }

            userData.setCategories(categories);
            userData.setGoals(goals);
            userData.setBehaviors(behaviors);
            userData.setActions(actions);
            userData.sync();

            //Parse the places and save write them into the database
            userData.setPlaces(PlaceParser.parsePlaces(userJson.getString("places")));
            CompassDbHelper dbHelper = new CompassDbHelper(context);
            dbHelper.emptyPlacesTable();
            dbHelper.savePlaces(userData.getPlaces());
            dbHelper.close();

            //Feed data
            FeedData feedData = new FeedData();

            JSONObject nextAction = userJson.getJSONObject("next_action");
            //If there is a next_action
            if (nextAction.has("id")){
                //Parse it out and retrieve the reference to the original copy
                feedData.setNextAction(userData.getAction(ContentParser.parseAction(nextAction.toString())));
            }

            if (!userJson.isNull("action_feedback")){
                Log.d("Parser", "has feedback");
                JSONObject feedback = userJson.getJSONObject("action_feedback");
                feedData.setFeedbackTitle(feedback.getString("title"));
                feedData.setFeedbackSubtitle(feedback.getString("subtitle"));
                feedData.setFeedbackIconId(feedback.getInt("icon"));
            }

            JSONObject progress = userJson.getJSONObject("progress");
            feedData.setProgressPercentage(progress.getInt("progress"));
            feedData.setCompletedActions(progress.getInt("completed"));
            feedData.setTotalActions(progress.getInt("total"));

            List<Action> upcomingActions = new ArrayList<>();
            ContentParser.parseActions(userJson.getString("upcoming_actions"), upcomingActions);
            if (upcomingActions.size() > 1){
                List<Action> actionReferences = new ArrayList<>();
                for (Action action:upcomingActions.subList(1, upcomingActions.size())){
                    actionReferences.add(userData.getAction(action));
                }
                feedData.setUpcomingActions(actionReferences);
            }
            else{
                feedData.setUpcomingActions(new ArrayList<Action>());
            }

            Map<Integer, Goal> suggestionMap = ContentParser.parseGoalArray(userJson.getString("suggestions"));
            List<Goal> suggestions = new ArrayList<>();
            if (suggestionMap != null){
                suggestions.addAll(suggestionMap.values());
            }
            feedData.setSuggestions(suggestions);

            userData.setFeedData(feedData);
            userData.logData();

            return userData;
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return null;
    }
}
