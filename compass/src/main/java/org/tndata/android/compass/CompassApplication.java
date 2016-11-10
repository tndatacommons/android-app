package org.tndata.android.compass;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.tndata.android.compass.database.TDCCategoryTableHandler;
import org.tndata.android.compass.util.FeedDataLoader;
import org.tndata.compass.model.Action;
import org.tndata.compass.model.TDCCategory;
import org.tndata.android.compass.model.FeedData;
import org.tndata.compass.model.User;
import org.tndata.android.compass.service.LocationNotificationService;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.GcmRegistration;
import org.tndata.android.compass.util.Tour;

import java.util.ArrayList;
import java.util.Collections;
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
    //The list of available categories
    private Map<Long, TDCCategory> mAvailableCategories;
    //The feed data bundle
    private FeedData mFeedData;


    /**
     * Token getter.
     *
     * @return the user token if one is set, otherwise an empty string.
     */
    public String getToken(){
        if (getUser() != null){
            return mUser.getToken();
        }
        return "";
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
     * User setter.
     *
     * @param user the user who logged in.
     */
    public void setUser(@NonNull User user){
        Log.d(TAG, "Setting user: " + user);
        mUser = user;
        mUser.writeToSharedPreferences(this);

        //Add the authorization header with the user's token to the requests library
        HttpRequest.addHeader("Authorization", "Token " + getToken());


    }

    /**
     * User getter.
     *
     * @return the currently logged in user, null if none.
     */
    public User getUser(){
        if (mUser == null){
            mUser = User.getFromPreferences(this);
        }
        return mUser;
    }

    /**
     * Deletes all user-related data and lets the backend know the user logged out.
     */
    public void logOut(){
        String regId = getGcmRegistrationId();
        if (regId != null && !regId.isEmpty()){
            HttpRequest.post(null, API.URL.logOut(), API.BODY.logOut(regId));
        }
        mUser = null;
        User.deleteFromPreferences(this);
    }

    /**
     * Available category setter. Categories set using this method are written to the database.
     * The data is kept internally as a Long->CategoryContent HashMap.
     *
     * @param categories the list of categories available to the user at fetch time.
     */
    public synchronized void setAvailableCategories(List<TDCCategory> categories){
        //Create a new Map and trash the old one
        mAvailableCategories = new HashMap<>();
        //Populate the new one
        for (TDCCategory category:categories){
            mAvailableCategories.put(category.getId(), category);
        }
        //Write them to the database
        TDCCategoryTableHandler handler = new TDCCategoryTableHandler(this);
        handler.writeCategories(categories);
        handler.close();
    }

    /**
     * Public category map getter.
     *
     * @return A Long->CategoryContent HashMap.
     */
    public synchronized Map<Long, TDCCategory> getAvailableCategories(){
        if (mAvailableCategories == null || mAvailableCategories.isEmpty()){
            TDCCategoryTableHandler handler = new TDCCategoryTableHandler(this);
            mAvailableCategories = handler.readCategories();
            handler.close();
        }
        return mAvailableCategories;
    }

    /**
     * Gets an ordered List of categories. Categories selected by default are excluded.
     *
     * @param filtered if true, only featured categories are included in the result.
     * @return a list of categories.
     */
    public synchronized List<TDCCategory> getCategoryList(boolean filtered){
        List<TDCCategory> result = new ArrayList<>();
        for (TDCCategory category: getAvailableCategories().values()){
            if (!category.isSelectedByDefault()){
                if (!filtered || category.isFeatured()){
                    result.add(category);
                }
            }
        }
        Collections.sort(result);
        return result;
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


    /*--------------------------------------------------------------*
     * These methods action handling methods in the FeedData class. *
     *--------------------------------------------------------------*/

    public void replaceUpNext(){

    }

    /**
     * Adds an action to the global FeedData bundle.
     *
     * @param action the action to be added.
     */
    public void addAction(Action action){
        mFeedData.addAction(action);
    }

    /**
     * Updates an action in the global FeedData bundle.
     *
     * @param action the acton to be added.
     */
    public void updateAction(Action action){
        if (mFeedData.updateAction(action)){
            FeedDataLoader.getInstance().loadNextAction();
        }
    }

    /**
     * Removes an action from the global FeedData bundle.
     *
     * @param action the action to be removed.
     */
    public void removeAction(Action action){
        mFeedData.removeAction(action);
        FeedDataLoader.getInstance().loadNextAction();
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

        Tour.init(this);
        //Tour.reset();
        LocationNotificationService.start(this);
    }
}
