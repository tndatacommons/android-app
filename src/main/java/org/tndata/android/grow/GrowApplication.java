package org.tndata.android.grow;

import java.util.ArrayList;

import org.tndata.android.grow.model.Action;
import org.tndata.android.grow.model.Category;
import org.tndata.android.grow.model.Goal;
import org.tndata.android.grow.model.User;

import android.app.Application;

public class GrowApplication extends Application {
    private String mToken;
    private User mUser;
    private ArrayList<Category> mCategories = null;
    private ArrayList<Goal> mGoals = null;
    private ArrayList<Action> mActions = new ArrayList<Action>();

    public GrowApplication() {
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

    public ArrayList<Goal> getGoals() {
        return mGoals;
    }

    public void setGoals(ArrayList<Goal> goals) {
        mGoals = goals;
    }

    public ArrayList<Action> getActions() {
        return mActions;
    }

    public void setActions(ArrayList<Action> actions) {
        mActions = actions;
    }
}
