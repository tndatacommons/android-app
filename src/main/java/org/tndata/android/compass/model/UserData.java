package org.tndata.android.compass.model;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class encapsulates all of the user-selected content from the online REST api:
 *
 * - Categories
 * - Goals
 * - Behaviors
 * - Actions
 * - Places
 *
 * It includes methods to set & get those values, and keeps a consistently updated
 * hierarchy (i.e. Behaviors know about their parent goals & child actions, etc).
 *
 * @author Brad Montgomery
 * @author Ismael Alonso
 */
public class UserData{
    private static final String TAG = "UserData";


    //User selected content. mapping_id -> content maps
    private Map<Integer, UserCategory> mCategories;
    private Map<Integer, UserGoal> mGoals;
    private Map<Integer, UserBehavior> mBehaviors;
    private Map<Integer, UserAction> mActions;

    //User places
    private List<Place> mPlaces = new ArrayList<>();

    //Data for the main feed
    private FeedData mFeedData;


    /**
     * Constructor.
     */
    public UserData(){
        mCategories = new HashMap<>();
        mGoals = new HashMap<>();
        mBehaviors = new HashMap<>();
        mActions = new HashMap<>();
    }


    /*--------------------CONTENT SETTERS--------------------*
     * These methods are meant to be used by the parser only *
     *-------------------------------------------------------*/

    public void setCategories(@NonNull Map<Integer, UserCategory> categories){
        mCategories = categories;
    }

    public void setGoals(@NonNull Map<Integer, UserGoal> goals){
        mGoals = goals;
    }

    public void setBehaviors(@NonNull Map<Integer, UserBehavior> behaviors){
        mBehaviors = behaviors;
    }

    public void setActions(@NonNull Map<Integer, UserAction> actions){
        mActions = actions;
    }


    /*--------------------------*
     * CATEGORY RELATED METHODS *
     *--------------------------*/

    /**
     * Returns the list of user-selected Categories.
     *
     * @return an ArrayList of Category objects
     */
    public Map<Integer, UserCategory> getCategories(){
        return mCategories;
    }

    /**
     * Returns the original copy of the provided category.
     *
     * @param category the category whose original copy needs to be fetched.
     * @return the original copy of such category.
     */
    public UserCategory getCategory(Category category){
        return mCategories.get(category.getId());
    }

    public UserCategory getCategory(UserCategory userCategory){
        return getCategory(userCategory.getCategory());
    }

    public boolean contains(UserCategory userCategory){
        return mCategories.containsKey(userCategory.getCategory().getId());
    }

    /**
     * Returns all of the goals for a given Category. This is a wrapper for category.getGoals().
     *
     * @return a List of Goal objects.
     */
    public List<UserGoal> getCategoryGoals(Category category){
        return getCategory(category).getGoals();
    }

    /**
     * Adds a Category to the list of user-selected categories (if it's not
     * already included) and assigns any user-selected goals to the new Category.
     *
     * @param userCategory the Category object to add.
     */
    public void addCategory(UserCategory userCategory){
        //If the category ain't in the data set
        if (!contains(userCategory)){
            //Add it
            mCategories.put(userCategory.getCategory().getId(), userCategory);

            //Link goals
            List<UserGoal> goals = new ArrayList<>();
            for (UserGoal categoryGoal:userCategory.getGoals()){
                UserGoal goal = getGoal(categoryGoal.getGoal());
                goal.addCategory(userCategory);
                goals.add(goal);
            }
            userCategory.setGoals(goals);
        }
    }

    /**
     * Remove a single Category from the user's collection. This also removes the
     * reference to that category from all Goals.
     *
     * @param category the category to remove
     */
    public void removeCategory(Category category){
        UserCategory removedCategory = mCategories.remove(category.getId());

        if (removedCategory != null){
            List<UserGoal> goalsToRemove = new ArrayList<>();
            //Remove this category from any child goals
            for (UserGoal goal : removedCategory.getGoals()){
                goal.removeCategory(removedCategory);
                //Record all the Goals w/o parent Categories
                if (goal.getCategories().isEmpty()){
                    goalsToRemove.add(goal);
                }
            }
            //Remove all the Goals w/o parent Categories
            for (UserGoal goal : goalsToRemove){
                removeGoal(goal.getGoal());
            }
        }
    }


