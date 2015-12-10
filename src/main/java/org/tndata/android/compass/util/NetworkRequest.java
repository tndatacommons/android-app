package org.tndata.android.compass.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Utility helper class to make network requests to our REST API.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public final class NetworkRequest{
    private static final int MAX_REQUEST_CODE = 999999;

    private static final int DEFAULT_REQUEST_TIMEOUT = 10*1000;
    private static final int DEFAULT_MAX_RETRIES = 4;
    private static final float DEFAULT_RETRY_BACKOFF = 1.5f;


    //requestCode -> NetworkRequest
    private static Map<Integer, NetworkRequest> sRequestMap;
    private static RequestQueue sRequestQueue;

    //Need to keep track of the last request code delivered to avoid collisions
    private static int sLastRequestCode = 0;


    /**
     * Initialises the static fields of the class if necessary.
     *
     * @param context a reference to the context.
     */
    private static void init(@NonNull Context context){
        if (sRequestMap == null){
            sRequestMap = new HashMap<>();
        }
        if (sRequestQueue == null){
            sRequestQueue = Volley.newRequestQueue(context);
        }
    }

    /**
     * Generates a unique request code.
     *
     * @return the request code.
     */
    private static int generateRequestCode(){
        if (sLastRequestCode >= MAX_REQUEST_CODE){
            sLastRequestCode = 0;
        }
        return ++sLastRequestCode;
    }

    public static int get(@NonNull Context context, @NonNull RequestCallback callback,
                          @NonNull String url, @NonNull String token){

        return requestWithoutBody(Request.Method.GET, context, callback, url, token,
                DEFAULT_REQUEST_TIMEOUT);
    }

    public static int get(@NonNull Context context, @NonNull RequestCallback callback,
                          @NonNull String url, @NonNull String token, int timeout){

        return requestWithoutBody(Request.Method.GET, context, callback, url, token, timeout);
    }

    public static int post(@NonNull Context context, @Nullable RequestCallback callback,
                           @NonNull String url, @NonNull String token, @NonNull JSONObject body){

        return requestWithBody(Request.Method.POST, context, callback, url, token, body,
                DEFAULT_REQUEST_TIMEOUT);
    }

    public static int post(@NonNull Context context, @Nullable RequestCallback callback,
                           @NonNull String url, @NonNull String token,
                           @NonNull JSONObject body, int timeout){

        return requestWithBody(Request.Method.POST, context, callback, url, token, body, timeout);
    }

    public static int put(@NonNull Context context, @Nullable RequestCallback callback,
                          @NonNull String url, @NonNull String token, @NonNull JSONObject body){

        return requestWithBody(Request.Method.PUT, context, callback, url, token, body,
                DEFAULT_REQUEST_TIMEOUT);
    }

    public static int put(@NonNull Context context, @Nullable RequestCallback callback,
                          @NonNull String url, @NonNull String token,
                          @NonNull JSONObject body, int timeout){

        return requestWithBody(Request.Method.PUT, context, callback, url, token, body, timeout);
    }

    public static int delete(@NonNull Context context, @Nullable RequestCallback callback,
                             @NonNull String url, @NonNull String token, @NonNull JSONObject body){

        return requestWithBody(Request.Method.DELETE, context, callback, url, token, body,
                DEFAULT_REQUEST_TIMEOUT);
    }

    public static int delete(@NonNull Context context, @Nullable RequestCallback callback,
                             @NonNull String url, @NonNull String token,
                             @NonNull JSONObject body, int timeout){

        return requestWithBody(Request.Method.DELETE, context, callback, url, token, body, timeout);
    }

    /**
     * Cancels a request if the request is still active.
     *
     * @param requestCode the request code of the request to cancel.
     * @return true if the request was cancelled successfully, false otherwise.
     */
    public static boolean cancel(int requestCode){
        if (sRequestMap != null){
            NetworkRequest request = sRequestMap.remove(requestCode);
            if (request != null){
                request.mRequest.cancel();
                return true;
            }
        }
        return false;
    }


    /*---------------------------------------------------------------------------------*
     * THE FOLLOWING TWO METHODS, requestWithoutBody() and requestWithBody(), ARE THE  *
     * CORE OF THIS CLASS. EVERY REQUEST TYPE METHOD SHOULD WRAP ONE OF THEM TO CREATE *
     * THE DESIRED REQUEST.                                                            *
     *---------------------------------------------------------------------------------*/

    /**
     * Creates a request without a body.
     *
     * @param method the HTTP method of this request.
     * @param context a reference to the context.
     * @param callback the callback object.
     * @param url the url to make the request to.
     * @param token the user's authentication token.
     * @param timeout a request timeout value.
     * @return the request code.
     */
    private static int requestWithoutBody(int method, @NonNull Context context,
                                          @Nullable RequestCallback callback, @NonNull String url,
                                          @NonNull String token, int timeout){

        //Init and generate the request code
        init(context);
        final int requestCode = generateRequestCode();

        //Create the request object and put it in the map
        NetworkRequest request = new NetworkRequest(callback, token);
        sRequestMap.put(requestCode, request);

        //Request a string response from the provided URL
        StringRequest volleyRequest = new StringRequest(
                //Method and url
                method, url,

                //Response listener, called if the request succeeds
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        NetworkRequest request = sRequestMap.remove(requestCode);
                        if (request.mCallback != null){
                            request.mCallback.onRequestComplete(requestCode, response);
                        }
                    }
                },

                //Error listener, called if the request fails
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.d("NetworkRequest", error.toString());
                        NetworkRequest request = sRequestMap.remove(requestCode);
                        if (request.mCallback != null){
                            request.mCallback.onRequestFailed(requestCode);
                        }
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                String token = sRequestMap.get(requestCode).mToken;
                if (!token.isEmpty()){
                    headers.put("Authorization", "Token " + token);
                }
                return headers;
            }
        };

        //Create and set the retry policy
        RetryPolicy retryPolicy = new DefaultRetryPolicy(
                timeout,
                DEFAULT_MAX_RETRIES,
                DEFAULT_RETRY_BACKOFF
        );
        volleyRequest.setRetryPolicy(retryPolicy);

        //Set the volley request to the request object
        request.setRequest(volleyRequest);

        //Add the request to the queue
        sRequestQueue.add(volleyRequest);

        return requestCode;
    }

    /**
     * Creates a request with a body.
     *
     * @param method the HTTP method of this request.
     * @param context a reference to the context.
     * @param callback the callback object.
     * @param url the url to make the request to.
     * @param token the user's authentication token.
     * @param body the body of the request.
     * @param timeout a request timeout value.
     * @return the request code.
     */
    private static int requestWithBody(int method, @NonNull Context context,
                                       @Nullable RequestCallback callback,
                                       @NonNull String url, @NonNull String token,
                                       @NonNull JSONObject body, int timeout){

        //Init and generate the request code
        init(context);
        final int requestCode = generateRequestCode();

        //Create the request object and put it in the map
        NetworkRequest request = new NetworkRequest(callback, token, body);
        sRequestMap.put(requestCode, request);

        //Request a string response from the provided URL
        StringRequest volleyRequest = new StringRequest(
                //Method and url
                method, url,

                //Response listener, called if the request succeeds
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        NetworkRequest request = sRequestMap.remove(requestCode);
                        if (request.mCallback != null){
                            request.mCallback.onRequestComplete(requestCode, response);
                        }
                    }
                },

                //Error listener, called if the request fails
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        NetworkRequest request = sRequestMap.remove(requestCode);
                        if (request.mCallback != null){
                            request.mCallback.onRequestFailed(requestCode);
                        }
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-type", "application/json");
                String token = sRequestMap.get(requestCode).mToken;
                if (!token.isEmpty()){
                    headers.put("Authorization", "Token " + token);
                }
                return headers;
            }

            @Override
            public String getBodyContentType(){
                return "application/json";
            }

            @Override
            public byte[] getBody() throws AuthFailureError{
                return sRequestMap.get(requestCode).mBody.toString().getBytes();
            }
        };

        //Create and set the retry policy
        RetryPolicy retryPolicy = new DefaultRetryPolicy(
                timeout,
                DEFAULT_MAX_RETRIES,
                DEFAULT_RETRY_BACKOFF
        );
        volleyRequest.setRetryPolicy(retryPolicy);

        //Set the volley request to the request object
        request.setRequest(volleyRequest);

        //Add the request to the queue
        sRequestQueue.add(volleyRequest);

        return requestCode;
    }


    /*----------------------------------------------*
     * NetworkRequest OBJECT ATTRIBUTES AND METHODS *
     *----------------------------------------------*/

    private RequestCallback mCallback;
    private String mToken;
    private JSONObject mBody;

    private StringRequest mRequest;


    /**
     * Constructor for requests without body.
     *
     * @param callback the callback object for this request.
     * @param token the user authentication token.
     */
    private NetworkRequest(@Nullable RequestCallback callback, @NonNull String token){
        this(callback, token, new JSONObject());
    }

    /**
     * Constructor for requests with a body.
     *
     * @param callback the callback object for this request.
     * @param token the user authentication token.
     * @param body the body of the request.
     */
    private NetworkRequest(@Nullable RequestCallback callback, @NonNull String token,
                           @NonNull JSONObject body){

        mCallback = callback;
        mToken = token;
        mBody = body;
    }

    /**
     * Associates the volley request with this NetworkRequest. Useful to cancel requests.
     *
     * @param request the volley request.
     */
    private void setRequest(@NonNull StringRequest request){
        mRequest = request;
    }


    /*private static final class DeleteWithBodyHttpClientStack extends HttpClientStack{
        public DeleteWithBodyHttpClientStack(HttpClient client){
            super(client);
        }
    }*/


    /*------------*
     * INTERFACES *
     *------------*/

    /**
     * Callback interface for NetworkRequests.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface RequestCallback{
        /**
         * Called when a request is completed successfully.
         *
         * @param requestCode the request code of the particular request.
         * @param result the result of the request as a string.
         */
        void onRequestComplete(int requestCode, String result);

        /**
         * Called when a request fails.
         *
         * @param requestCode the request code of the particular request.
         */
        void onRequestFailed(int requestCode);
    }
}
