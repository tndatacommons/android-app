package org.tndata.android.compass.parser;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Worker that does all the parsing in the background.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
final class ParserWorker<T> extends AsyncTask<Void, Void, T>{
    private int mRequestCode;
    private String mSrc;
    private Class<T> mType;
    private ParserCallback<T> mCallback;

    /**
     * Constructor.
     *
     * @param requestCode the request code, needed for the callback.
     * @param src the string to parse.
     * @param type the type of the target object.
     * @param callback the callback object.
     */
    ParserWorker(int requestCode, String src, Class<T> type, ParserCallback<T> callback){
        mRequestCode = requestCode;
        mSrc = src;
        mType = type;
        mCallback = callback;
    }

    @Override
    protected T doInBackground(Void... params){
        return parse(mSrc);
    }

    /**
     * Method used to parse an object whose structure is unknown.
     *
     * @param src the source object.
     */
    private T parse(String src){
        //TODO this ain't completely generic
        try{
            JSONObject object = new JSONObject(src);
            if (object.has("results")){
                src = object.getJSONArray("results").getString(0);
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }

        T result = ParserMethods.sGson.fromJson(src, mType);
        mCallback.onBackgroundProcessing(mRequestCode, result);
        return result;
    }

    @Override
    protected void onPostExecute(T result){
        mCallback.onParseSuccess(mRequestCode, result);
    }
}