    /*----------------------*
     * GOAL RELATED METHODS *
     *----------------------*/

    /**
     * Returns the list of user-selected Goals.
     *
     * @return a List of Goal objects.
     */
    public Map<Integer, UserGoal> getGoals(){
        return mGoals;
    }

    /**
     * Returns the original copy of the provided goal.
     *
     * @param goal the goal whose original copy needs to be fetched.
     * @return the original copy of such goal.
     */
    public UserGoal getGoal(Goal goal){
        return mGoals.get(goal.getId());
    }

    public UserGoal getGoal(UserGoal userGoal){
        return getGoal(userGoal.getGoal());
    }

    public boolean contains(UserGoal userGoal){
        return mGoals.containsKey(userGoal.getGoal().getId());
    }

    /**
     * Add a goal to the list of user selected Goals.
     *
     * Adding a Goal also assigns it to any existing user-selected categories,
     * as well as assigning any existing Behaviors to the Goal.
     *
     * @param userGoal the goal to be added to the user list.
     */
    public void addGoal(UserGoal userGoal){
        //If the goal ain't in the data set
        if (!contains(userGoal)){
            //Add it
            mGoals.put(userGoal.getGoal().getId(), userGoal);

            //Add it to the relevant categories
            for (UserCategory category:userGoal.getCategories()){
                UserCategory cat = getCategory(category);
                if (cat != null){
                    cat.addGoal(userGoal);
                }
            }

            //Link behaviors
            List<UserBehavior> behaviors = new ArrayList<>();
            for (UserBehavior goalBehavior:userGoal.getBehaviors()){
                UserBehavior behavior = getBehavior(goalBehavior.getBehavior());
                behavior.addGoal(userGoal);
                behaviors.add(behavior);
            }
            userGoal.setBehaviors(behaviors);
        }
    }

    /**
     * Removes a goal from the user's collection.
     *
     * Removing a goal also removes its reference from the parent Categories
     * as well as the child Behaviors.
     *
     * @param goal the goal to be removed from the user list.
     */
    public void removeGoal(Goal goal){
        UserGoal removedGoal = mGoals.remove(goal.getId());

        if (removedGoal != null){
            //Remove the goal from its parent categories
            for (UserCategory category : removedGoal.getCategories()){
                category.removeGoal(removedGoal);
            }

            List<UserBehavior> behaviorsToRemove = new ArrayList<>();
            //Remove the goal from its child Behaviors
            for (UserBehavior behavior : removedGoal.getBehaviors()){
                behavior.removeGoal(removedGoal);
                //Record all the Behaviors w/o parent Goals
                if (behavior.getGoals().isEmpty()){
                    behaviorsToRemove.add(behavior);
                }
            }
            //Remove Behaviors w/o parent Goals
            for (UserBehavior behavior : behaviorsToRemove){
                removeBehavior(behavior.getBehavior());
            }
        }
    }


    /*--------------------------*
     * BEHAVIOR RELATED METHODS *
     *--------------------------*/

    /**
     * Return the user-selected Behaviors.
     *
     * @return an ArrayList of Behavior objects.
     */
    public Map<Integer, UserBehavior> getBehaviors(){
        return mBehaviors;
    }

    /**
     * Returns the original copy of the provided behavior.
     *
     * @param behavior the behavior whose original copy needs to be fetched.
     * @return the original copy of such behavior.
     */
    public UserBehavior getBehavior(Behavior behavior){
        return mBehaviors.get(behavior.getId());
    }

    public UserBehavior getBehavior(UserBehavior userBehavior){
        return getBehavior(userBehavior.getBehavior());
    }

