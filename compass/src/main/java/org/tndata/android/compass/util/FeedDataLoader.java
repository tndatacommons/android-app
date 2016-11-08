package org.tndata.android.compass.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.tndata.android.compass.model.FeedData;
import org.tndata.compass.model.Goal;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.compass.model.ResultSet;

import java.util.ArrayList;
import java.util.List;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * This class loads all the necessary initial data for the feed.
 *
 * TODO have the next batch of goals ready for the next time the loadNextGoalBatch is called
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class FeedDataLoader implements HttpRequest.RequestCallback, Parser.ParserCallback{
    private static final String TAG = "FeedDataLoader";

    private static FeedDataLoader sLoader;


    /**
     * Gets an instance of the FeedDataLoader.
     *
     * @return an instance of the FeedDataLoader.
     */
    public static FeedDataLoader getInstance(){
        if (sLoader == null){
            sLoader = new FeedDataLoader();
        }
        return sLoader;
    }


    private DataLoadCallback mDataLoadCallback;
    private GoalLoadCallback mGoalLoadCallback;
    private FeedData mFeedData;
    private List<Goal> mGoalBatch;

    //¡¡IMPORTANT!!
    //If the URLs are null, the data has been loaded but the result set was empty
    //If the URLs are the empty string, the data has not yet been loaded
    //This is because the API returns NULL as the next URL in the last page of the dataset
    private String mGetNextUserActionUrl;
    private String mGetNextCustomActionUrl;
    //Goal load is not concurrent, so the above comment does not apply to this URL
    private String mGetNextGoalBatchUrl;

    //Request codes
    private int mGetFeedDataRC;
    private int mGetNextUserActionRC;
    private int mGetNextCustomActionRC;
    private int mGetCustomGoalsRC;
    private int mGetUserGoalsRC;

    //Initial run flags; when true it means the initial load for a particular section is
    //  still running
    private boolean mInitialActionLoad;
    private boolean mInitialGoalLoad;


    /**
     * Private constructor. Only the getInstance() method is supposed to create instances.
     */
    private FeedDataLoader(){
        init();
    }

    /**
     * Initializes some of the components of this objects
     */
    private void init(){
        mDataLoadCallback = null;
        mGoalLoadCallback = null;
        mGoalBatch = new ArrayList<>();
        //Empty means data not yet loaded
        mGetNextUserActionUrl = "";
        mGetNextCustomActionUrl = "";
        mInitialActionLoad = true;
        mInitialGoalLoad = true;
    }

    /**
     * Resets this object and triggers the feed load sequence.
     *
     * @param callback the object that should get notified when the feed data is loaded.
     */
    public void load(@NonNull DataLoadCallback callback){
        //Cancel any previous requests
        cancel();

        //Do some setup
        init();
        mDataLoadCallback = callback;

        //Trigger the load sequence
        mGetFeedDataRC = HttpRequest.get(this, API.URL.getFeedData());
    }

    /**
     * Loads the next user action.
     */
    public void loadNextUserAction(){
        if (mGetNextUserActionUrl != null){
            mGetNextUserActionRC = HttpRequest.get(this, mGetNextUserActionUrl);
        }
    }

    /**
     * Loads the next custom action.
     */
    public void loadNextCustomAction(){
        if (mGetNextCustomActionUrl != null){
            mGetNextCustomActionRC = HttpRequest.get(this, mGetNextCustomActionUrl);
        }
    }

    /**
     * Checks whether more goals can be loaded.
     *
     * @return true if more goals can be loaded.
     */
    public boolean canLoadMoreGoals(){
        return mGetNextGoalBatchUrl != null;
    }

    /**
     * Loads the next batch of goals.
     *
     * @param callback the object that should get notified when the next batch has loaded.
     */
    public void loadNextGoalBatch(@NonNull GoalLoadCallback callback){
        if (!mInitialGoalLoad && canLoadMoreGoals()){
            mGoalLoadCallback = callback;
            mGoalBatch.clear();
            int rc = HttpRequest.get(this, mGetNextGoalBatchUrl);
            if (mGetNextGoalBatchUrl.contains("custom")){
                mGetCustomGoalsRC = rc;
            }
            else{
                mGetUserGoalsRC = rc;
            }
        }
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetFeedDataRC){
            Log.i(TAG, "FeedData loaded");
            Parser.parse(result, ParserModels.FeedDataResultSet.class, this);
        }
        else if (requestCode == mGetNextUserActionRC){
            Log.i(TAG, "UserAction loaded");
            Parser.parse(result, ParserModels.UserActionResultSet.class, this);
        }
        else if (requestCode == mGetNextCustomActionRC){
            Log.i(TAG, "CustomAction loaded");
            Parser.parse(result, ParserModels.CustomActionResultSet.class, this);
        }
        else if (requestCode == mGetCustomGoalsRC){
            Log.i(TAG, "CustomGoals loaded");
            Parser.parse(result, ParserModels.CustomGoalsResultSet.class, this);
        }
        else if (requestCode == mGetUserGoalsRC){
            Log.i(TAG, "UserGoals loaded");
            Parser.parse(result, ParserModels.UserGoalsResultSet.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        //Log the error and which request failed
        Log.e(TAG, error.toString());
        if (requestCode == mGetFeedDataRC){
            Log.e(TAG, "FeedData couldn't be loaded");
        }
        else if (requestCode == mGetNextUserActionRC){
            Log.e(TAG, "UserAction couldn't be loaded");
        }
        else if (requestCode == mGetNextCustomActionRC){
            Log.e(TAG, "CustomAction couldn't be loaded");
        }
        else if (requestCode == mGetCustomGoalsRC){
            Log.e(TAG, "CustomGoals couldn't be loaded");
            //If goal load callback exists, notify of failure
            if (mGoalLoadCallback != null){
                mGoalLoadCallback.onGoalsLoaded(null);
            }
        }
        else if (requestCode == mGetUserGoalsRC){
            Log.e(TAG, "UserGoals couldn't be loaded");
            //If goal load callback exists, notify of failure
            if (mGoalLoadCallback != null){
                mGoalLoadCallback.onGoalsLoaded(null);
            }
        }

        //If data load callback exists, notify of failure
        if (mDataLoadCallback != null){
            mDataLoadCallback.onFeedDataLoaded(null);
        }
    }

    @Override
    public void onProcessResult(int requestCode, ResultSet result){
        if (result instanceof ParserModels.FeedDataResultSet){
            mFeedData = ((ParserModels.FeedDataResultSet)result).results.get(0);
            mFeedData.init();
        }
    }

    @Override
    public synchronized void onParseSuccess(int requestCode, ResultSet result){
        if (result instanceof ParserModels.FeedDataResultSet){
            //When the feed data is loaded, begin loading the rest of the dataset
            mGetNextUserActionRC = HttpRequest.get(this, API.URL.getTodaysUserActions());
            mGetNextCustomActionRC = HttpRequest.get(this, API.URL.getTodaysCustomActions());
            mGetCustomGoalsRC = HttpRequest.get(this, API.URL.getCustomGoals());
        }
        else if (result instanceof ParserModels.UserActionResultSet){
            ParserModels.UserActionResultSet set = (ParserModels.UserActionResultSet)result;
            //Record the url to fetch the next user action and set this one
            mGetNextUserActionUrl = set.next;
            if (set.results.isEmpty()){
                Log.i(TAG, "No user actions loaded");
                mFeedData.setNextUserAction(null);
            }
            else{
                Log.i(TAG, "User action loaded: " + set.results.get(0));
                mFeedData.setNextUserAction(set.results.get(0));
            }
            if (mInitialActionLoad && (mGetNextCustomActionUrl == null || !mGetNextCustomActionUrl.isEmpty())){
                //If this is the initial load and the custom action has already been loaded,
                //  call replaceUpNext to set the upNext field in FeedData and trigger the
                //  load of the next action
                mFeedData.replaceUpNext();
                mInitialActionLoad = false;
                if (!mInitialGoalLoad){
                    //If the goals have already been loaded, let the callback know
                    mDataLoadCallback.onFeedDataLoaded(mFeedData);
                }
            }
        }
        else if (result instanceof ParserModels.CustomActionResultSet){
            //This block follows the same procedure as the block above
            ParserModels.CustomActionResultSet set = (ParserModels.CustomActionResultSet)result;
            mGetNextCustomActionUrl = set.next;
            if (set.results.isEmpty()){
                mFeedData.setNextCustomAction(null);
            }
            else{
                mFeedData.setNextCustomAction(set.results.get(0));
            }
            if (mInitialActionLoad && (mGetNextUserActionUrl == null || !mGetNextUserActionUrl.isEmpty())){
                mFeedData.replaceUpNext();
                mInitialActionLoad = false;
                if (!mInitialGoalLoad){
                    mDataLoadCallback.onFeedDataLoaded(mFeedData);
                }
            }
        }
        else if (result instanceof ParserModels.CustomGoalsResultSet){
            ParserModels.CustomGoalsResultSet set = (ParserModels.CustomGoalsResultSet)result;
            //Copy the url and fill the next batch list
            mGetNextGoalBatchUrl = set.next;
            mGoalBatch.addAll(set.results);
            if (mGetNextGoalBatchUrl == null){
                //If there are no more custom goals, replace the url for the ont used to fetch
                //  user goals
                mGetNextGoalBatchUrl = API.URL.getUserGoals();
                if (mGoalBatch.size() < 3){
                    //If there are less than 3 custom goals, load some user goals too
                    mGetUserGoalsRC = HttpRequest.get(this, mGetNextGoalBatchUrl);
                }
                else{
                    //Otherwise, dispatch
                    dispatchGoals();
                }
            }
            else{
                dispatchGoals();
            }
        }
        else if (result instanceof ParserModels.UserGoalsResultSet){
            ParserModels.UserGoalsResultSet set = (ParserModels.UserGoalsResultSet)result;
            mGetNextGoalBatchUrl = set.next;
            mGoalBatch.addAll(set.results);
            dispatchGoals();
        }
    }

    @Override
    public void onParseFailed(int requestCode){
        //This shouldn't happen
    }

    /**
     * Decides what to do with recently loaded goals.
     */
    private void dispatchGoals(){
        Log.i(TAG, "Dispatching goals...");
        if (mInitialGoalLoad){
            Log.i(TAG, "Initial load, adding to FeedData...");
            mFeedData.addGoals(mGoalBatch);
            if (!mInitialActionLoad){
                Log.i(TAG, "Action load complete, notifying callback...");
                mDataLoadCallback.onFeedDataLoaded(mFeedData);
            }
            else{
                Log.i(TAG, "Action load in progress, waiting...");
            }
            mInitialGoalLoad = false;
        }

        if (mGoalLoadCallback != null){
            Log.i(TAG, "GoalLoadCallback found, notifying...");
            mGoalLoadCallback.onGoalsLoaded(mGoalBatch);
        }
    }

    /**
     * Cancels all pending network requests.
     */
    public void cancel(){
        HttpRequest.cancel(sLoader.mGetFeedDataRC);
        HttpRequest.cancel(sLoader.mGetNextUserActionRC);
        HttpRequest.cancel(sLoader.mGetNextCustomActionRC);
        HttpRequest.cancel(sLoader.mGetCustomGoalsRC);
        HttpRequest.cancel(sLoader.mGetUserGoalsRC);
    }


    /**
     * Callback interface for FeedData load requests.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface DataLoadCallback{
        /**
         * Called when the FeedData object is loaded or fails to load.
         *
         * @param feedData the FeedData object if load was successful, null otherwise.
         */
        void onFeedDataLoaded(@Nullable FeedData feedData);
    }


    /**
     * Callback interface for Goal load requests.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface GoalLoadCallback{
        /**
         * Called when Goals are loaded or fail to load.
         *
         * @param batch a list of Goals if load was successful, null otherwise.
         */
        void onGoalsLoaded(@Nullable List<Goal> batch);
    }
}
