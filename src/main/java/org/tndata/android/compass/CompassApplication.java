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
import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.service.LocationNotificationService;
import org.tndata.android.compass.util.GcmRegistration;
import org.tndata.android.compass.util.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.sandwatch.httprequests.HttpRequest;
import io.fabric.sdk.android.Fabric;


//TODO Fix this mess.
public class CompassApplication extends Application{
    private static final String TAG = "CompassApplication";


    private User mUser; // The logged-in user
    private Map<Long, CategoryContent> mPublicCategories;



    private FeedData mFeedDataX;

    public void setFeedDataX(FeedData feedData){
        mFeedDataX = feedData;
    }

    public FeedData getFeedDataX(){
        return mFeedDataX;
    }



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

    public void setPublicCategories(List<CategoryContent> categories){
        mPublicCategories = new HashMap<>();
        for (CategoryContent category:categories){
            mPublicCategories.put(category.getId(), category);
        }
    }

    public Map<Long, CategoryContent> getPublicCategories(){
        return mPublicCategories;
    }

    public List<CategoryContent> getPublicCategoryList(){
        return new ArrayList<>(mPublicCategories.values());
    }


    /*---------------------------------------------------------------------------*
     * These methods wrap add, update, and remove methods in the FeedData class. *
     *---------------------------------------------------------------------------*/

    public void addGoal(Goal goal){
        mFeedDataX.addGoal(goal);
    }

    public void updateGoal(Goal goal){
        mFeedDataX.updateGoal(goal);
    }

    public void removeGoal(Goal goal){
        mFeedDataX.removeGoal(goal);
    }

    public void addAction(Goal goal, Action action){
        mFeedDataX.addAction(goal, action);
    }

    public void updateAction(Goal goal, Action action){
        mFeedDataX.updateAction(goal, action);
    }

    public void updateAction(Action action){
        mFeedDataX.updateAction(action);
    }

    public void removeAction(Action action){
        mFeedDataX.removeAction(action);
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
            Log.d("Init", "Setting auth header: " + getToken());
            HttpRequest.addHeader("Authorization", "Token " + getToken());
        }
        else{
            HttpRequest.removeHeader("Authorization");
        }
        //Add a constant url parameter for API versioning
        HttpRequest.addUrlParameter("version", "2");

        startService(new Intent(this, LocationNotificationService.class));
        ImageLoader.initialize(getApplicationContext());
    }
}