    public boolean contains(UserBehavior userBehavior){
        return mBehaviors.containsKey(userBehavior.getBehavior().getId());
    }

    /**
     * Add a single Behavior to the user's collection.
     *
     * Adding this object will also trigger an update to behavior's parent goals.
     *
     * @param userBehavior the behavior to be added to the user's list.
     */
    public void addBehavior(UserBehavior userBehavior){
        if (!contains(userBehavior)){
            mBehaviors.put(userBehavior.getId(), userBehavior);

            for (UserGoal goal:userBehavior.getGoals()){
                getGoal(goal).addBehavior(userBehavior);
            }
        }
    }

    /**
     * Remove a single Behavior from a user's collection. Doing this will also remove any
     * references to the Behavior from the parent Goal, and it will also remove the
     * child Actions.
     *
     * @param behavior the Behavior instance to remove.
     */
    public void removeBehavior(Behavior behavior){
        UserBehavior removedBehavior = mBehaviors.remove(behavior.getId());

        if (removedBehavior != null){
            //Remove the behavior from any parent Goals
            for (UserGoal goal:removedBehavior.getGoals()){
                goal.removeBehavior(removedBehavior);
            }

            //Remove the child Actions
            for (UserAction action:removedBehavior.getActions()){
                mActions.remove(action.getAction().getId());
            }
        }
    }


    /*------------------------*
     * ACTION RELATED METHODS *
     *------------------------*/

    /**
     * Retrieve a list of all user-selected Actions.
     *
     * @return an ArrayList of Action objects.
     */
    public Map<Integer, UserAction> getActions(){
        return mActions;
    }

    /**
     * Returns the original copy of the provided action.
     *
     * @param action the action whose original copy needs to be fetched.
     * @return the original copy of such action.
     */
    public UserAction getAction(Action action){
        return mActions.get(action.getId());
    }

    public UserAction getAction(UserAction userAction){
        return getAction(userAction.getAction());
    }

    public boolean contains(UserAction userAction){
        return mActions.containsKey(userAction.getAction().getId());
    }

    /**
     *  Add an individual action the user's collection. Adding an action will also
     *  update the user's selected Behaviors, including a reference to the new
     *  action within any relevant parent Behavior.
     *
     * @param userAction the action to be added to the list.
     */
    public void addAction(UserAction userAction){
        if (!contains(userAction)){
            mActions.put(userAction.getAction().getId(), userAction);

            UserBehavior behavior = getBehavior(userAction.getBehavior());
            behavior.addAction(userAction);
            userAction.setBehavior(behavior);
        }
    }

    /**
     * Remove a single Action from a user's collection. Doing this will also remove
     * any reference to that Action from it's parent Behaviors.
     *
     * @param action the Action object to remove.
     */
    public void removeAction(Action action){
        UserAction removedAction = mActions.remove(action.getId());
        if (removedAction != null){
            removedAction.getBehavior().removeAction(removedAction);
        }
    }


    /*----------------------------------------------------------*
     * SYNCHRONIZATION METHODS. I REALLY WANT TO DEPRECATE THIS *
     *----------------------------------------------------------*/

    /**
     * Sync up all parent/child objects.
     */
    public void sync(){
        assignGoalsToCategories();
        assignBehaviorsToGoals();
        assignActionsToBehaviors();
        //setActionParents();
    }

    /**
     * Goals are associated with one or more categories. This method will include
     * the user's selected goals within their selected Categories.
     */
    public void assignGoalsToCategories(){
        // add each goal to the correct category
        for (UserCategory category:getCategories().values()){
            ArrayList<UserGoal> categoryGoals = new ArrayList<>();
            for (UserGoal goal:category.getGoals()){
                goal = getGoal(goal);
                if (goal != null){
                    categoryGoals.add(goal);
                }
            }
            category.setGoals(categoryGoals);
        }
    }

