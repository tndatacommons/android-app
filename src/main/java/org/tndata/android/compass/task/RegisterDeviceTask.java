package org.tndata.android.compass.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class RegisterDeviceTask extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private RegisterDeviceTaskListener mCallBack;
    private String mRegistrationId;
    private static final String TAG = "RegisterDeviceTask";

    public interface RegisterDeviceTaskListener {
        public void deviceRegistered(String registration_id);
    }

    public RegisterDeviceTask(Context context, RegisterDeviceTaskListener callback, String registration_id) {
        mContext = context;
        mCallBack = callback;
        mRegistrationId = registration_id;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String token = ((CompassApplication) (mContext)).getToken();
        String url = Constants.BASE_URL + "notifications/devices/";

        Log.d(TAG, "POSTing to: " + url);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + token);

        JSONObject body = new JSONObject();
        try {
            body.put("registration_id", mRegistrationId);
            body.put("device_name", Build.MANUFACTURER+ " " + Build.PRODUCT);
        } catch (JSONException e1) {
            e1.printStackTrace();
            return null;
        }

        InputStream stream = NetworkHelper.httpPostStream(url, headers,
                body.toString());
        if (stream == null) {
            Log.d(TAG, "----> null response");
            return null;
        }
        String result = "";
        String createResponse;
        try {

            BufferedReader bReader = new BufferedReader(new InputStreamReader(
                    stream, "UTF-8"));

            String line;
            while ((line = bReader.readLine()) != null) {
                result += line;
            }
            bReader.close();

            Log.d(TAG, "result: " + result);

            createResponse = Html.fromHtml(result).toString();
            JSONObject device = new JSONObject(createResponse);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute() {
        mCallBack.deviceRegistered(mRegistrationId);
    }

}
