package org.tndata.android.compass;

import android.app.Application;
import android.util.Log;

import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.User;

import java.util.ArrayList;

public class CompassApplication extends Application {
    private String TAG = "CompassApplication";
    private String mToken;
    private User mUser; // The logged-in user
    private ArrayList<Category> mCategories = new ArrayList<Category>(); // The user's selected Categories
    private ArrayList<Goal> mGoals = new ArrayList<Goal>();  // The user's selected Goals
    private ArrayList<Behavior> mBehaviors = new ArrayList<Behavior>(); // The user's selected behaviors
    private ArrayList<Action> mActions = new ArrayList<Action>(); // The user's selected actions

    // Need
    // - Goals organized by Category
    // - Behaviors organized by Goal
    // - Actions organized by Behavior

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
    }

    /* Remove a single Category from the user's collection */
    public void removeCategory(Category category) {
        mCategories.remove(category);
    }
    
    public ArrayList<Goal> getGoals() {
        return mGoals;
    }

    public void setGoals(ArrayList<Goal> goals) {
        mGoals = goals;
        for(Goal g: goals) {
            Log.d(TAG, "--> setGoals: " + g.getTitle());
        }
    }

    /* Remove a single Goal from the user's collection */
    public void removeGoal(Goal goal) {
        mGoals.remove(goal);
    }

    public void setBehaviors(ArrayList<Behavior> behaviors) {
        mBehaviors = behaviors;
        for(Behavior b: behaviors) {
            Log.d(TAG, "--> setBehavior: " + b.getTitle());
        }
    }

    public ArrayList<Behavior> getBehaviors() {
        return mBehaviors;
    }

    /* Remove a single Behavior from a user's collection */
    public void removeBehavior(Behavior behavior) {
        mBehaviors.remove(behavior);
    }

    public void addBehavior(Behavior behavior) {
        mBehaviors.add(behavior);
    }

    public ArrayList<Action> getActions() {
        return mActions;
    }

    /* Remove a single Action from a user's collection */
    public void removeAction(Action action) {
        mActions.remove(action);
    }

    public void setActions(ArrayList<Action> actions) {
        mActions = actions;
        for(Action a: actions) {
            Log.d(TAG, "--> setAction: " + a.getTitle());
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
}
