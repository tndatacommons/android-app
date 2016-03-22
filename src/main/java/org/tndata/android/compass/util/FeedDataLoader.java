package org.tndata.android.compass.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.tndata.android.compass.model.FeedData;
import org.tndata.android.compass.model.UpcomingAction;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;

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
    private static FeedDataLoader sLoader;


    public static void load(@NonNull Callback callback){
        sLoader = new FeedDataLoader(callback);
    }

    public static void cancel(){
        HttpRequest.cancel(sLoader.mGetFeedDataRC);
        HttpRequest.cancel(sLoader.mGetUpcomingRC);
        HttpRequest.cancel(sLoader.mGetCustomGoalsRC);
        HttpRequest.cancel(sLoader.mGetUserGoalsRC);
    }


    private Callback mCallback;
    private FeedData mFeedData;

    private int mGetFeedDataRC;
    private int mGetUpcomingRC;
    private int mGetCustomGoalsRC;
    private int mGetUserGoalsRC;


    private FeedDataLoader(@NonNull Callback callback){
        mCallback = callback;
        mGetFeedDataRC = HttpRequest.get(this, API.getFeedDataUrl());
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetFeedDataRC){
            Parser.parse(result, ParserModels.FeedDataResultSet.class, this);
        }
        else if (requestCode == mGetUpcomingRC){
            Parser.parse(result, ParserModels.UpcomingActionsResultSet.class, this);
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
        mCallback.onFeedDataLoaded(null);
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.FeedDataResultSet){
            ((ParserModels.FeedDataResultSet)result).results.get(0).init();
        }
        else if (result instanceof ParserModels.UpcomingActionsResultSet){
            List<UpcomingAction> upcoming = ((ParserModels.UpcomingActionsResultSet)result).results;
            mFeedData.setUpcomingActionsX(upcoming);
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.FeedDataResultSet){
            mFeedData = ((ParserModels.FeedDataResultSet)result).results.get(0);
            mGetUpcomingRC = HttpRequest.get(this, API.getUpcomingUrl());
        }
        else if (result instanceof ParserModels.UpcomingActionsResultSet){
            mGetCustomGoalsRC = HttpRequest.get(this, API.getCustomGoalsUrl());
        }
        else if (result instanceof ParserModels.CustomGoalsResultSet){
            ParserModels.CustomGoalsResultSet set = (ParserModels.CustomGoalsResultSet)result;
            String url = set.next;
            if (url == null){
                url = API.getUserGoalsUrl();
                if (set.results.isEmpty()){
                    mGetUserGoalsRC = HttpRequest.get(this, url);
                }
                else{
                    mFeedData.addGoalsX(set.results, url);
                    mCallback.onFeedDataLoaded(mFeedData);
                }
            }
            else{
                if (API.STAGING && url.startsWith("https")){
                    url = url.replaceFirst("s", "");
                }
                mFeedData.addGoalsX(set.results, url);
                mCallback.onFeedDataLoaded(mFeedData);
            }
        }
        else if (result instanceof ParserModels.UserGoalsResultSet){
            ParserModels.UserGoalsResultSet set = (ParserModels.UserGoalsResultSet)result;
            String url = set.next;
            if (url != null){
                url = url.replaceFirst("s", "");
            }
            mFeedData.addGoalsX(set.results, url);
            mCallback.onFeedDataLoaded(mFeedData);
        }
    }


    public interface Callback{
        void onFeedDataLoaded(@Nullable FeedData feedData);
    }
}
