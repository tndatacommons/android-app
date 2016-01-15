package org.tndata.android.compass.parser.deserializer;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Generic deserializer for Lists. Converts JsonArrays to Lists (ArrayList).
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ListDeserializer implements JsonDeserializer<List<?>>{
    @Override
    public List<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context){
        return parse(json, ((ParameterizedType)typeOfT).getActualTypeArguments()[0]);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> parse(JsonElement item, T type){
        //Create the list where the parsed objects will be put
        List<T> list = new ArrayList<>();

        //We've got some JSONArrays that come as JSONObjects from the api, skip
        //  those to return an empty List, which is what that represents. That
        //  is not regular expected behavior though
        if (!item.toString().equals("{}")){
            //Build the GSON parser that looks for field matches
            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                    .create();

            //Parse all the elements of the array and add them to the list
            for (JsonElement element:item.getAsJsonArray()){
                list.add((T)gson.fromJson(element, (Class<?>)type));
            }
        }

        return list;
    }
}
