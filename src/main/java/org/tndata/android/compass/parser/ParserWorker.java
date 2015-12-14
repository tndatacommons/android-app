package org.tndata.android.compass.parser;

import android.os.AsyncTask;


/**
 * Worker that does all the parsing in the background.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ParserWorker extends AsyncTask<Void, Void, Void>{
    private int mRequestCode;
    private String mSrc;
    private ParserCallback mCallback;

    private ParseResults mResult;


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

        mResult = new ParseResults();
    }

    @Override
    protected Void doInBackground(Void... params){
        return null;
    }

    @Override
    protected void onPostExecute(Void result){
        mCallback.onParseSuccess(mRequestCode, mResult);
    }
}
