package org.tndata.android.compass.parser.deserializer;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.tndata.android.compass.model.UserSelectedContent;
import org.tndata.android.compass.util.CompassUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Deserializer to convert JSONArrays into Maps, specifically HashMaps.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class MapDeserializer implements JsonDeserializer<Map<Long, ? extends UserSelectedContent>>{
    @Override
    public Map<Long, ? extends UserSelectedContent> deserialize(JsonElement json, Type typeOfT,
                                                                JsonDeserializationContext context){
        return parse(json);
    }

    @SuppressWarnings("unchecked")
    public <T extends UserSelectedContent> Map<Long, T> parse(JsonElement item){
        //Create the set where the parsed objects will be put
        Map<Long, T> map = new HashMap<>();

        //We've got some JSONArrays that come as JSONObjects from the api, skip
        //  those to return an empty Map, which is what that represents. That
        //  is not regular expected behavior though
        if (!item.toString().equals("{}")){
            //Build the GSON parser that looks for field matches
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Set.class, new SetDeserializer())
                    .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                    .create();

            //Parse all the elements of the array and put them into the map
            for (JsonElement element:item.getAsJsonArray()){
                String type = ((JsonObject)element).get("object_type").getAsString();
                T object = (T)gson.fromJson(element, CompassUtil.getTypeOf(type));
                object.init();
                map.put(object.getContentId(), object);
            }
        }

        return map;
    }
}
