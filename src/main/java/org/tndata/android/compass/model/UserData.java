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
 * This class also contains the data to be displayed at the main feed.
 *
 * @author Brad Montgomery
 * @author Ismael Alonso
 * @version 2.0.0
 */
public class UserData{
    private static final String TAG = "UserData";


    //User selected content. id -> content maps
    private Map<Integer, UserCategory> categories;
    private Map<Integer, UserGoal> goals;
    private Map<Integer, UserBehavior> behaviors;
    private Map<Integer, UserAction> actions;

    //User places
    private List<UserPlace> places = new ArrayList<>();

    //Data for the main feed
    private FeedData feed_data;


    /**
     * Constructor.
     */
    public UserData(){
        categories = new HashMap<>();
        goals = new HashMap<>();
        behaviors = new HashMap<>();
        actions = new HashMap<>();
    }


    /*--------------------CONTENT SETTERS--------------------*
     * These methods are meant to be used by the parser only *
     *-------------------------------------------------------*/

    public void setCategories(@NonNull Map<Integer, UserCategory> categories){
        this.categories = categories;
    }

    public void setGoals(@NonNull Map<Integer, UserGoal> goals){
        this.goals = goals;
    }

    public void setBehaviors(@NonNull Map<Integer, UserBehavior> behaviors){
        this.behaviors = behaviors;
    }

    public void setActions(@NonNull Map<Integer, UserAction> actions){
        this.actions = actions;
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
        return categories;
    }

    /**
     * Returns the original copy of the provided category.
     *
     * @param category the category whose original copy needs to be fetched.
     * @return the original copy of such category.
     */
    public UserCategory getCategory(Category category){
        return categories.get(category.getId());
    }

    public UserCategory getCategory(UserCategory userCategory){
        return getCategory(userCategory.getCategory());
    }

