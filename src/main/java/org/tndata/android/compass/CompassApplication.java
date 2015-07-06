package org.tndata.android.compass;

import android.app.Application;

import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.User;

import java.util.ArrayList;

public class CompassApplication extends Application {
    private String mToken;
    private User mUser;
    private ArrayList<Category> mCategories = new ArrayList<Category>();
    private ArrayList<Goal> mGoals = new ArrayList<Goal>();
    private ArrayList<Behavior> mBehaviors = new ArrayList<Behavior>(); // The user's selected behaviors
    private ArrayList<Action> mActions = new ArrayList<Action>();

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
    }

    public void removeCategory(Category category) {
        mCategories.remove(category);
    }
    
    public ArrayList<Goal> getGoals() {
        return mGoals;
    }

    public void setGoals(ArrayList<Goal> goals) {
        mGoals = goals;
    }

    public void removeGoal(Goal goal) {
        mGoals.remove(goal);
    }

    public void setBehaviors(ArrayList<Behavior> behaviors) {
        mBehaviors = behaviors;
    }

    public ArrayList<Behavior> getBehaviors() {
        return mBehaviors;
    }

    public void removeBehavior(Behavior behavior) {
        mBehaviors.remove(behavior);
    }

    public void addBehavior(Behavior behavior) {
        mBehaviors.add(behavior);
    }

    public ArrayList<Action> getActions() {
        return mActions;
    }

    public void removeAction(Action action) {
        mActions.remove(action);
    }

    public void setActions(ArrayList<Action> actions) {
        mActions = actions;
    }

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
