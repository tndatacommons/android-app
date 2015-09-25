package org.tndata.android.compass.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class RegisterDeviceTask extends AsyncTask<Void, Void, Void>{
    private static final String TAG = "RegisterDeviceTask";

    private Context mContext;
    private RegisterDeviceTaskListener mCallback;
    private String mRegistrationId;
    private String mDeviceId;


    public RegisterDeviceTask(@NonNull Context context, @NonNull RegisterDeviceTaskListener callback,
                              @NonNull String registrationId, @Nullable String deviceId){
        mContext = context;
        mCallback = callback;
        mRegistrationId = registrationId;
        mDeviceId = deviceId;
    }

    @Override
    protected Void doInBackground(Void... params){
        String token = ((CompassApplication)(mContext.getApplicationContext())).getToken();
        String url = Constants.BASE_URL + "notifications/devices/";

        Log.d(TAG, "POSTing to: " + url);
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + token);

        JSONObject body = new JSONObject();
        try{
            body.put("registration_id", mRegistrationId);
            body.put("device_name", Build.MANUFACTURER + " " + Build.PRODUCT);
            if (mDeviceId != null){
                body.put("device_id", mDeviceId);
            }
        }
        catch (JSONException e1){
            e1.printStackTrace();
            return null;
        }

        InputStream stream = NetworkHelper.httpPostStream(url, headers, body.toString());
        if (stream == null){
            Log.d(TAG, "Bad stream");
        }
        else{
            try{
                stream.close();
            }
            catch (IOException iox){
                iox.printStackTrace();
            }
            Log.d(TAG, "Request complete");
        }
        return null;
    }

    protected void onPostExecute(){
        mCallback.deviceRegistered(mRegistrationId);
    }


    public interface RegisterDeviceTaskListener{
        void deviceRegistered(String registration_id);
    }
}