    public boolean contains(UserCategory userCategory){
        return categories.containsKey(userCategory.getCategory().getId());
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
            categories.put(userCategory.getCategory().getId(), userCategory);

            //Link goals
            List<UserGoal> goals = new ArrayList<>();
            for (UserGoal categoryGoal : userCategory.getGoals()){
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
        UserCategory removedCategory = categories.remove(category.getId());

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
        return goals;
    }

    /**
     * Returns the original copy of the provided goal.
     *
     * @param goal the goal whose original copy needs to be fetched.
     * @return the original copy of such goal.
     */
    public UserGoal getGoal(Goal goal){
        return goals.get(goal.getId());
    }

    public UserGoal getGoal(UserGoal userGoal){
        return getGoal(userGoal.getGoal());
    }

    public boolean contains(UserGoal userGoal){
        return goals.containsKey(userGoal.getGoal().getId());
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
            goals.put(userGoal.getGoal().getId(), userGoal);

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
        UserGoal removedGoal = goals.remove(goal.getId());

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
        return behaviors;
    }

    /**
     * Returns the original copy of the provided behavior.
     *
     * @param behavior the behavior whose original copy needs to be fetched.
     * @return the original copy of such behavior.
     */
    public UserBehavior getBehavior(Behavior behavior){
        return behaviors.get(behavior.getId());
    }

    public UserBehavior getBehavior(UserBehavior userBehavior){
        return getBehavior(userBehavior.getBehavior());
    }

    public boolean contains(UserBehavior userBehavior){
        return behaviors.containsKey(userBehavior.getBehavior().getId());
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
            behaviors.put(userBehavior.getId(), userBehavior);

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
        UserBehavior removedBehavior = behaviors.remove(behavior.getId());

        if (removedBehavior != null){
            //Remove the behavior from any parent Goals
            for (UserGoal goal:removedBehavior.getGoals()){
                goal.removeBehavior(removedBehavior);
            }

            //Remove the child Actions
            for (UserAction action:removedBehavior.getActions()){
                actions.remove(action.getAction().getId());
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
        return actions;
    }

    /**
     * Returns the original copy of the provided action.
     *
     * @param action the action whose original copy needs to be fetched.
     * @return the original copy of such action.
     */
    public UserAction getAction(Action action){
        return actions.get(action.getId());
    }

    public UserAction getAction(UserAction userAction){
        return getAction(userAction.getAction());
    }

    public boolean contains(UserAction userAction){
        return actions.containsKey(userAction.getAction().getId());
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
            actions.put(userAction.getAction().getId(), userAction);

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
        UserAction removedAction = actions.remove(action.getId());
        if (removedAction != null){
            removedAction.getBehavior().removeAction(removedAction);
        }
    }


    /*-------------------------*
     * SYNCHRONIZATION METHODS *
     *-------------------------*/

    /**
     * Sync up all parent/child objects. This method should only be used at startup,
     * from that point on, the lists are supposed to be kept up to date.
     */
    public void sync(){
        linkGoalsAndCategories();
        linkBehaviorsAndGoals();
        linkActions();
        feed_data.sync(this);
    }

    /**
     * Generates the inner lists of parents and children for goals and categories, respectively.
     */
    private void linkGoalsAndCategories(){
        //Add each goal to the correct category and vice versa
        for (UserGoal userGoal: goals.values()){
            for (Integer categoryId:userGoal.getGoal().getCategories()){
                if (categories.containsKey(categoryId)){
                    UserCategory userCategory = categories.get(categoryId);
                    userCategory.addGoal(userGoal);
                    userGoal.addCategory(userCategory);
                }
            }
            userGoal.setPrimaryCategory(categories.get(userGoal.getPrimaryCategoryId()));
        }
    }

    /**
     * Generates the inner lists of parents and children for behaviors and goals, respectively.
     */
    private void linkBehaviorsAndGoals(){
        //Look at all the selected goals
        for (UserBehavior userBehavior: behaviors.values()){
            for (Integer goalId:userBehavior.getBehavior().getGoals()){
                if (goals.containsKey(goalId)){
                    UserGoal userGoal = goals.get(goalId);
                    userGoal.addBehavior(userBehavior);
                    userBehavior.addGoal(userGoal);
                }
            }
        }
    }

    /**
     * Generates the inner list of children for behaviors and sets the parents for actions.
     */
    public void linkActions(){
        for (UserAction userAction: actions.values()){
            UserBehavior userBehavior = behaviors.get(userAction.getAction().getBehavior());
            userBehavior.addAction(userAction);
            userAction.setBehavior(userBehavior);
            userAction.setPrimaryGoal(goals.get(userAction.getPrimaryGoalId()));
            userAction.setPrimaryCategory(categories.get(userAction.getPrimaryCategoryId()));
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
    public void setPlaces(List<UserPlace> places){
        this.places = places;
    }

    /**
     * Gets the list of user set places.
     *
     * @return the list of places set by te user.
     */
    public List<UserPlace> getPlaces(){
        return places;
    }

    /**
     * Adds a place to the list of places set bu the user.
     *
     * @param place the place to be added.
     */
    public void addPlace(UserPlace place){
        places.add(place);
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
        feed_data = feedData;
    }

    /**
     * Gets the feed data.
     *
     * @return the object containing the information to be displayed in the feed.
     */
    public FeedData getFeedData(){
        return feed_data;
    }


    /*------------------------------------*
     * LOG RELATED METHODS. FOR DEBUGGING *
     *------------------------------------*/

    /**
     * Log the value of individual private data members.
     */
    public void logData() {
        Log.d(TAG, "Categories.");
        for (UserCategory item:categories.values()){
            Log.d(TAG, "- " + item.toString());
            Log.d(TAG, "--> contains " + item.getGoals().size() + " goals");
        }
        Log.d(TAG, "Goals.");
        for (UserGoal item:goals.values()){
            Log.d(TAG, "- " + item.toString());
            Log.d(TAG, "--> contains " + item.getCategories().size() + " categories");
            Log.d(TAG, "--> contains " + item.getBehaviors().size() + " behaviors");
        }
        Log.d(TAG, "Behaviors.");
        for (UserBehavior item:behaviors.values()){
            Log.d(TAG, "- " + item.toString());
            Log.d(TAG, "--> contains " + item.getGoals().size() + " goals");
            Log.d(TAG, "--> contains " + item.getActions().size() + " actions");
        }
        Log.d(TAG, "Actions.");
        for (UserAction item:actions.values()){
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
                output += "(" + userCategory.getObjectId() + ") " + userCategory.getTitle() + ", ";
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
                output += "(" + userGoal.getObjectId() + ") " + userGoal.getTitle() + ", ";
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
            output = "(" + userBehavior.getObjectId() + ") " + userBehavior.getTitle();
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
