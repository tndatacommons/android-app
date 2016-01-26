package org.tndata.android.compass;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;

import org.tndata.android.compass.model.ActionContent;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.model.UserBehavior;
import org.tndata.android.compass.model.UserCategory;
import org.tndata.android.compass.model.UserData;
import org.tndata.android.compass.model.UserGoal;
import org.tndata.android.compass.service.LocationNotificationService;
import org.tndata.android.compass.util.GcmRegistration;
import org.tndata.android.compass.util.ImageLoader;

import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;


public class CompassApplication extends Application{
    private static final String TAG = "CompassApplication";


    private String mToken;
    private User mUser; // The logged-in user
    private UserData mUserData = new UserData(); // The user's selected content.
    private List<Category> mPublicCategories;


    public void setToken(String token) {
        mToken = token;
    }

    public String getToken(){
        if (mToken != null && !mToken.isEmpty()){
            return mToken;
        }
        mToken = PreferenceManager.getDefaultSharedPreferences(this).getString("auth_token", "");
        return mToken;
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

    public void setPublicCategories(List<Category> publicCategories){
        mPublicCategories = publicCategories;
    }

    public List<Category> getPublicCategories(){
        return mPublicCategories;
    }

    // -------------------------------------------------------------------
    // The following methods are wrappers around UserData methods; All
    // of this info used to be stored directly in the CompassApplication
    // class, so I've left these here for backwards compatibility.
    // -------------------------------------------------------------------
    public Map<Integer, UserCategory> getCategories() {
        return mUserData.getCategories();
    }

    public void addCategory(UserCategory category) {
        mUserData.addCategory(category);
    }

    public List<UserGoal> getCategoryGoals(Category category) {
        return mUserData.getCategoryGoals(category);
    }

    public Map<Integer, UserGoal> getGoals() {
        return mUserData.getGoals();
    }

    public void addGoal(UserGoal goal) {
        mUserData.addGoal(goal);
        mUserData.logSelectedData("AFTER CompassApplication.addGoal", false);
    }

    public void removeGoal(Goal goal) {
        mUserData.removeGoal(goal);
        mUserData.logSelectedData("AFTER CompassApplication.removeGoal", false);
    }

    public Map<Integer, UserBehavior> getBehaviors() {
        return mUserData.getBehaviors();
    }

    public void removeBehavior(Behavior behavior) {
        mUserData.removeBehavior(behavior);
        mUserData.logSelectedData("AFTER CompassApplication.removeBehavior: ", false);
    }

    public void addBehavior(UserBehavior behavior) {
        mUserData.addBehavior(behavior);
        mUserData.logSelectedData("AFTER CompassApplication.addBehavior", false);
    }

    public Map<Integer, UserAction> getActions() {
        return mUserData.getActions();
    }

    public void removeAction(ActionContent action) {
        mUserData.removeAction(action);
        mUserData.logSelectedData("AFTER CompassApplication.removeAction: ", false);
    }

    public void addAction(UserAction action) {
        mUserData.addAction(action);
        mUserData.logSelectedData("AFTER CompassApplication.addAction", false);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        //if(!BuildConfig.DEBUG){
            Fabric.with(this, new Crashlytics());
        //}
        startService(new Intent(this, LocationNotificationService.class));
        ImageLoader.initialize(getApplicationContext());
    }
}
