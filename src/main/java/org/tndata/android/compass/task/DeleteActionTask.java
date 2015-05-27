package org.tndata.android.compass.task;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.util.HashMap;
import java.util.Map;

public class DeleteActionTask extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private static Gson gson = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.IDENTITY).create();
    private DeleteActionTaskListener mCallback;
    private String mActionMappingId;

    public interface DeleteActionTaskListener {
        void actionDeleted();
    }

    public DeleteActionTask(Context context, DeleteActionTaskListener callback,
                            String actionId) {
        mContext = context;
        mCallback = callback;
        mActionMappingId = actionId;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String token = ((CompassApplication) ((Activity) mContext)
                .getApplication()).getToken();

        String url = Constants.BASE_URL + "users/actions/" + mActionMappingId + "/";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + token);
        Log.d("Delete user action", url);

        NetworkHelper.httpDeleteStream(url, headers, null);

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        mCallback.actionDeleted();
    }

}