    /**
     * Behaviors are contained within a Goal. This method will take the list of
     * selected behaviors, and associate them with the user's selected goals.
     */
    public void assignBehaviorsToGoals(){
        //Look at all the selected goals
        for (UserGoal goal:getGoals().values()){
            ArrayList<UserBehavior> goalBehaviors = new ArrayList<>();
            for (UserBehavior userBehavior:getBehaviors().values()){
                if (userBehavior.getGoals().contains(goal)){
                    goalBehaviors.add(userBehavior);
                }
            }/*
            for (Behavior behavior:goal.getBehaviors()){
                behavior = getBehavior(behavior);
                if (behavior != null){
                    goalBehaviors.add(behavior);
                }
            }*/
            goal.setBehaviors(goalBehaviors);
        }
    }

    /**
     * Update a single Action in a user's collection; When some Action has been changed
     * (e.g. it's custom-trigger updated), this method will replace stale instances of
     * the Action with the new version.
     *
     * Updating an Action will also trigger an update to the parent behaviors.
     *
     * @param action the Action object to update.
     */
    /*public void updateAction(Action action){
        //Given a single action, find it in the list of Actions and keep the input version
        int i = 0;
        boolean found = false;
        for (Action a:mActions.values()){
            if (a.getId() == action.getId()){
                found = true;
                break;
            }
            i++;
        }
        if(found){
            //Remove the old action
            removeAction(mActions.get(i));
        }
        mActions.add(action);
        assignActionsToBehaviors();
    }*/

    /**
     * Actions are contained within a single Behavior. This method will take the list
     * of selected actions, and associate them with the user-selected behaviors.
     *
     * This ensures that all behaviors have a valid reference to their child Actions.
     */
    public void assignActionsToBehaviors(){
        for (UserBehavior behavior:getBehaviors().values()){
            List<UserAction> behaviorActions = new ArrayList<>();
            for (UserAction action:behavior.getActions()){
                action = getAction(action);
                if (action != null){
                    behaviorActions.add(action);
                    action.setBehavior(behavior);
                }
            }
            behavior.setActions(behaviorActions);
        }

        // now, set each Action's parent Behavior.
        //setActionParents();
    }

    /**
     * This method will set the Behavior attribute for all of the user's selected Actions, so the
     * Action will contain a valid reference to its parent.
     */
    public void setActionParents(){
        // set a reference to the parent for each action.
        for (UserAction action:getActions().values()){
            for (UserBehavior behavior:getBehaviors().values()){
                if (action.getBehavior().getId() == behavior.getId()){
                    action.setBehavior(behavior);
                    break;
                }
            }
        }
    }


    /*-----------------------*
     * PLACE RELATED METHODS *
     *-----------------------*/

    /**
     * Sets the list of user set places.
     *
     * @param places the list of places set by the user.
     */
    public void setPlaces(List<Place> places){
        mPlaces = places;
    }

    /**
     * Gets the list of user set places.
     *
     * @return the list of places set by te user.
     */
    public List<Place> getPlaces(){
        return mPlaces;
    }

    /**
     * Adds a place to the list of places set bu the user.
     *
     * @param place the place to be added.
     */
    public void addPlace(Place place){
        mPlaces.add(place);
    }


    /*---------------------------*
     * FEED DATA RELATED METHODS *
     *---------------------------*/

    /**
     * Sets the feed data retrieved from the API.
     *
     * @param feedData the object containing the information to be displayed in the feed.
     */
    public void setFeedData(FeedData feedData){
        mFeedData = feedData;
    }

    /**
     * Gets the feed data.
     *
     * @return the object containing the information to be displayed in the feed.
     */
    public FeedData getFeedData(){
        return mFeedData;
    }


    /*------------------------------------*
     * LOG RELATED METHODS. FOR DEBUGGING *
     *------------------------------------*/

