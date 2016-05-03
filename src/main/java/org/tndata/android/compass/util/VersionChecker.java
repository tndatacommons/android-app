package org.tndata.android.compass.util;

import android.os.AsyncTask;

import org.jsoup.Jsoup;

import java.io.IOException;


public class VersionChecker extends AsyncTask<Void, Void, String>{
    private static final String URL = "https://play.google.com/store/apps/details?id=org.tndata.android.compass&hl=en";

    private VersionCallback mCallback;


    public VersionChecker(VersionCallback callback){
        mCallback = callback;
    }

    @Override
    protected String doInBackground(Void... params){
        try{
            return Jsoup.connect(URL)
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select("div[itemprop=softwareVersion]")
                    .first()
                    .ownText();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return "";
    }

    @Override
    protected void onPostExecute(String result){
        mCallback.onVersionRetrieved(result);
    }


    public interface VersionCallback{
        void onVersionRetrieved(String versionName);
    }
}
