package org.tndata.android.compass;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.CategoryContent;
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

import es.sandwatch.httprequests.HttpRequest;
import io.fabric.sdk.android.Fabric;


//TODO Fix this mess.
public class CompassApplication extends Application{
    private static final String TAG = "CompassApplication";


    private User mUser; // The logged-in user
    private UserData mUserData = new UserData(); // The user's selected content.
    private List<CategoryContent> mPublicCategories;


    public String getToken(){
        if (mUser != null && mUser.getToken() != null && !mUser.getToken().isEmpty()){
            return mUser.getToken();
        }
        return PreferenceManager.getDefaultSharedPreferences(this).getString("auth_token", "");
    }

    public String getGcmRegistrationId(){
        return getSharedPreferences(GcmRegistration.class.getSimpleName(), Context.MODE_PRIVATE)
                .getString(GcmRegistration.PROPERTY_REG_ID, "");
    }

    public User getUserLoginInfo(){
        SharedPreferences loginInfo = PreferenceManager.getDefaultSharedPreferences(this);
        return new User(loginInfo.getString("email", ""), loginInfo.getString("password", ""));
    }

    public User getUser(){
        return mUser;
    }

    public void setUser(User user, boolean setPreferences){
        Log.d(TAG, "Setting user: " + user);
        Log.d(TAG, "Set preferences: " + setPreferences);
        mUser = user;

        //Add the authorization header with the user's token to the requests library
        HttpRequest.addHeader("Authorization", "Token " + getToken());

        if (setPreferences){
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("auth_token", mUser.getToken());
            editor.putString("first_name", mUser.getFirstName());
            editor.putString("last_name", mUser.getLastName());
            editor.putString("email", mUser.getEmail());
            editor.putString("password", mUser.getPassword());
            editor.putLong("id", mUser.getId());
            editor.apply();
        }
    }

    public UserData getUserData() {
        return mUserData;
    }

    public void setUserData(UserData userData) {
        mUserData = userData;
    }

    public void setPublicCategories(List<CategoryContent> publicCategories){
        mPublicCategories = publicCategories;
    }

    public List<CategoryContent> getPublicCategories(){
        return mPublicCategories;
    }

    // -------------------------------------------------------------------
    // The following methods are wrappers around UserData methods; All
    // of this info used to be stored directly in the CompassApplication
    // class, so I've left these here for backwards compatibility.
    // -------------------------------------------------------------------
    public Map<Long, UserCategory> getCategories() {
        return mUserData.getCategories();
    }

    public void addCategory(UserCategory category) {
        mUserData.addCategory(category);
    }

    public List<UserGoal> getCategoryGoals(CategoryContent category) {
        return mUserData.getCategoryGoals(category);
    }

    public Map<Long, UserGoal> getGoals() {
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

    public Map<Long, UserBehavior> getBehaviors() {
        return mUserData.getBehaviors();
    }

    public void removeBehavior(UserBehavior behavior) {
        mUserData.removeBehavior(behavior);
        mUserData.logSelectedData("AFTER CompassApplication.removeBehavior: ", false);
    }

    public void addBehavior(UserBehavior behavior) {
        mUserData.addBehavior(behavior);
        mUserData.logSelectedData("AFTER CompassApplication.addBehavior", false);
    }

    public Map<Long, UserAction> getActions() {
        return mUserData.getActions();
    }

    public void removeAction(Action action) {
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

        //Init the HttpRequest library
        HttpRequest.init(getApplicationContext());
        //Add or remove the authorization header with the user's token
        String token = getToken();
        if (token != null && !token.isEmpty()){
            HttpRequest.addHeader("Authorization", "Token " + getToken());
        }
        else{
            HttpRequest.removeHeader("Authorization");
        }
        //Add a constant url parameter vir API versioning
        HttpRequest.addUrlParameter("version", "2");

        startService(new Intent(this, LocationNotificationService.class));
        ImageLoader.initialize(getApplicationContext());
    }
}
