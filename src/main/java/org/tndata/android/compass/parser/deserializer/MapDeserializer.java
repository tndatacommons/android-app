package org.tndata.android.compass.parser.deserializer;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import org.tndata.android.compass.model.UserContent;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


/**
 * Deserializer to convert JSONArrays into Maps, concretely HashMaps.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class MapDeserializer implements JsonDeserializer<Map<Integer, ? extends UserContent>>{
    @Override
    public Map<Integer, ? extends UserContent> deserialize(JsonElement json, Type typeOfT,
                                                           JsonDeserializationContext context){
        return parse(json, ((ParameterizedType)typeOfT).getActualTypeArguments()[0]);
    }

    @SuppressWarnings("unchecked")
    public <T extends UserContent> Map<Integer, T> parse(JsonElement item, Type type){
        Log.d("MapDeserializer", item.toString());

        //Create the set where the parsed objects will be put
        Map<Integer, T> map = new HashMap<>();

        //We've got some JSONArrays that come as JSONObjects from the api, skip
        //  those to return an empty Set, which is what that represents. That
        //  is not regular expected behavior though
        if (!item.toString().equals("{}")){
            //Build the GSON parser that looks for field matches
            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                    .create();

            //Parse all the elements of the array and put them into the map
            for (JsonElement element:item.getAsJsonArray()){
                T object = (T)gson.fromJson(element, (Class<?>)type);
                map.put(object.getObjectId(), object);
            }
        }

        return map;
    }
}
