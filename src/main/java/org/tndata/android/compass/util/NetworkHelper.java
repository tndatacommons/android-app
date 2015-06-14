package org.tndata.android.compass.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class NetworkHelper {

    public static InputStream httpPostStream(String url,
                                             Map<String, String> headers, String body) {

        DefaultHttpClient client = new DefaultHttpClient();
        HttpResponse httpResponse = null;
        // url with the post data
        HttpPost httpPost = new HttpPost(url);
        try {
            if (body != null && !body.isEmpty()) {
                // pass the body to a string builder/entity
                StringEntity se = new StringEntity(body);

                // sets the post request as the resulting string
                httpPost.setEntity(se);
            }

            if (headers != null) {
                // set all of our headers
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    httpPost.setHeader(header.getKey(), header.getValue());
                }
            }

            // Handles what is returned from the page
            // ResponseHandler responseHandler = new BasicResponseHandler();
            HttpContext responseHandler = new BasicHttpContext();
            httpResponse = client.execute(httpPost, responseHandler);
            int code = httpResponse.getStatusLine().getStatusCode();
            if (code != 201 && code != 200) {
                return null;
            }
            HttpEntity getResponseEntity = httpResponse.getEntity();
            return getResponseEntity.getContent();

        } catch (IOException e) {
            httpPost.abort();
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream httpPutStream(String url,
                                            Map<String, String> headers, String body) {

        DefaultHttpClient client = new DefaultHttpClient();
        HttpResponse httpResponse = null;
        // url with the post data
        HttpPut httpPut = new HttpPut(url);
        try {
            if (body != null && !body.isEmpty()) {
                // pass the body to a string builder/entity
                StringEntity se = new StringEntity(body);

                // sets the post request as the resulting string
                httpPut.setEntity(se);
            }

            if (headers != null) {
                // set all of our headers
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    httpPut.setHeader(header.getKey(), header.getValue());
                }
            }

            // Handles what is returned from the page
            // ResponseHandler responseHandler = new BasicResponseHandler();
            HttpContext responseHandler = new BasicHttpContext();
            httpResponse = client.execute(httpPut, responseHandler);
            int code = httpResponse.getStatusLine().getStatusCode();
            if (code != 201 && code != 200) {
                return null;
            }
            HttpEntity getResponseEntity = httpResponse.getEntity();
            return getResponseEntity.getContent();

        } catch (IOException e) {
            httpPut.abort();
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream httpGetStream(String url,
                                            Map<String, String> headers) {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        if (headers != null) {
            // set all of our headers
            for (Map.Entry<String, String> header : headers.entrySet()) {
                httpGet.setHeader(header.getKey(), header.getValue());
            }
        }
        HttpResponse httpResponse = null;
        try {
            HttpContext responseHandler = new BasicHttpContext();
            httpResponse = client.execute(httpGet, responseHandler);
            int code = httpResponse.getStatusLine().getStatusCode();
            if (code != 201 && code != 200) {
                return null;
            }
            HttpEntity getResponseEntity = httpResponse.getEntity();
            return getResponseEntity.getContent();
        } catch (IOException e) {
            httpGet.abort();
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream httpDeleteStream(String url,
                                               Map<String, String> headers, String body) {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(url);
        if (headers != null) {
            // set all of our headers
            for (Map.Entry<String, String> header : headers.entrySet()) {
                httpDelete.setHeader(header.getKey(), header.getValue());
            }
        }
        HttpResponse httpResponse = null;
        try {
            if (body != null && !body.isEmpty()) {
                // pass the body to a string builder/entity
                StringEntity se = new StringEntity(body);

                // sets the post request as the resulting string
                httpDelete.setEntity(se);
            }
            HttpContext responseHandler = new BasicHttpContext();
            httpResponse = client.execute(httpDelete, responseHandler);
            int code = httpResponse.getStatusLine().getStatusCode();
            if (code != 201 && code != 200 && code != 204) {
                return null;
            }
            HttpEntity getResponseEntity = httpResponse.getEntity();
            if (getResponseEntity != null) {
                return getResponseEntity.getContent();
            }
        } catch (IOException e) {
            httpDelete.abort();
            e.printStackTrace();
        }
        return null;
    }
}
