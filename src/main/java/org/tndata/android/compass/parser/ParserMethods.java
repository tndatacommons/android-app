package org.tndata.android.compass.parser;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.tndata.android.compass.parser.deserializer.ListDeserializer;
import org.tndata.android.compass.parser.deserializer.MapDeserializer;
import org.tndata.android.compass.parser.deserializer.SetDeserializer;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Contains the methods used by the Parser to generate content out of a JSON string.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
class ParserMethods{
    protected static Gson sGson = new GsonBuilder()
            .registerTypeAdapter(Map.class, new MapDeserializer())
            .registerTypeAdapter(List.class, new ListDeserializer())
            .registerTypeAdapter(Set.class, new SetDeserializer())
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .create();

    /*static UserData parseUserData(JSONObject src) throws JSONException{
        Log.d("Parser", "Parsing user data");

        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
        UserData userData = new UserData();

        //First, parse out the user content
        Map<Integer, Category> categories = new HashMap<>();
        JSONArray categoryArray = src.getJSONArray("categories");
        for (int i = 0; i < categoryArray.length(); i++){
            JSONObject userCategory = categoryArray.getJSONObject(i);
            Category category = gson.fromJson(userCategory.getString("category"), Category.class);
            category.setMappingId(userCategory.getInt("id"));
            category.setEditable(userCategory.getBoolean("editable"));
            categories.put(category.getId(), category);
        }
        userData.setCategories(categories);

        Map<Integer, Goal> goals = new HashMap<>();
        JSONArray goalArray = src.getJSONArray("goals");
        for (int i = 0; i < goalArray.length(); i++){
            JSONObject userGoal = goalArray.getJSONObject(i);
            Goal goal = gson.fromJson(goalArray.getString(i), Goal.class);
            goal.setMappingId(userGoal.getInt("id"));
            goal.setEditable(userGoal.getBoolean("editable"));
            goals.put(goal.getId(), goal);
        }
        userData.setGoals(goals);

        Map<Integer, Behavior> behaviors = new HashMap<>();
        JSONArray behaviorArray = src.getJSONArray("behaviors");
        for (int i = 0; i < behaviorArray.length(); i++){
            JSONObject userBehavior = behaviorArray.getJSONObject(i);
            Behavior behavior = gson.fromJson(behaviorArray.getString(i), Behavior.class);
            behavior.setMappingId(userBehavior.getInt("id"));
            behavior.setEditable(userBehavior.getBoolean("editable"));
            behaviors.put(behavior.getId(), behavior);
        }
        userData.setBehaviors(behaviors);

        Map<Integer, Action> actions = new HashMap<>();
        JSONArray actionArray = src.getJSONArray("actions");
        for (int i = 0; i < actionArray.length(); i++){
            JSONObject userAction = actionArray.getJSONObject(i);
            Action action = gson.fromJson(actionArray.getString(i), Action.class);
            action.setMappingId(userAction.getInt("id"));
            action.setEditable(userAction.getBoolean("editable"));
            actions.put(action.getId(), action);
        }
        userData.setActions(actions);

        //Start the linking
        JSONObject dataGraph = src.getJSONObject("data_graph");
        categoryArray = dataGraph.getJSONArray("categories");
        for (int i = 0; i < categoryArray.length(); i++){
            JSONArray catGoalRelationship = categoryArray.getJSONArray(i);
            Category category = categories.get(catGoalRelationship.getInt(0));
            Goal goal = goals.get(catGoalRelationship.getInt(1));
            category.addGoal(goal);
            goal.addCategory(category);
        }

        goalArray = dataGraph.getJSONArray("goals");
        for (int i = 0; i < goalArray.length(); i++){
            JSONArray goalBehaviorRelationship = goalArray.getJSONArray(i);
            Goal goal = goals.get(goalBehaviorRelationship.getInt(0));
            Behavior behavior = behaviors.get(goalBehaviorRelationship.getInt(1));
            goal.addBehavior(behavior);
            behavior.addGoal(goal);
        }

        behaviorArray = dataGraph.getJSONArray("behaviors");
        for (int i = 0; i < behaviorArray.length(); i++){
            JSONArray behaviorActionRelationship = behaviorArray.getJSONArray(i);
            Behavior behavior = behaviors.get(behaviorActionRelationship.getInt(0));
            Action action = actions.get(behaviorActionRelationship.getInt(1));
            behavior.addAction(action);
            //TODO, check with Brad
            action.setBehavior(behavior);
        }

        JSONArray primaryCategoryArray = dataGraph.getJSONArray("primary_categories");
        for (int i = 0; i < primaryCategoryArray.length(); i++){
            JSONArray goalCatRelationship = primaryCategoryArray.getJSONArray(i);
            int categoryId = goalCatRelationship.optInt(1, -1);
            if (categoryId != -1){
                goals.get(goalCatRelationship.getInt(0)).setPrimaryCategory(categories.get(categoryId));
            }
        }

        JSONArray primaryGoalArray = dataGraph.getJSONArray("primary_goals");
        for (int i = 0; i < primaryGoalArray.length(); i++){
            JSONArray actionGoalRelationship = primaryGoalArray.getJSONArray(i);
            int goalId = actionGoalRelationship.optInt(1, -1);
            if (goalId != -1){
                actions.get(actionGoalRelationship.getInt(0)).setPrimaryGoal(goals.get(goalId));
            }
        }

        JSONArray placeArray = src.getJSONArray("places");
        List<Place> places = new ArrayList<>();
        for (int i = 0; i < placeArray.length(); i++){
            JSONObject placeObject = placeArray.getJSONObject(i);
            Place place = gson.fromJson(placeObject.toString(), Place.class);
            place.setName(placeObject.getJSONObject("place").getString("name"));
            place.setPrimary(placeObject.getJSONObject("place").getBoolean("primary"));
            places.add(place);
        }
        userData.setPlaces(places);

        //Feed data
        FeedData feedData = new FeedData();

        JSONObject nextAction = src.getJSONObject("next_action");
        //If there is a next_action
        if (nextAction.has("id")){
            //Parse it out and retrieve the reference to the original copy
            feedData.setNextAction(null);
        }

        if (!src.isNull("action_feedback")){
            Log.d("Parser", "has feedback");
            JSONObject feedback = src.getJSONObject("action_feedback");
            feedData.setFeedbackTitle(feedback.getString("title"));
            feedData.setFeedbackSubtitle(feedback.getString("subtitle"));
            feedData.setFeedbackIconId(feedback.getInt("icon"));
        }

        JSONObject progress = src.getJSONObject("progress");
        feedData.setProgressPercentage(progress.getInt("progress"));
        feedData.setCompletedActions(progress.getInt("completed"));
        feedData.setTotalActions(progress.getInt("total"));

        List<Action> upcomingActions = new ArrayList<>();
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

        JSONArray suggestionArray = src.getJSONArray("suggestions");
        List<Goal> suggestions = new ArrayList<>();
        for (int i = 0; i < suggestionArray.length(); i++){
            suggestions.add(gson.fromJson(suggestionArray.getString(i), Goal.class));
        }
        feedData.setSuggestions(suggestions);

        userData.setFeedData(feedData);
        return userData;
    }*/
}
