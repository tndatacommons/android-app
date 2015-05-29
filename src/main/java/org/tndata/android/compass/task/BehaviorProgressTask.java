package org.tndata.android.compass.task;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.tndata.android.compass.model.Behavior;

import java.util.ArrayList;

public class BehaviorProgressTask extends AsyncTask<Void, Void, ArrayList<Behavior>> {

    private Context mContext;
    private static Gson gson = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.IDENTITY).create();
    private ArrayList<String> mBehaviorIds;
    private BehaviorProgressTaskListener mCallback;

    public interface BehaviorProgressTaskListener {
        public void behaviorProgressSaved();
    }

    public BehaviorProgressTask(Context context, BehaviorProgressTaskListener callback,
                           ArrayList<String> behaviorIds) {
        mContext = context;
        mCallback = callback;
        mBehaviorIds = behaviorIds;
    }

    @Override
    protected ArrayList<Behavior> doInBackground(Void... params) {

        // TODO: POST info to http://app.tndata.org/api/users/behaviors/progress/

        /*
        String token = ((CompassApplication) ((Activity) mContext)
                .getApplication()).getToken();

        String url = Constants.BASE_URL + "users/behaviors/";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + token);
        JSONArray postArray = new JSONArray();
        for (int i = 0; i < mBehaviorIds.size(); i++) {
            JSONObject postId = new JSONObject();
            try {
                postId.put("behavior", mBehaviorIds.get(i));
                postArray.put(postId);
            } catch (JSONException e1) {
                e1.printStackTrace();
                return null;
            }
        }
        InputStream stream = NetworkHelper.httpPostStream(url, headers,
                postArray.toString());
        if (stream == null) {
            return null;
        }
        String result = "";
        String createResponse = "";
        try {

            BufferedReader bReader = new BufferedReader(new InputStreamReader(
                    stream, "UTF-8"));

            String line = null;
            while ((line = bReader.readLine()) != null) {
                result += line;
            }
            bReader.close();

            createResponse = Html.fromHtml(result).toString();

            JSONArray jArray = new JSONArray(createResponse);
            ArrayList<Behavior> behaviors = new ArrayList<Behavior>();
            Log.d("user behavior", jArray.toString(2));
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject userBehavior = jArray.getJSONObject(i);
                Behavior behavior = gson.fromJson(userBehavior.getString("behavior"),
                        Behavior.class);
                behavior.setMappingId(userBehavior.getInt("id"));
                behaviors.add(behavior);
            }

            return behaviors;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        return null;
    }

    protected void onPostExecute() {
        mCallback.behaviorProgressSaved();
    }

}
