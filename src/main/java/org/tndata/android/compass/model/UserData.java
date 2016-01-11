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


    //User selected content. There are the master lists. References to all the objects in
    //  the data structure are placed here. All other lists to be used within this class
    //  should contain references to these same exact objects, not references to other
    //  equivalent objects. This makes easier the access to and modification of the data
    //  structure. These lists are deprecated and will eventually be removed, use the
    //  <Id -> Content> maps instead for improved performance.
    //private List<Category> mCategories = new ArrayList<>();
    //private List<Goal> mGoals = new ArrayList<>();
    //private List<Behavior> mBehaviors = new ArrayList<>();
    //private List<Action> mActions = new ArrayList<>();

    private Map<Integer, Category> mCategories;
    private Map<Integer, Goal> mGoals;
    private Map<Integer, Behavior> mBehaviors;
    private Map<Integer, Action> mActions;

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

    public void setCategories(@NonNull Map<Integer, Category> categories){
        mCategories = categories;
    }

    public void setGoals(@NonNull Map<Integer, Goal> goals){
        mGoals = goals;
    }

    public void setBehaviors(@NonNull Map<Integer, Behavior> behaviors){
        mBehaviors = behaviors;
    }

    public void setActions(@NonNull Map<Integer, Action> actions){
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
    public Map<Integer, Category> getCategories(){
        return mCategories;
    }

    /**
     * Returns the original copy of the provided category.
     *
     * @param category the category whose original copy needs to be fetched.
     * @return the original copy of such category.
     */
    public Category getCategory(Category category){
        return mCategories.get(category.getId());
    }

    /**
     * Returns all of the goals for a given Category. This is a wrapper for category.getGoals().
     *
     * @return a List of Goal objects.
     */
    public List<Goal> getCategoryGoals(Category category){
        return getCategory(category).getGoals();
    }

    /**
     * Adds a Category to the list of user-selected categories (if it's not
     * already included) and assigns any user-selected goals to the new Category.
     *
     * @param category the Category object to add.
     */
    public void addCategory(Category category){
        //If the category ain't in the data set
        if(!mCategories.containsKey(category.getId())){
            //Add it
            mCategories.put(category.getId(), category);

            //Link goals
            List<Goal> goals = new ArrayList<>();
            for (Goal categoryGoal:category.getGoals()){
                Goal goal = getGoal(categoryGoal);
                goal.addCategory(category);
                goals.add(goal);
            }
            category.setGoals(goals);
        }
    }

    /**
     * Remove a single Category from the user's collection. This also removes the
     * reference to that category from all Goals.
     *
     * @param category the category to remove
     */
    public void removeCategory(Category category){
        category = mCategories.remove(category.getId());

        List<Goal> toRemove = new ArrayList<>();
        //Remove this category from any child goals
        for (Goal goal:category.getGoals()){
            goal.removeCategory(category);
            //Record all the Goals w/o parent Categories
            if (goal.getCategories().isEmpty()){
                toRemove.add(goal);
            }
        }
        //Remove all the Goals w/o parent Categories
        for (Goal goal:toRemove){
            removeGoal(goal);
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
    public Map<Integer, Goal> getGoals(){
        return mGoals;
    }

    /**
     * Returns the original copy of the provided goal.
     *
     * @param goal the goal whose original copy needs to be fetched.
     * @return the original copy of such goal.
     */
    public Goal getGoal(Goal goal){
        return mGoals.get(goal.getId());
    }

    /**
     * Add a goal to the list of user selected Goals.
     *
     * Adding a Goal also assigns it to any existing user-selected categories,
     * as well as assigning any existing Behaviors to the Goal.
     *
     * @param goal the goal to be added to the user list.
     */
    public void addGoal(Goal goal){
        //If the goal ain't in the data set
        if (!mGoals.containsKey(goal.getId())){
            //Add it
            mGoals.put(goal.getId(), goal);

            //Add it to the relevant categories
            for (Category category:goal.getCategories()){
                Category cat = getCategory(category);
                if (cat != null){
                    cat.addGoal(goal);
                }
            }

            //Link behaviors
            List<Behavior> behaviors = new ArrayList<>();
            for (Behavior goalBehavior:goal.getBehaviors()){
                Behavior behavior = getBehavior(goalBehavior);
                behavior.addGoal(goal);
                behaviors.add(behavior);
            }
            goal.setBehaviors(behaviors);
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
        goal = mGoals.remove(goal.getId());

        // Remove the goal from its parent categories
        for(Category category:goal.getCategories()){
            category.removeGoal(goal);
        }

        List<Behavior> toRemove = new ArrayList<>();
        //Remove the goal from its child Behaviors
        for (Behavior behavior:goal.getBehaviors()){
            behavior.removeGoal(goal);
            //Record all the Behaviors w/o parent Goals
            if (behavior.getGoals().isEmpty()){
                toRemove.add(behavior);
            }
        }
        //Remove Behaviors w/o parent Goals
        for (Behavior behavior:toRemove){
            removeBehavior(behavior);
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
    public Map<Integer, Behavior> getBehaviors(){
        return mBehaviors;
    }

    /**
     * Returns the original copy of the provided behavior.
     *
     * @param behavior the behavior whose original copy needs to be fetched.
     * @return the original copy of such behavior.
     */
    public Behavior getBehavior(Behavior behavior){
        return mBehaviors.get(behavior.getId());
    }

    /**
     * Add a single Behavior to the user's collection.
     *
     * Adding this object will also trigger an update to behavior's parent goals.
     *
     * @param behavior the behavior to be added to the user's list.
     */
    public void addBehavior(Behavior behavior){
        if (!mBehaviors.containsKey(behavior.getId())){
            mBehaviors.put(behavior.getId(), behavior);

            for (Goal goal:behavior.getGoals()){
                getGoal(goal).addBehavior(behavior);
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
        behavior = mBehaviors.remove(behavior.getId());

        //Remove the behavior from any parent Goals
        for (Goal goal:behavior.getGoals()){
            goal.removeBehavior(behavior);
        }

        //Remove the child Actions
        for (Action action:behavior.getActions()){
            mActions.remove(action.getId());
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
    public Map<Integer, Action> getActions(){
        return mActions;
    }

    /**
     * Returns the original copy of the provided action.
     *
     * @param action the action whose original copy needs to be fetched.
     * @return the original copy of such action.
     */
    public Action getAction(Action action){
        return mActions.get(action.getId());
    }

    /**
     *  Add an individual action the user's collection. Adding an action will also
     *  update the user's selected Behaviors, including a reference to the new
     *  action within any relevant parent Behavior.
     *
     * @param action the action to be added to the list.
     */
    public void addAction(Action action){
        if (!mActions.containsKey(action.getId())){
            mActions.put(action.getId(), action);

            Behavior behavior = getBehavior(action.getBehavior());
            behavior.addAction(action);
            action.setBehavior(behavior);
        }
    }

    /**
     * Remove a single Action from a user's collection. Doing this will also remove
     * any reference to that Action from it's parent Behaviors.
     *
     * @param action the Action object to remove.
     */
    public void removeAction(Action action){
        action = mActions.remove(action.getId());
        action.getBehavior().removeAction(action);
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
        for (Category category:getCategories().values()){
            ArrayList<Goal> categoryGoals = new ArrayList<>();
            for (Goal goal:category.getGoals()){
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
        for (Goal goal:getGoals().values()){
            ArrayList<Behavior> goalBehaviors = new ArrayList<>();
            for (Behavior behavior:goal.getBehaviors()){
                behavior = getBehavior(behavior);
                if (behavior != null){
                    goalBehaviors.add(behavior);
                }
            }
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
        for (Behavior behavior:getBehaviors().values()){
            List<Action> behaviorActions = new ArrayList<>();
            for (Action action:behavior.getActions()){
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
        for (Action action:getActions().values()){
            for (Behavior behavior:getBehaviors().values()){
                if (action.getBehavior_id() == behavior.getId()){
                    action.setBehavior(behavior);
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
        for (Category item:mCategories.values()){
            Log.d(TAG, "- (" + item.getId() + ") " + item.getTitle());
            Log.d(TAG, "--> contains " + item.getGoals().size() + " goals");
        }
        Log.d(TAG, "Goals.");
        for (Goal item:mGoals.values()){
            Log.d(TAG, "- (" + item.getId() + ") " + item.getTitle());
            Log.d(TAG, "--> contains " + item.getCategories().size() + " categories");
            Log.d(TAG, "--> contains " + item.getBehaviors().size() + " behaviors");
        }
        Log.d(TAG, "Behaviors.");
        for (Behavior item:mBehaviors.values()){
            Log.d(TAG, "- (" + item.getId() + ") " + item.getTitle());
            Log.d(TAG, "--> contains " + item.getGoals().size() + " goals");
            Log.d(TAG, "--> contains " + item.getActions().size() + " actions");
        }
        Log.d(TAG, "Actions.");
        for (Action item:mActions.values()){
            Log.d(TAG, "- (" + item.getId() + ") " + item.getTitle());
            Log.d(TAG, "--> contains, behavior_id = " + item.getBehavior_id());
        }
    }

    /**
     * Given a goal, Log information for it's parent Categories.
     *
     * @param goal the goal whose information needs to be logged.
     */
    public void logParentCategories(Goal goal){
        String output = "NONE";
        if (!goal.getCategories().isEmpty()){
            output = "";
            for (Category c:goal.getCategories()){
                output += "(" + c.getId() + ") " + c.getTitle() + ", ";
            }
        }
        Log.d(TAG, "- (parents) -> " + output);
    }

    /**
     * Given a behavior, log information for it's parent Goals.
     *
     * @param behavior the behavior whose information needs to be logged.
     */
    public void logParentGoals(Behavior behavior){
        String output = "NONE";
        if (!behavior.getGoals().isEmpty()){
            output = "";
            for (Goal g:behavior.getGoals()){
                output += "(" + g.getId() + ") " + g.getTitle() + ", ";
            }
        }
        Log.d(TAG, "-- (parents) ->" + output);
    }

    /**
     * Given an Action, log information for it's parent Behavior
     *
     * @param action the action whose information needs to be logged.
     */
    public void logParentBehavior(Action action){
        String output = "NONE";
        if (action.getBehavior() != null){
            output = "(" + action.getBehavior().getId() + ") " + action.getBehavior().getTitle();
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
     * @param include_parents if true, will Log each item's parent objects.
     */
    public void logSelectedData(String title, boolean include_parents){
        // Log user-selected categories, goals, behaviors, actions
        Log.d(TAG, "------------- " + title + " --------------- ");
        for(Category c:getCategories().values()){
            Log.d(TAG, "CATEGORY: " + c.getTitle());
            for (Goal g:c.getGoals()){
                Log.d(TAG, "- GOAL: " + g.getTitle());
                if (include_parents){
                    logParentCategories(g);
                }
                for (Behavior b:g.getBehaviors()){
                    Log.d(TAG, "-- BEHAVIOR: " + b.getTitle());
                    if (include_parents){
                        logParentGoals(b);
                    }
                    for (Action a:b.getActions()){
                        Log.d(TAG, "--- ACTION: " + a.getTitle());
                        if (include_parents){
                            logParentBehavior(a);
                        }
                    }
                }
            }
        }
        Log.d(TAG, "------------------------------------------- ");
    }
}
