package org.tndata.android.compass.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

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
    private static FeedDataLoader sLoader;


    public static void load(@NonNull Callback callback){
        sLoader = new FeedDataLoader(callback);
    }

    public static void cancel(){
        if (sLoader != null){
            HttpRequest.cancel(sLoader.mGetFeedDataRC);
            HttpRequest.cancel(sLoader.mGetCustomGoalsRC);
            HttpRequest.cancel(sLoader.mGetUserGoalsRC);
        }
    }


    private Callback mCallback;
    private FeedData mFeedData;

    private int mGetFeedDataRC;
    private int mGetCustomGoalsRC;
    private int mGetUserGoalsRC;


    private FeedDataLoader(@NonNull Callback callback){
        mCallback = callback;
        mGetFeedDataRC = HttpRequest.get(this, API.URL.getFeedData());
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetFeedDataRC){
            Log.d("FeedDataLoader", result);
            Parser.parse(result, ParserModels.FeedDataResultSet.class, this);
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
            ((ParserModels.FeedDataResultSet)result).results.get(0).init();
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.FeedDataResultSet){
            mFeedData = ((ParserModels.FeedDataResultSet)result).results.get(0);
            mGetCustomGoalsRC = HttpRequest.get(this, API.URL.getCustomGoals());
        }
        else if (result instanceof ParserModels.CustomGoalsResultSet){
            ParserModels.CustomGoalsResultSet set = (ParserModels.CustomGoalsResultSet)result;
            String url = set.next;
            if (url == null){
                url = API.URL.getUserGoals();
                mFeedData.addGoals(set.results, url);
                if (set.results.size() < 3){
                    mGetUserGoalsRC = HttpRequest.get(this, url);
                }
                else{
                    sLoader = null;
                    mCallback.onFeedDataLoaded(mFeedData);
                }
            }
            else{
                if (API.STAGING && url.startsWith("https")){
                    url = url.replaceFirst("s", "");
                }
                mFeedData.addGoals(set.results, url);
                sLoader = null;
                mCallback.onFeedDataLoaded(mFeedData);
            }
        }
        else if (result instanceof ParserModels.UserGoalsResultSet){
            ParserModels.UserGoalsResultSet set = (ParserModels.UserGoalsResultSet)result;
            String url = set.next;
            if (url != null){
                if (API.STAGING && url.startsWith("https")){
                    url = url.replaceFirst("s", "");
                }
            }
            mFeedData.addGoals(set.results, url);
            sLoader = null;
            mCallback.onFeedDataLoaded(mFeedData);
        }
    }


    public interface Callback{
        void onFeedDataLoaded(@Nullable FeedData feedData);
    }
}
