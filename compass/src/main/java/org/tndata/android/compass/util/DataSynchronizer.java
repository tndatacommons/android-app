package org.tndata.android.compass.util;

import android.content.Context;
import android.util.Log;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.database.PlaceTableHandler;
import org.tndata.compass.model.ResultSet;
import org.tndata.compass.model.TDCCategory;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.service.LocationNotificationService;

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
    private int mGetPlacesRC;
    private int mGetCategoriesRC;


    /**
     * Constructor. Internally, casts the application reference and starts the http requests.
     *
     * @param context a reference to the context.
     */
    private DataSynchronizer(Context context){
        mApplication = (CompassApplication)context.getApplicationContext();

        mGetUserRC = HttpRequest.get(this, API.URL.getUserAccount());
        mGetPlacesRC = HttpRequest.get(this, API.URL.getUserPlaces());
        mGetCategoriesRC = HttpRequest.get(this, API.URL.getCategories());
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetUserRC){
            Parser.parse(result, ParserModels.UserResultSet.class, this);
        }
        else if (requestCode == mGetPlacesRC){
            Parser.parse(result, ParserModels.UserPlacesResultSet.class, this);
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
        else if (requestCode == mGetPlacesRC){
            Log.e(TAG, "GET Places failed");
        }
        else if (requestCode == mGetCategoriesRC){
            Log.e(TAG, "GET Categories failed");
        }
    }

    @Override
    public void onProcessResult(int requestCode, ResultSet result){
        if (result instanceof ParserModels.UserResultSet){
            mApplication.setUser(((ParserModels.UserResultSet)result).results.get(0));
            Log.i(TAG, "User synchronized");
        }
        else if (result instanceof ParserModels.UserPlacesResultSet){
            PlaceTableHandler handler = new PlaceTableHandler(mApplication);
            handler.emptyPlacesTable();
            handler.savePlaces(((ParserModels.UserPlacesResultSet)result).results);
            handler.close();
            Log.i(TAG, "Places synchronized");
        }
        else if (result instanceof ParserModels.CategoryContentResultSet){
            List<TDCCategory> categories = ((ParserModels.CategoryContentResultSet)result).results;
            String log = "";
            for (TDCCategory category:categories){
                log += category.getId() + ", ";
            }
            CompassUtil.log(mApplication, "Categories", log.substring(0, log.length()-2));
            mApplication.setAvailableCategories(categories);
            Log.i(TAG, "Categories synchronized");
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ResultSet result){
        if (requestCode == mGetPlacesRC){
            //Places might've changed, notify the location notification service to update the set
            LocationNotificationService.updateDataSet(mApplication.getApplicationContext());
        }
    }

    @Override
    public void onParseFailed(int requestCode){

    }
}
