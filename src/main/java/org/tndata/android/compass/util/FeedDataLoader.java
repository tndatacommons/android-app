package org.tndata.android.compass.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;

import java.util.ArrayList;
import java.util.List;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * This class loads all the necessary initial data for the feed.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class FeedDataLoader implements HttpRequest.RequestCallback, Parser.ParserCallback{
    private static final String TAG = "FeedDataLoader";

    private static FeedDataLoader sLoader;


    public static FeedDataLoader getInstance(){
        if (sLoader == null){
            sLoader = new FeedDataLoader();
        }
        return sLoader;
    }

    public static void load(@NonNull DataLoadCallback callback){
        getInstance().loadData(callback);
    }

    public static void cancel(){
        if (sLoader != null){
            sLoader.cancel2();
        }
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


    public void loadData(@NonNull DataLoadCallback callback){
        //Cancel any previous requests
        cancel2();

        //Do some setup
        mDataLoadCallback = callback;
        mGoalLoadCallback = null;
        mGoalBatch = new ArrayList<>();
        //Empty means data not yet loaded
        mGetNextUserActionUrl = "";
        mGetNextCustomActionUrl = "";
        mInitialActionLoad = true;
        mInitialGoalLoad = true;

        //Trigger the process
        mGetFeedDataRC = HttpRequest.get(this, API.URL.getFeedData());
    }

    public void loadNextUserAction(){
        if (mGetNextUserActionUrl != null){
            mGetNextUserActionRC = HttpRequest.get(this, mGetNextUserActionUrl);
        }
    }

    public void loadNextCustomAction(){
        if (mGetNextCustomActionUrl != null){
            mGetNextCustomActionRC = HttpRequest.get(this, mGetNextCustomActionUrl);
        }
    }

    public boolean canLoadMoreGoals(){
        return mGetNextGoalBatchUrl != null;
    }

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
        sLoader = null;
        mDataLoadCallback.onFeedDataLoaded(null);
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.FeedDataResultSet){
            mFeedData = ((ParserModels.FeedDataResultSet)result).results.get(0);
            mFeedData.init();
        }
    }

    @Override
    public synchronized void onParseSuccess(int requestCode, ParserModels.ResultSet result){
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
            mFeedData.setNextUserAction(set.results.get(0));
            if (mInitialActionLoad && (mGetNextCustomActionUrl == null || mGetNextCustomActionUrl.isEmpty())){
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
            mFeedData.setNextCustomAction(set.results.get(0));
            if (mInitialActionLoad && (mGetNextUserActionUrl == null || mGetNextUserActionUrl.isEmpty())){
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

    }

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

    public void cancel2(){
        HttpRequest.cancel(sLoader.mGetFeedDataRC);
        HttpRequest.cancel(sLoader.mGetNextUserActionRC);
        HttpRequest.cancel(sLoader.mGetNextCustomActionRC);
        HttpRequest.cancel(sLoader.mGetCustomGoalsRC);
        HttpRequest.cancel(sLoader.mGetUserGoalsRC);
    }


    public interface DataLoadCallback{
        void onFeedDataLoaded(@Nullable FeedData feedData);
    }

    public interface GoalLoadCallback{
        void onGoalsLoaded(List<Goal> batch);
    }
}
