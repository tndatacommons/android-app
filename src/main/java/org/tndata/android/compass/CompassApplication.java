package org.tndata.android.compass;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;

import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.service.LocationNotificationService;
import org.tndata.android.compass.util.GcmRegistration;
import org.tndata.android.compass.util.ImageLoader;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;


public class CompassApplication extends Application{
    private String TAG = "CompassApplication";
    private String mToken;
    private User mUser; // The logged-in user
    private UserData mUserData = new UserData(); // The user's selected content.


    public CompassApplication() {
        super();
    }

    public void setToken(String token) {
        mToken = token;
    }

    public String getToken(){
        if (mToken != null && !mToken.isEmpty()){
            return mToken;
        }
        return PreferenceManager.getDefaultSharedPreferences(this).getString("auth_token", "");
    }

    public String getGcmRegistrationId(){
        return getSharedPreferences(GcmRegistration.class.getSimpleName(), Context.MODE_PRIVATE)
                .getString(GcmRegistration.PROPERTY_REG_ID, "");
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public UserData getUserData() {
        return mUserData;
    }

    public void setUserData(UserData userData) {
        mUserData = userData;
    }

    // -------------------------------------------------------------------
    // The following methods are wrappers around UserData methods; All
    // of this info used to be stored directly in the CompassApplication
    // class, so I've left these here for backwards compatibility.
    // -------------------------------------------------------------------
    public ArrayList<Category> getCategories() {
        return mUserData.getCategories();
    }

    public void setCategories(ArrayList<Category> categories) {
        mUserData.setCategories(categories);
    }

    public void addCategory(Category category) {
        mUserData.addCategory(category);
    }

    public ArrayList<Goal> getCategoryGoals(Category category) {
        return mUserData.getCategoryGoals(category);
    }

    public ArrayList<Goal> getGoals() {
        return mUserData.getGoals();
    }

    public void setGoals(ArrayList<Goal> goals) {
        mUserData.setGoals(goals);
    }

    public void addGoal(Goal goal) {
        mUserData.addGoal(goal);
        mUserData.logSelectedData("AFTER CompassApplication.addGoal", false);
    }

    public void removeGoal(Goal goal) {
        mUserData.removeGoal(goal);
        mUserData.logSelectedData("AFTER CompassApplication.removeGoal", false);
    }

    public void setBehaviors(ArrayList<Behavior> behaviors) {
        mUserData.setBehaviors(behaviors);
        mUserData.logSelectedData("AFTER CompassApplication.setBehaviors", false);
    }

    public ArrayList<Behavior> getBehaviors() {
        return mUserData.getBehaviors();
    }

    public void removeBehavior(Behavior behavior) {
        mUserData.removeBehavior(behavior);
        mUserData.logSelectedData("AFTER CompassApplication.removeBehavior: ", false);
    }

    public void addBehavior(Behavior behavior) {
        mUserData.addBehavior(behavior);
        mUserData.logSelectedData("AFTER CompassApplication.addBehavior", false);
    }

    public ArrayList<Action> getActions() {
        return mUserData.getActions();
    }

    public void removeAction(Action action) {
        mUserData.removeAction(action);
        mUserData.logSelectedData("AFTER CompassApplication.removeAction: ", false);
    }

    public void setActions(ArrayList<Action> actions) {
        mUserData.setActions(actions);
        mUserData.logSelectedData("AFTER CompassApplication.setActions", false);
    }

    public void addAction(Action action) {
        mUserData.addAction(action);
        mUserData.logSelectedData("AFTER CompassApplication.addAction", false);
    }

    public void updateAction(Action action) {
        mUserData.updateAction(action);
        mUserData.logSelectedData("AFTER CompassApplication.updateAction", false);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        if(!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        startService(new Intent(this, LocationNotificationService.class));
        ImageLoader.initialize(getApplicationContext());
    }
}
