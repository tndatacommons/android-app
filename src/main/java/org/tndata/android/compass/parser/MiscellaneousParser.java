package org.tndata.android.compass.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tndata.android.compass.model.Instrument;
import org.tndata.android.compass.model.Reward;
import org.tndata.android.compass.model.SearchResult;
import org.tndata.android.compass.model.Survey;

import java.util.ArrayList;
import java.util.List;


/**
 * Class containing parser methods that don't quite fit anywhere else.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public final class MiscellaneousParser extends ParserMethods{
    public static Instrument parseInstrument(String src){
        return sGson.fromJson(src, Instrument.class);
    }

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

    /**
     * Parses a list of rewards from a JSON string.
     *
     * @param src the source string in JSON format.
     * @return a list of categories.
     */
    public static List<Reward> parseRewards(String src){
        List<Reward> rewards = new ArrayList<>();
        try{
            JSONArray rewardArray = new JSONObject(src).getJSONArray("results");
            for (int i = 0; i < rewardArray.length(); i++){
                rewards.add(sGson.fromJson(rewardArray.getString(i), Reward.class));
            }
        }
        catch (JSONException jsonx){
            jsonx.printStackTrace();
        }
        return rewards;
    }
}
