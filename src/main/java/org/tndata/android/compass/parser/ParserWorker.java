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
final class ParserWorker extends AsyncTask<Void, Void, Void>{
    private int mRequestCode;
    private String mSrc;
    private ParserCallback mCallback;

    private ParserResults mResults;

    /**
     * Constructor.
     *
     * @param requestCode the request code, needed for the callback.
     * @param src the string to parse.
     * @param callback the callback object.
     */
    ParserWorker(int requestCode, String src, ParserCallback callback){
        mRequestCode = requestCode;
        mSrc = src;
        mCallback = callback;

        mResults = new ParserResults();
    }

    @Override
    protected Void doInBackground(Void... params){
        try{
            parse(new JSONObject(mSrc));
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return null;
    }

    /**
     * Method used to parse an object whose structure is unknown.
     *
     * @param src the source object.
     * @throws JSONException
     */
    private void parse(JSONObject src) throws JSONException{
        switch (src.optString("object_type")){
            case "user_data":
            default:
                JSONObject result = src.getJSONArray("results").getJSONObject(0);
                mResults.mUserData = ParserMethods.parseUserData(result);
                break;
        }
    }

    @Override
    protected void onPostExecute(Void unused){
        mCallback.onParseSuccess(mRequestCode, mResults);
    }
}
