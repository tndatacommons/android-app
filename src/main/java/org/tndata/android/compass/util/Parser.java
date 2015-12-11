package org.tndata.android.compass.util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.database.CompassDbHelper;
import org.tndata.android.compass.model.*;
import org.tndata.android.compass.model.Package;

import java.util.ArrayList;
import java.util.List;


/**
 * A centralised parser. All the code that does parsing should go here. At the moment it
 * includes category, goal, behavior, action, and place parsing.
 *
 * @author Ismael Alonso
 * @version 1.0.0 (WIP)
 */
public class Parser{
    private Gson gson;


    /**
     * Constructor.
     */
    public Parser(){
        gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
    }

    public List<Category> parseCategories(String src){
        try{
            return parseCategories(new JSONObject(src).getJSONArray("results"), false);
        }
        catch (JSONException jsonx){
            return null;
        }
    }

    /**
     * Parses out the category list.
     *
     * @param categoryArray a JSONArray containing categories.
     * @param userCategories true if the data comes from a api/user/ prefixed endpoint.
     * @return A list of categories.
     */
    public List<Category> parseCategories(JSONArray categoryArray, boolean userCategories){
        List<Category> categories = new ArrayList<>();

        try{
            //For each category in the array
            for (int i = 0; i < categoryArray.length(); i++){
                //The string to be parsed by GSON is extracted from the array
                String categoryString;
                if (userCategories){
                    //If it is a user category, it will come as a nested object
                    categoryString = categoryArray.getJSONObject(i).getString("category");
                }
                else{
                    //If it is not, it will come as the object itself
                    categoryString = categoryArray.getString(i);
                }
                Category category = gson.fromJson(categoryString, Category.class);

                //The JSONObject is extracted, since it will be needed later on
                JSONObject categoryJson = categoryArray.getJSONObject(i);
                String goalArrayName;
                if (userCategories){
                    category.setMappingId(categoryJson.getInt("id"));
                    category.setProgressValue(categoryJson.getDouble("progress_value"));
                    category.setCustomTriggersAllowed(categoryJson.getBoolean("custom_triggers_allowed"));
                    goalArrayName = "user_goals";
                }
                else{
                    goalArrayName = "goals";
                }

                //Set the category's goals
                List<Goal> goals = new ArrayList<>();
                JSONArray goalArray = categoryJson.getJSONArray(goalArrayName);
                for (int j = 0; j < goalArray.length(); j++){
                    goals.add(gson.fromJson(goalArray.getString(j), Goal.class));
                }
                category.setGoals(goals);

                Log.d("CategoryParser", category.toString());
                categories.add(category);
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }

        return categories;
    }

    public List<Goal> parseGoals(String src){
        try{
            return parseGoals(new JSONObject(src).getJSONArray("results"), false);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }

    public Goal parseAddedGoal(String src){
        try{
            JSONObject userGoal = new JSONObject(src);
            Goal goal = gson.fromJson(userGoal.getString("goal"), Goal.class);
            goal.setMappingId(userGoal.getInt("id"));
            JSONArray categoryArray = userGoal.getJSONArray("user_categories");
            List<Category> categories = goal.getCategories();
            for (int x = 0; x < categoryArray.length(); x++){
                Category category = gson.fromJson(categoryArray.getString(x), Category.class);
                categories.add(category);
            }
            goal.setCategories(categories);
            return goal;
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }

    /**
     * Parses out the goal list.
     *
     * @param goalArray a JSONArray containing goals.
     * @param userGoals true if the data comes from a api/user/ prefixed endpoint.
     * @return A list of goals.
     */
    public List<Goal> parseGoals(JSONArray goalArray, boolean userGoals){
        List<Goal> goals = new ArrayList<>();

        try {
            //For each category in the array
            for (int i = 0; i < goalArray.length(); i++){
                //The string to be parsed by GSON is extracted from the array
                String goalString;
                if (userGoals){
                    //If it is a user goal, it will come as a nested object
                    goalString = goalArray.getJSONObject(i).getString("goal");
                }
                else{
                    //If it is not, it will come as the object itself
                    goalString = goalArray.getString(i);
                }
                Goal goal = gson.fromJson(goalString, Goal.class);

                JSONObject goalJson = goalArray.getJSONObject(i);
                String categoryArrayName;
                if (userGoals){
                    goal.setProgressValue(goalJson.getDouble("progress_value"));
                    goal.setMappingId(goalJson.getInt("id"));
                    goal.setCustomTriggersAllowed(goalJson.getBoolean("custom_triggers_allowed"));

                    //Set the goal's child behaviors
                    List<Behavior> behaviors = new ArrayList<>();
                    JSONArray behaviorArray = goalJson.getJSONArray("user_behaviors");
                    for (int j = 0; j < behaviorArray.length(); j++){
                        behaviors.add(gson.fromJson(behaviorArray.getString(j), Behavior.class));
                    }
                    goal.setBehaviors(behaviors);

                    //Set the primary category
                    goal.setPrimaryCategory(gson.fromJson(goalJson.getString("primary_category"), Category.class));

                    //Set the progress
                    goal.setProgress(gson.fromJson(goalJson.getString("goal_progress"), Progress.class));

                    categoryArrayName = "user_categories";
                }
                else{
                    categoryArrayName = "categories";
                }

                //Set the goal's parent categories
                List<Category> categories = new ArrayList<>();
                JSONArray categoryArray = goalJson.getJSONArray(categoryArrayName);
                for (int j = 0; j < categoryArray.length(); j++){
                    categories.add(gson.fromJson(categoryArray.getString(j), Category.class));
                }
                goal.setCategories(categories);

                Log.d("GoalParser", goal.toString());
                goals.add(goal);
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }

        return goals;
    }

    public List<Behavior> parseBehaviors(String src){
        try{
            return parseBehaviors(new JSONObject(src).getJSONArray("results"), false);
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }

    public Behavior parseAddedBehavior(String src){
        try{
            JSONObject userBehavior = new JSONObject(src);
            ;
            Behavior behavior = gson.fromJson(userBehavior.getString("behavior"), Behavior.class);
            behavior.setMappingId(userBehavior.getInt("id"));

            // Include the Behavior's Parent goals that have been selected by the user
            JSONArray goalArray = userBehavior.getJSONArray("user_goals");
            List<Goal> goals = behavior.getGoals();
            for (int x = 0; x < goalArray.length(); x++){
                Goal goal = gson.fromJson(goalArray.getString(x), Goal.class);
                goals.add(goal);
            }
            behavior.setGoals(goals);
            return behavior;
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }

    /**
     * Parses out the goal list.
     *
     * @param behaviorArray a JSONArray containing behaviors.
     * @param userBehaviors true if the data comes from a api/user/ prefixed endpoint.
     * @return A list of behaviors.
     */
    public List<Behavior> parseBehaviors(JSONArray behaviorArray, boolean userBehaviors){
        List<Behavior> behaviors = new ArrayList<>();

        try{
            for (int i = 0; i < behaviorArray.length(); i++){
                //The string to be parsed by GSON is extracted from the array
                String behaviorString;
                if (userBehaviors){
                    //If it is a user behavior, it will come as a nested object
                    behaviorString = behaviorArray.getJSONObject(i).getString("behavior");
                }
                else{
                    //If it is not, it will come as the object itself
                    behaviorString = behaviorArray.getString(i);
                }
                Behavior behavior = gson.fromJson(behaviorString, Behavior.class);

                JSONObject behaviorJson = behaviorArray.getJSONObject(i);
                String goalArrayName;
                if (userBehaviors){
                    behavior.setMappingId(behaviorJson.getInt("id"));
                    behavior.setCustomTriggersAllowed(behaviorJson.getBoolean("custom_triggers_allowed"));

                    //Set the behavior's child actions
                    List<Action> actions = new ArrayList<>();
                    JSONArray actionArray = behaviorJson.getJSONArray("user_actions");
                    for (int j = 0; j < actionArray.length(); j++){
                        actions.add(parseAction(actionArray.getJSONObject(j), false));
                    }
                    behavior.setActions(actions);

                    JSONArray categoryArray = behaviorJson.getJSONArray("user_categories");
                    List<Category> categories = behavior.getUserCategories();
                    for (int j = 0; j < categoryArray.length(); j++){
                        categories.add(gson.fromJson(categoryArray.getString(j), Category.class));
                    }
                    behavior.setUserCategories(categories);

                    behavior.setProgress(gson.fromJson(behaviorJson.getString("behavior_progress"), Progress.class));

                    goalArrayName = "user_goals";
                }
                else{
                    goalArrayName = "goals";
                }

                //Set the behavior's parent goals
                List<Goal> goals = behavior.getGoals();
                JSONArray goalArray = behaviorJson.getJSONArray(goalArrayName);
                for (int j = 0; j < goalArray.length(); j++){
                    goals.add(gson.fromJson(goalArray.getJSONObject(j).toString(), Goal.class));
                }
                behavior.setGoals(goals);

                Log.d("BehaviorParser", behavior.toString());
                behaviors.add(behavior);
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }

        return behaviors;
    }

    public List<Action> parseUserActions(String src){
        try{
            return parseActions(new JSONObject(src).getJSONArray("results"), true);
        }
        catch (JSONException jsonx){
            return null;
        }
    }

    public List<Action> parseActions(String src){
        try{
            return parseActions(new JSONObject(src).getJSONArray("results"), false);
        }
        catch (JSONException jsonx){
            return null;
        }
    }

    public Action parseAddedAction(String src){
        try{
            JSONObject userAction = new JSONObject(src);
            Action action = gson.fromJson(userAction.getString("action"), Action.class);

            if (action != null){
                action.setMappingId(userAction.getInt("id"));
                return action;
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return null;
    }

    public Action parseActionWithTrigger(String src){
        try{
            JSONObject response = new JSONObject(src);
            Action action = gson.fromJson(response.getString("action"), Action.class);
            action.setMappingId(response.getInt("id"));
            if (!response.isNull("custom_trigger")){
                action.setCustomTrigger(gson.fromJson(response.getString("custom_trigger"), Trigger.class));
            }
            action.setNextReminderDate(response.getString("next_reminder"));
            return action;
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }

    /**
     * Parses out the action list.
     *
     * @param actionArray a JSONArray containing actions.
     * @param userActions true if the data comes from a api/user/ prefixed endpoint.
     * @return A list of actions.
     */
    public List<Action> parseActions(JSONArray actionArray, boolean userActions){

        Log.d("ActionParser", actionArray.length()+"");
        List<Action> actions = new ArrayList<>();

        try{
            //For each action in the array
            for (int i = 0; i < actionArray.length(); i++){
                actions.add(parseAction(actionArray.getJSONObject(i), userActions));
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }

        return actions;
    }

    /**
     * Parses out a single action.
     *
     * @param actionObject the JSONObject containing the action.
     * @param userAction true if the data is un user format.
     * @return the parsed action.
     */
    @Nullable
    public Action parseAction(JSONObject actionObject, boolean userAction){
        //The string to be parsed by GSON is extracted from the array
        try{
            String actionString;
            if (userAction){
                //If it is a user action, it will come as a nested object
                actionString = actionObject.getString("action");
            }
            else{
                //If it is not, it will come as the object itself
                actionString = actionObject.toString();
            }
            Action action = gson.fromJson(actionString, Action.class);
            //There are some other things that need to be parsed, but only if this is a user action
            if (userAction){
                //Set the user mapping id and the trigger allowance flag
                action.setMappingId(actionObject.getInt("id"));
                action.setCustomTriggersAllowed(actionObject.getBoolean("custom_triggers_allowed"));

                //Parse the custom trigger if there is one
                if (!actionObject.isNull("custom_trigger")){
                    String triggerString = actionObject.getString("custom_trigger");
                    action.setCustomTrigger(gson.fromJson(triggerString, Trigger.class));
                }

                action.setPrimaryGoal(gson.fromJson(actionObject.getString("primary_goal"), Goal.class));
                action.setNextReminderDate(actionObject.getString("next_reminder"));

                action.setPrimaryCategory(gson.fromJson(actionObject.getString("primary_category"), Category.class));
            }

            Log.d("ActionParser", action.toString());
            return action;
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }

        return null;
    }

    /**
     * Parses out the place list.
     *
     * @param placeArray a JSONArray containing a list of places.
     * @return a list of places.
     */
    public List<Place> parsePlaces(JSONArray placeArray){
        List<Place> places = new ArrayList<>();

        try{
            for (int i = 0; i < placeArray.length(); i++){
                JSONObject placeObject = placeArray.getJSONObject(i);
                Place place = gson.fromJson(placeObject.toString(), Place.class);
                place.setName(placeObject.getJSONObject("place").getString("name"));
                place.setPrimary(placeObject.getJSONObject("place").getBoolean("primary"));
                places.add(place);
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }

        return places;
    }

    public List<Place> parsePrimaryPlaces(String src){
        List<Place> places = new ArrayList<>();
        try{
            JSONArray placeArray = new JSONObject(src).optJSONArray("results");
            if (placeArray != null){
                Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
                for (int i = 0; i < placeArray.length(); i++){
                    Place place = gson.fromJson(placeArray.getString(i), Place.class);
                    place.setId(-1);
                    places.add(place);
                }
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return places;
    }

    /**
     * Parses a user from a JSON string.
     *
     * @param src the source string.
     * @return the parsed User.
     */
    public User parseUser(String src){
        User user = gson.fromJson(src, User.class);
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

    /**
     * Parses the user data string provided by the api.
     *
     * @param context the context.
     * @param src the source string.
     * @return the data bundle containing the user data.
     */
    public UserData parseUserData(Context context, String src){
        try{
            UserData userData = new UserData();

            JSONObject userJson = new JSONObject(src).getJSONArray("results").getJSONObject(0);

            //Parse the user-selected content, store in userData; wait till all data is set
            //  before syncing parent/child relationships
            userData.setCategories(parseCategories(userJson.getJSONArray("categories"), true), false);
            userData.setGoals(parseGoals(userJson.getJSONArray("goals"), true), false);
            userData.setBehaviors(parseBehaviors(userJson.getJSONArray("behaviors"), true), false);
            userData.setActions(parseActions(userJson.getJSONArray("actions"), true), false);
            userData.sync();

            //Parse the places and save write them into the database
            userData.setPlaces(parsePlaces(userJson.getJSONArray("places")));
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
                feedData.setNextAction(userData.getAction(parseAction(nextAction, true)));
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

            List<Action> actions = parseActions(userJson.getJSONArray("upcoming_actions"), true);
            if (actions.size() > 1){
                List<Action> actionReferences = new ArrayList<>();
                for (Action action:actions.subList(1, actions.size())){
                    actionReferences.add(userData.getAction(action));
                }
                feedData.setUpcomingActions(actionReferences);
            }
            else{
                feedData.setUpcomingActions(new ArrayList<Action>());
            }
            feedData.setSuggestions(parseGoals(userJson.getJSONArray("suggestions"), false));

            userData.setFeedData(feedData);
            userData.logData();

            return userData;
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return null;
    }

    /**
     * Parses a list of rewards from a JSON string.
     *
     * @param src the source string in JSON format.
     * @return a list of categories.
     */
    public List<Reward> parseRewards(String src){
        List<Reward> rewards = new ArrayList<>();
        try{
            JSONArray rewardArray = new JSONObject(src).getJSONArray("results");
            for (int i = 0; i < rewardArray.length(); i++){
                rewards.add(gson.fromJson(rewardArray.getString(i), Reward.class));
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return rewards;
    }

    public List<Action> parseTodaysActions(String src){
        List<Action> actions = new ArrayList<>();
        try{
            JSONArray actionArray = new JSONObject(src).getJSONArray("results");
            for (int i = 0; i < actionArray.length(); i++){
                JSONObject actionObject = actionArray.getJSONObject(i);
                Action action = parseAction(actionObject, true);
                Goal goal = gson.fromJson(actionObject.getString("primary_goal"), Goal.class);
                goal.setPrimaryCategory(gson.fromJson(actionObject.getString("primary_category"), Category.class));
                if (action != null){
                    action.setPrimaryGoal(goal);
                    action.setBehavior(gson.fromJson(actionObject.getString("behavior"), Behavior.class));
                    actions.add(action);
                }
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return actions;
    }

    public List<SearchResult> parseSearchResults(String src){
        List<SearchResult> results = new ArrayList<>();
        try{
            JSONArray resultArray = new JSONObject(src).getJSONArray("results");
            for (int i = 0; i < resultArray.length(); i++){
                results.add(gson.fromJson(resultArray.getString(i), SearchResult.class));
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return results;
    }

    public Goal parseGoal(String src){
        Goal goal = gson.fromJson(src, Goal.class);
        try{
            List<Category> categories = new ArrayList<>();
            goal.setCategories(categories);
            JSONArray categoriesArray = new JSONObject(src).getJSONArray("categories");
            for (int i = 0; i < categoriesArray.length(); i++){
                categories.add(gson.fromJson(categoriesArray.getString(i), Category.class));
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return goal;
    }

    public Instrument parseInstrument(String src){
        return gson.fromJson(src, Instrument.class);
    }

    public Package parsePackage(String src){
        try{
            Package myPackage = gson.fromJson(new JSONObject(src).getString("category"), Package.class);
            myPackage.setId(new JSONObject(src).getInt("id"));
            return myPackage;
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }

    public List<Survey> parseProfileBio(String src){
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

    public Gson getGson(){
        return gson;
    }
}
