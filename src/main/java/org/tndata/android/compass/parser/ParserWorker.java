package org.tndata.android.compass.parser;

import android.os.AsyncTask;

import org.tndata.android.compass.parser.ParserModels.ResultSet;
import org.tndata.android.compass.util.CompassUtil;


/**
 * Worker that does all the parsing in the background.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
final class ParserWorker<T extends ResultSet> extends AsyncTask<Void, Void, ResultSet>{
    private int mRequestCode;
    private String mSrc;
    private Class<T> mType;
    private ParserCallback mCallback;

    /**
     * Constructor.
     *
     * @param requestCode the request code, needed for the callback.
     * @param src the string to parse.
     * @param type the type of the target object.
     * @param callback the callback object.
     */
    ParserWorker(int requestCode, String src, Class<T> type, ParserCallback callback){
        mRequestCode = requestCode;
        mSrc = src;
        mType = type;
        mCallback = callback;
    }

    @Override
    protected ResultSet doInBackground(Void... params){
        return parse(mSrc);
    }

    /**
     * Method used to parse an object whose structure is unknown.
     *
     * @param src the source object.
     */
    private ResultSet parse(String src){
        ResultSet result;
        if (mType == null){
            result = (ResultSet)ParserMethods.sGson.fromJson(src, CompassUtil.getTypeOf(src));
        }
        else{
            result = ParserMethods.sGson.fromJson(src, mType);
        }
        mCallback.onProcessResult(mRequestCode, result);
        return result;
    }

    @Override
    protected void onPostExecute(ResultSet result){
        mCallback.onParseSuccess(mRequestCode, result);
    }
}
