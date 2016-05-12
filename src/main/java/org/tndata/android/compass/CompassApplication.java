package org.tndata.android.compass;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;

import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.TDCCategory;
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


/**
 * Application class. Contains some utility methods and global data (user info and feed data).
 *
 * @author Ismael Alonso.
 * @version 2.0.0
 */
public class CompassApplication extends Application{
    private static final String TAG = "CompassApplication";

    //The logged-in user
    private User mUser;
    //The list of public categories
    private Map<Long, TDCCategory> mPublicCategories;
    //The feed data bundle
    private FeedData mFeedData;


    /**
     * Token getter.
     *
     * @return the user token if one is set, otherwise an empty string.
     */
    public String getToken(){
        if (mUser != null && mUser.getToken() != null && !mUser.getToken().isEmpty()){
            return mUser.getToken();
        }
        return PreferenceManager.getDefaultSharedPreferences(this).getString("auth_token", "");
    }

    /**
     * GCM registration id getter.
     *
     * @return the GCM registration id if one is available, an empty string otherwise.
     */
    public String getGcmRegistrationId(){
        return getSharedPreferences(GcmRegistration.class.getSimpleName(), Context.MODE_PRIVATE)
                .getString(GcmRegistration.PROPERTY_REG_ID, "");
    }

    /**
     * User log in info getter.
     *
     * @return a {@code User} object with email and password fields set. If no login information is
     *         available, these fields will be empty strings.
     */
    public User getUserLoginInfo(){
        SharedPreferences loginInfo = PreferenceManager.getDefaultSharedPreferences(this);
        return new User(loginInfo.getString("email", ""), loginInfo.getString("password", ""));
    }

    /**
     * User setter.
     *
     * @param user the user who logged in.
     * @param setPreferences true to overwrite shared preferences.
     */
    public void setUser(User user, boolean setPreferences){
        Log.d(TAG, "Setting user: " + user);
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

    /**
     * User getter.
     *
     * @return the currently logged in user.
     */
    public User getUser(){
        return mUser;
    }

    /**
     * Public category setter. The data is kept internally as a Long->CategoryContent HashMap.
     *
     * @param categories the list of public categories.
     */
    public void setPublicCategories(List<TDCCategory> categories){
        mPublicCategories = new HashMap<>();
        for (TDCCategory category:categories){
            mPublicCategories.put(category.getId(), category);
        }
    }

    /**
     * Public category getter.
     *
     * @return A Long->CategoryContent HashMap.
     */
    public Map<Long, TDCCategory> getPublicCategories(){
        return mPublicCategories;
    }

    /**
     * Public category list getter.
     *
     * @return the unordered list of public categories.
     */
    public List<TDCCategory> getPublicCategoryList(){
        return new ArrayList<>(mPublicCategories.values());
    }

    /**
     * A filtered list of public categories. At the moment, this method
     * excludes those categories that are selected for all users by default.
     *
     * @return the unordered list of public categories, excluding those selected by default.
     */
    public List<TDCCategory> getFilteredCategoryList(){
        List<TDCCategory> featured = new ArrayList<>();
        List<TDCCategory> regular = new ArrayList<>();
        for (TDCCategory category:mPublicCategories.values()){
            if (!category.isSelectedByDefault()){
                if (category.isFeatured()){
                    featured.add(category);
                }
                else{
                    regular.add(category);
                }
            }
        }
        featured.addAll(regular);
        return featured;
    }

    public List<TDCCategory> getFeaturedCategories(){
        List<TDCCategory> featured = new ArrayList<>();
        for (TDCCategory category:mPublicCategories.values()){
            if (category.isFeatured()){
                featured.add(category);
            }
        }
        return featured;
    }

    /**
     * Feed data setter.
     *
     * @param feedData the user's feed data bundle.
     */
    public void setFeedData(FeedData feedData){
        mFeedData = feedData;
    }

    /**
     * Feed data getter.
     *
     * @return the user's feed data bundle.
     */
    public FeedData getFeedData(){
        return mFeedData;
    }


    /*---------------------------------------------------------------------------*
     * These methods wrap add, update, and remove methods in the FeedData class. *
     *---------------------------------------------------------------------------*/

    /**
     * Adds a goal to the global FeedData bundle.
     *
     * @param goal the goal to be added.
     */
    public void addGoal(Goal goal){
        mFeedData.addGoal(goal);
    }

    /**
     * Updates a goal in the global FeedData bundle.
     *
     * @param goal the goal to be updated.
     */
    public void updateGoal(Goal goal){
        mFeedData.updateGoal(goal);
    }

    /**
     * Removes a goal from the global FeedData bundle.
     *
     * @param goal the goal to be removed.
     */
    public void removeGoal(Goal goal){
        mFeedData.removeGoal(goal);
    }

    /**
     * Adds an action to the global FeedData bundle.
     *
     * @param goal the parent goal of the action.
     * @param action the action to be added.
     */
    public void addAction(Goal goal, Action action){
        mFeedData.addAction(goal, action);
    }

    /**
     * Updates an action in the global FeedData bundle.
     *
     * @param goal the parent goal of the action.
     * @param action the action to be added.
     */
    public void updateAction(Goal goal, Action action){
        mFeedData.updateAction(goal, action);
    }

    /**
     * Updates an action in the global FeedData bundle.
     *
     * @param action the acton to be added.
     */
    public void updateAction(Action action){
        mFeedData.updateAction(action);
    }

    /**
     * Removes an action from the global FeedData bundle.
     *
     * @param action the action to be removed.
     */
    public void removeAction(Action action){
        mFeedData.removeAction(action);
    }


    /*----------------------------------*
     * Application's onCreate() method. *
     *----------------------------------*/

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
            Log.d(TAG, "(Init) Setting auth header: " + getToken());
            HttpRequest.addHeader("Authorization", "Token " + getToken());
        }
        else{
            HttpRequest.removeHeader("Authorization");
        }
        //Add a constant url parameter for API versioning
        HttpRequest.addUrlParameter("version", "2");

        LocationNotificationService.start(this);
        ImageLoader.initialize(getApplicationContext());
    }
}
