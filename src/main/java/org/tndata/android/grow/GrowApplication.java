package org.tndata.android.grow;

import java.util.ArrayList;

import org.tndata.android.grow.model.Category;
import org.tndata.android.grow.model.Goal;
import org.tndata.android.grow.model.User;

import android.app.Application;
import android.util.Log;

public class GrowApplication extends Application {
    private String mToken;
    private User mUser;
    private ArrayList<Category> mCategories = null;
    private ArrayList<Goal> mGoals = null;

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
        for (Goal goal : mGoals) {
            Log.e("GOAL", "APP:" + goal.getTitle());
        }
    }
}
