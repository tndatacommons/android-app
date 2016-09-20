package org.tndata.android.compass.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;

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

    public static void load(@NonNull Callback callback){
        getInstance().loadData(callback);
    }

    public static void cancel(){
        if (sLoader != null){
            sLoader.cancel2();
        }
    }


    private Callback mCallback;
    private FeedData mFeedData;

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


    public void loadData(@NonNull Callback callback){
        //Cancel any previous requests
        cancel2();

        //Do some setup
        mCallback = callback;
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

    public void loadNextGoalBatch(){

    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetFeedDataRC){
            Parser.parse(result, ParserModels.FeedDataResultSet.class, this);
        }
        else if (requestCode == mGetNextUserActionRC){
            Parser.parse(result, ParserModels.UserActionResultSet.class, this);
        }
        else if (requestCode == mGetNextCustomActionRC){
            Parser.parse(result, ParserModels.CustomActionResultSet.class, this);
        }
        else if (requestCode == mGetCustomGoalsRC){
            Parser.parse(result, ParserModels.CustomGoalsResultSet.class, this);
        }
        else if (requestCode == mGetUserGoalsRC){
            Parser.parse(result, ParserModels.UserGoalsResultSet.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        sLoader = null;
        mCallback.onFeedDataLoaded(null);
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.FeedDataResultSet){
            mFeedData = ((ParserModels.FeedDataResultSet)result).results.get(0);
            mFeedData.init();
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
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
            if (mInitialActionLoad && mGetNextCustomActionUrl != null && !mGetNextCustomActionUrl.isEmpty()){
                //If this is the initial load and the custom action has already been loaded,
                //  call replaceUpNext to set the upNext field in FeedData and trigger the
                //  load of the next action
                mFeedData.replaceUpNext();
                mInitialActionLoad = false;
                if (!mInitialGoalLoad){
                    //If the goals have already been loaded, let the callback know
                    mCallback.onFeedDataLoaded(mFeedData);
                }
            }
        }
        else if (result instanceof ParserModels.CustomActionResultSet){
            ParserModels.CustomActionResultSet set = (ParserModels.CustomActionResultSet)result;
            mGetNextCustomActionUrl = set.next;
            mFeedData.setNextCustomAction(set.results.get(0));
            if (mInitialActionLoad && mGetNextUserActionUrl != null && !mGetNextUserActionUrl.isEmpty()){
                mFeedData.replaceUpNext();
                mInitialActionLoad = false;
                if (!mInitialGoalLoad){
                    mCallback.onFeedDataLoaded(mFeedData);
                }
            }
        }
        else if (result instanceof ParserModels.CustomGoalsResultSet){
            ParserModels.CustomGoalsResultSet set = (ParserModels.CustomGoalsResultSet)result;
            mGetNextGoalBatchUrl = set.next;
            if (mGetNextGoalBatchUrl == null){
                mGetNextGoalBatchUrl = API.URL.getUserGoals();
                mFeedData.addGoals(set.results, mGetNextGoalBatchUrl);
                if (set.results.size() < 3){
                    mGetUserGoalsRC = HttpRequest.get(this, mGetNextGoalBatchUrl);
                }
                else{
                    if (mInitialGoalLoad && !mInitialActionLoad){
                        mCallback.onFeedDataLoaded(mFeedData);
                    }
                    if (mInitialGoalLoad){
                        mInitialGoalLoad = false;
                    }
                }
            }
            else{
                mFeedData.addGoals(set.results, mGetNextGoalBatchUrl);
                if (mInitialGoalLoad && !mInitialActionLoad){
                    mCallback.onFeedDataLoaded(mFeedData);
                }
                if (mInitialGoalLoad){
                    mInitialGoalLoad = false;
                }
            }
        }
        else if (result instanceof ParserModels.UserGoalsResultSet){
            ParserModels.UserGoalsResultSet set = (ParserModels.UserGoalsResultSet)result;
            mGetNextGoalBatchUrl = set.next;
            mFeedData.addGoals(set.results, mGetNextGoalBatchUrl);
            mCallback.onFeedDataLoaded(mFeedData);
            if (mInitialGoalLoad && !mInitialActionLoad){
                mCallback.onFeedDataLoaded(mFeedData);
            }
            if (mInitialGoalLoad){
                mInitialGoalLoad = false;
            }
        }
    }

    @Override
    public void onParseFailed(int requestCode){

    }

    public void cancel2(){
        HttpRequest.cancel(sLoader.mGetFeedDataRC);
        HttpRequest.cancel(sLoader.mGetNextUserActionRC);
        HttpRequest.cancel(sLoader.mGetNextCustomActionRC);
        HttpRequest.cancel(sLoader.mGetCustomGoalsRC);
        HttpRequest.cancel(sLoader.mGetUserGoalsRC);
    }


    public interface Callback{
        void onFeedDataLoaded(@Nullable FeedData feedData);
    }
}