    /**
     * Log the value of individual private data members.
     */
    public void logData() {
        Log.d(TAG, "Categories.");
        for (UserCategory item:mCategories.values()){
            Log.d(TAG, "- " + item.toString());
            Log.d(TAG, "--> contains " + item.getGoals().size() + " goals");
        }
        Log.d(TAG, "Goals.");
        for (UserGoal item:mGoals.values()){
            Log.d(TAG, "- " + item.toString());
            Log.d(TAG, "--> contains " + item.getCategories().size() + " categories");
            Log.d(TAG, "--> contains " + item.getBehaviors().size() + " behaviors");
        }
        Log.d(TAG, "Behaviors.");
        for (UserBehavior item:mBehaviors.values()){
            Log.d(TAG, "- " + item.toString());
            Log.d(TAG, "--> contains " + item.getGoals().size() + " goals");
            Log.d(TAG, "--> contains " + item.getActions().size() + " actions");
        }
        Log.d(TAG, "Actions.");
        for (UserAction item:mActions.values()){
            Log.d(TAG, "- " + item.toString());
            Log.d(TAG, "--> contains: " + item.getBehavior().toString());
        }
    }

    /**
     * Given a goal, Log information for it's parent Categories.
     *
     * @param userGoal the goal whose information needs to be logged.
     */
    public void logParentCategories(UserGoal userGoal){
        String output = "NONE";
        if (!userGoal.getCategories().isEmpty()){
            output = "";
            for (UserCategory userCategory:userGoal.getCategories()){
                output += "(" + userCategory.getCategoryId() + ") " + userCategory.getTitle() + ", ";
            }
        }
        Log.d(TAG, "- (parents) -> " + output);
    }

    /**
     * Given a behavior, log information for it's parent Goals.
     *
     * @param userBehavior the behavior whose information needs to be logged.
     */
    public void logParentGoals(UserBehavior userBehavior){
        String output = "NONE";
        if (!userBehavior.getGoals().isEmpty()){
            output = "";
            for (UserGoal userGoal:userBehavior.getGoals()){
                output += "(" + userGoal.getGoalId() + ") " + userGoal.getTitle() + ", ";
            }
        }
        Log.d(TAG, "-- (parents) ->" + output);
    }

    /**
     * Given an Action, log information for it's parent Behavior
     *
     * @param userAction the action whose information needs to be logged.
     */
    public void logParentBehavior(UserAction userAction){
        String output = "NONE";
        UserBehavior userBehavior = userAction.getBehavior();
        if (userBehavior != null){
            output = "(" + userBehavior.getBehaviorId() + ") " + userBehavior.getTitle();
        }
        Log.d(TAG, "--- (parent)-> " + output);
    }

    /**
     * Log the hierarchy of all User-selected data.
     */
    public void logSelectedData(){
        logSelectedData("User Data");
    }

    /**
     * Log the hierarchy of all User-selected data, with a custom title.
     *
     * @param title a custom title to display above the output.
     *
     */
    public void logSelectedData(String title){
        logSelectedData(title, true);
    }

    /**
     * Log the hierarchy of all User-selected data, with a custom title, and an option to
     * include each level's parent data.
     *
     * @param title a custom title to display above the output.
     * @param includeParents if true, will Log each item's parent objects.
     */
    public void logSelectedData(String title, boolean includeParents){
        // Log user-selected categories, goals, behaviors, actions
        Log.d(TAG, "------------- " + title + " --------------- ");
        for (UserCategory userCategory:getCategories().values()){
            Log.d(TAG, "CATEGORY: " + userCategory.getTitle());
            for (UserGoal userGoal:userCategory.getGoals()){
                Log.d(TAG, "- GOAL: " + userGoal.getTitle());
                if (includeParents){
                    logParentCategories(userGoal);
                }
                for (UserBehavior userBehavior:userGoal.getBehaviors()){
                    Log.d(TAG, "-- BEHAVIOR: " + userBehavior.getTitle());
                    if (includeParents){
                        logParentGoals(userBehavior);
                    }
                    for (UserAction userAction:userBehavior.getActions()){
                        Log.d(TAG, "--- ACTION: " + userAction.getTitle());
                        if (includeParents){
                            logParentBehavior(userAction);
                        }
                    }
                }
            }
        }
        Log.d(TAG, "------------------------------------------- ");
    }
}
