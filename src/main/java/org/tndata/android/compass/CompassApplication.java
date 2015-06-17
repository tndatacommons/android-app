package org.tndata.android.compass;

import android.app.Application;

import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.User;

import java.util.ArrayList;

public class CompassApplication extends Application {
    private String mToken;
    private User mUser;
    private ArrayList<Category> mCategories = null;
    private ArrayList<Goal> mGoals = null;
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

    public ArrayList<Action> getActions() {
        return mActions;
    }

    public void removeAction(Action action) {
        mActions.remove(action);
    }

    public void setActions(ArrayList<Action> actions) {
        mActions = actions;
    }
}
