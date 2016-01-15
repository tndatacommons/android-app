package org.tndata.android.compass.parser.deserializer;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;


/**
 * This class parses arrays into Sets, concretely HashSets. This needs testing
 * and possibly tweaking.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class SetDeserializer implements JsonDeserializer<Set<?>>{
    @Override
    public Set<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context){
        return parse(json, ((ParameterizedType)typeOfT).getActualTypeArguments()[0]);
    }

    @SuppressWarnings("unchecked")
    public <T> Set<T> parse(JsonElement item, T type){
        //Create the set where the parsed objects will be put
        Set<T> set = new HashSet<>();

        //We've got some JSONArrays that come as JSONObjects from the api, skip
        //  those to return an empty Set, which is what that represents. That
        //  is not regular expected behavior though
        if (!item.toString().equals("{}")){
            //Build the GSON parser that looks for field matches
            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                    .create();

            //Parse all the elements of the array and add them to the set
            for (JsonElement element:item.getAsJsonArray()){
                set.add((T)gson.fromJson(element, (Class<?>)type));
            }

            Log.d("SetDeserializer", item.toString());
        }

        return set;
    }
}
