package org.tndata.android.compass.util;

import android.content.Context;
import android.util.Log;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.database.TDCCategoryTableHandler;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.model.User;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;

import java.util.List;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * Synchronizes cached data in the background.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public final class DataSynchronizer implements HttpRequest.RequestCallback, Parser.ParserCallback{
    private static final String TAG = "DataSynchronizer";


    /**
     * Starts the synchronization process.
     *
     * @param context a reference to the context.
     */
    public static void sync(Context context){
        new DataSynchronizer(context);
    }


    private CompassApplication mApplication;

    private int mGetUserRC;
    private int mGetCategoriesRC;


    /**
     * Constructor. Internally, casts the application reference and starts the http requests.
     *
     * @param context a reference to the context.
     */
    private DataSynchronizer(Context context){
        mApplication = (CompassApplication)context.getApplicationContext();

        mGetUserRC = -1; //HttpRequest.get(this, API.get)
        mGetCategoriesRC = HttpRequest.get(this, API.getCategoriesUrl());
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetUserRC){
            Parser.parse(result, User.class, this);
        }
        else if (requestCode == mGetCategoriesRC){
            Parser.parse(result, ParserModels.CategoryContentResultSet.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        if (requestCode == mGetUserRC){
            Log.e(TAG, "GET User failed");
        }
        else if (requestCode == mGetCategoriesRC){
            Log.e(TAG, "GET Categories failed");
        }
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof User){
            mApplication.setUser((User)result);
            Log.i(TAG, "User synchronized");
        }
        else if (result instanceof ParserModels.CategoryContentResultSet){
            List<TDCCategory> categories = ((ParserModels.CategoryContentResultSet)result).results;
            mApplication.setPublicCategories(categories);

            TDCCategoryTableHandler handler = new TDCCategoryTableHandler(mApplication);
            handler.writeCategories(categories);
            handler.close();
            Log.i(TAG, "Categories synchronized");
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        //Nothing to do in the foreground
    }
}
