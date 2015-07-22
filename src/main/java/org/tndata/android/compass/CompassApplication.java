package org.tndata.android.compass;

import android.app.Application;
import android.util.Log;

import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.util.ImageLoader;

import java.util.ArrayList;

public class CompassApplication extends Application {
    private String TAG = "CompassApplication";
    private String mToken;
    private User mUser; // The logged-in user
    private ArrayList<Category> mCategories = new ArrayList<Category>(); // The user's selected Categories
    private ArrayList<Goal> mGoals = new ArrayList<Goal>();  // The user's selected Goals
    private ArrayList<Behavior> mBehaviors = new ArrayList<Behavior>(); // The user's selected behaviors
    private ArrayList<Action> mActions = new ArrayList<Action>(); // The user's selected actions

    public CompassApplication() {
        super();
    }

    public void setToken(String token) {
        mToken = token;
    }

    public String getToken() {
        return mToken;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public ArrayList<Category> getCategories() {
        return mCategories;
    }

    public void setCategories(ArrayList<Category> categories) {
        mCategories = categories;

        for(Category c: categories) {
            Log.d(TAG, "--> setCategory: " + c.getTitle());
        }

        logSelectedData("AFTER CompassApplication.setCategories");
    }

    /* Remove a single Category from the user's collection */
    public void removeCategory(Category category) {
        mCategories.remove(category);

        // also remove this category from any child goals
        for(Goal goal : mGoals) {
            goal.removeCategory(category);
        }
        logSelectedData("AFTER CompassApplication.removeCategory");
    }

    /* Returns all of the goals for a given Category */
    public ArrayList<Goal> getCategoryGoals(Category category) {
        return category.getGoals();
    }

    public ArrayList<Goal> getGoals() {
        return mGoals;
    }

    public void setGoals(ArrayList<Goal> goals) {
        mGoals = goals;
        for(Goal g: goals) {
            Log.d(TAG, "--> setGoals: " + g.getTitle());
        }
        assignGoalsToCategories();
        logSelectedData("AFTER CompassApplication.setGoals");
    }

    public void addGoal(Goal goal) {
        if(!mGoals.contains(goal)) {
            mGoals.add(goal);
            assignGoalsToCategories();
        }
        logSelectedData("AFTER CompassApplication.addGoal: " + goal.getTitle());
    }

    /* Remove a single Goal from the user's collection */
    public void removeGoal(Goal goal) {
        mGoals.remove(goal);

        // Remove the goal from its parent categories
        for(Category category : mCategories) {
            category.removeGoal(goal);
        }

        logSelectedData("AFTER CompassApplication.removeGoal: " + goal.getTitle());
    }

    /**
     * Goals are associated with one or more categories. This method will include
     * the user's selected goals within their selected Categories.
     */
    public void assignGoalsToCategories() {
        // add each goal to the correct category
        for (Category category : getCategories()) {
            ArrayList<Goal> categoryGoals = new ArrayList<Goal>();
            for (Goal goal : getGoals()) {
                for (Category goalCategory : goal.getCategories()) {
                    if (goalCategory.getId() == category.getId()) {
                        categoryGoals.add(goal);
                        break;
                    }
                }
            }
            category.setGoals(categoryGoals);
        }
    }

    public void setBehaviors(ArrayList<Behavior> behaviors) {
        mBehaviors = behaviors;
        for(Behavior b: behaviors) {
            Log.d(TAG, "--> setBehavior: " + b.getTitle());
        }
        assignBehaviorsToGoals();
        logSelectedData("AFTER CompassApplication.setBehaviors");
    }

    public ArrayList<Behavior> getBehaviors() {
        return mBehaviors;
    }

    /* Remove a single Behavior from a user's collection */
    public void removeBehavior(Behavior behavior) {
        mBehaviors.remove(behavior);

        // Remove the behavior from any Goals
        for(Goal goal : getGoals()) {
            goal.removeBehavior(behavior);
        }
        logSelectedData("AFTER CompassApplication.removeBehavior: " + behavior.getTitle());
    }

    public void addBehavior(Behavior behavior) {
        if(!mBehaviors.contains(behavior)) {
            mBehaviors.add(behavior);
            assignBehaviorsToGoals();
            logSelectedData("AFTER CompassApplication.addBehavior: " + behavior.getTitle());
        }
    }

    /** Behaviors are contained within a Goal. This method will take the list
     * of selected behaviors, and associate them with the user's selected goals.
     */
    public void assignBehaviorsToGoals() {
        for (Goal goal : getGoals()) { // Look at all the selected goals
            ArrayList<Behavior> goalBehaviors = new ArrayList<Behavior>();
            for (Behavior behavior : getBehaviors()) { // look at all the selected behaviors...
                for (Goal behaviorGoal : behavior.getGoals()) { // The Behavior's Parent goals
                    if (behaviorGoal.getId() == goal.getId()) {
                        goalBehaviors.add(behavior);
                        break;
                    }
                }
            }
            goal.setBehaviors(goalBehaviors);
        }
    }

    public ArrayList<Action> getActions() {
        return mActions;
    }

    /* Remove a single Action from a user's collection */
    public void removeAction(Action action) {
        mActions.remove(action);

        // Remove the action from any related behaviors
        for(Behavior behavior : mBehaviors) {
            behavior.removeAction(action);
        }

        logSelectedData("AFTER CompassApplication.removeAction: " + action.getTitle());
    }

    public void setActions(ArrayList<Action> actions) {
        mActions = actions;
        for(Action a: actions) {
            Log.d(TAG, "--> setAction: " + a.getTitle());
        }
        assignActionsToBehaviors();
        logSelectedData("AFTER CompassApplication.setActions");
    }

    /* Add an individual action the user's collection */
    public void addAction(Action action) {
        if(!mActions.contains(action)) {
            mActions.add(action);
            assignActionsToBehaviors();
            logSelectedData("AFTER CompassApplication.addAction: " + action.getTitle());
        }
    }

    /* update a single Action in a user's collection */
    public void updateAction(Action action) {
        // Given a single action, find it in the list of Actions and keep the input version
        int i = 0;
        boolean found = false;
        for (Action a : mActions) {
            if(a.getId() == action.getId()) {
                found = true;
                break;
            }
            i++;
        }
        if(found) {
            // remove the old action
            mActions.remove(i);
        }
        mActions.add(action);
    }

    /** Actions are contained within a single Behavior. This method will take the list
     * of selected actions, and associate them with the user's selected behaviors.
     */
    public void assignActionsToBehaviors() {

        for (Behavior behavior : getBehaviors()) {
            ArrayList<Action> behaviorActions = new ArrayList<Action>();
            for (Action action : getActions()) {
                if (behavior.getId() == action.getBehavior_id()) {
                    behaviorActions.add(action);
                    break;
                }
            }
            behavior.setActions(behaviorActions);
        }

        // now, set each Action's parent Behavior.
        setActionParents();
    }

    /**
    * This method will set the Behavior attribute for all of the user's selected Actions, so the
    * Action will contain a reference to its parent.
    */
    public void setActionParents() {
        // set a reference to the parent for each action.
        for(Action action : getActions()) {
            for(Behavior behavior : getBehaviors()) {
                if(action.getBehavior_id() == behavior.getId()) {
                    action.setBehavior(behavior);
                }
            }
        }
    }

    public void logSelectedGoalData(String title) {
        // Log user-selected goals, behaviors, actions
        Log.d(TAG, "------------- " + title + " --------------- ");
        for(Goal g : getGoals()) {
            Log.d(TAG, "-- GOAL --> " + g.getTitle());
            for(Behavior b : g.getBehaviors()) {
                Log.d(TAG, "---- Behavior --> " + b.getTitle());
                for(Action a : b.getActions()) {
                    Log.d(TAG, "------ Action --> " + a.getTitle());
                }
            }
        }
    }

    public void logSelectedData(String title) {
        // Log user-selected categories, goals, behaviors, actions
        Log.d(TAG, "------------- " + title + " --------------- ");
        for(Category c : getCategories()) {
            Log.d(TAG, "CATEGORY --> " + c.getTitle());
            for (Goal g : c.getGoals()) {
                Log.d(TAG, "-- GOAL --> " + g.getTitle());
                for (Behavior b : g.getBehaviors()) {
                    Log.d(TAG, "---- BEHAVIOR --> " + b.getTitle());
                    for (Action a : b.getActions()) {
                        Log.d(TAG, "------ ACTION --> " + a.getTitle());
                    }
                }
            }
        }
        Log.d(TAG, "------------------------------------------- ");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        ImageLoader.initialize(getApplicationContext());
    }
}
