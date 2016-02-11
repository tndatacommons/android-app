package org.tndata.android.compass.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.model.*;
import org.tndata.android.compass.model.Package;

import java.util.ArrayList;
import java.util.List;


/**
 * Class containing parser methods that don't quite fit anywhere else.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public final class MiscellaneousParser extends ParserMethods{
    public static Survey parseSurvey(String src){
        return sGson.fromJson(src, Survey.class);
    }

    public static List<SearchResult> parseSearchResults(String src){
        List<SearchResult> results = new ArrayList<>();
        try{
            JSONArray resultArray = new JSONObject(src).getJSONArray("results");
            for (int i = 0; i < resultArray.length(); i++){
                results.add(sGson.fromJson(resultArray.getString(i), SearchResult.class));
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return results;
    }

    public static org.tndata.android.compass.model.Package parsePackage(String src){
        try{
            Package myPackage = sGson.fromJson(new JSONObject(src).getString("category"), Package.class);
            myPackage.setId(new JSONObject(src).getInt("id"));
            return myPackage;
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
            return null;
        }
    }
}
