package org.tndata.android.compass.task;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tndata.android.compass.model.Instrument;
import org.tndata.android.compass.util.Constants;
import org.tndata.android.compass.util.NetworkHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class InstrumentLoaderTask extends AsyncTask<String, Void, ArrayList<Instrument>> {
    private InstrumentLoaderListener mCallback;
    private static Gson gson = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.IDENTITY).create();

    public interface InstrumentLoaderListener {
        public void instrumentsLoaded(ArrayList<Instrument> instruments);
    }

    public InstrumentLoaderTask(InstrumentLoaderListener callback) {
        mCallback = callback;
    }

    @Override
    protected ArrayList<Instrument> doInBackground(String... params) {
        String token = params[0];
        String instrumentId = null;
        if (params.length > 1) {
            instrumentId = params[1];
        }
        String url = Constants.BASE_URL + "survey/instruments/";
        if (instrumentId != null) {
            url += instrumentId + "/";
        }
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
        headers.put("Authorization", "Token " + token);

        InputStream stream = NetworkHelper.httpGetStream(url, headers);
        if (stream == null) {
            return null;
        }
        String result = "";
        String instrumentResponse = "";
        try {

            BufferedReader bReader = new BufferedReader(new InputStreamReader(
                    stream, "UTF-8"));

            String line = null;
            while ((line = bReader.readLine()) != null) {
                result += line;
            }
            bReader.close();

            instrumentResponse = Html.fromHtml(result).toString();

            JSONObject jObject = new JSONObject(instrumentResponse);
            Log.d("instrument response", jObject.toString(2));
            ArrayList<Instrument> instruments = new ArrayList<Instrument>();

            if (instrumentId != null) {
                Instrument instrument = gson.fromJson(instrumentResponse, Instrument.class);
                instruments.add(instrument);
            } else {
                JSONArray jsonArray = jObject.getJSONArray("results");
                for (int i = 0; i < jsonArray.length(); i++) {
                    Instrument instrument = gson.fromJson(jsonArray.getString(i), Instrument.class);
                    instruments.add(instrument);
                }
            }

            return instruments;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Instrument> instruments) {
        mCallback.instrumentsLoaded(instruments);
    }
}
