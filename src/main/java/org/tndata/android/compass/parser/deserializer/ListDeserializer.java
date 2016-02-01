package org.tndata.android.compass.parser.deserializer;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.tndata.android.compass.model.TDCBase;
import org.tndata.android.compass.parser.ParserMethods;
import org.tndata.android.compass.util.CompassUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Generic deserializer for Lists. Converts JsonArrays to Lists (ArrayList).
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ListDeserializer extends ParserMethods implements JsonDeserializer<List<?>>{
    @Override
    public List<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context){
        return parse(json);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> parse(JsonElement item){
        //Create the list where the parsed objects will be put
        List<T> list = new ArrayList<>();

        //We've got some JSONArrays that come as JSONObjects from the api, skip
        //  those to return an empty List, which is what that represents. That
        //  is not regular expected behavior though
        if (!item.toString().equals("{}")){
            Log.d("ListDeserializer", item.toString());
            //Parse all the elements of the array and add them to the list
            for (JsonElement element:item.getAsJsonArray()){
                try{
                    String type = ((JsonObject)element).get("object_type").getAsString();
                    list.add((T)sGson.fromJson(element, CompassUtil.getTypeOf(type)));
                }
                catch (ClassCastException ccx){
                    ccx.printStackTrace();
                    list.add((T)sGson.fromJson(element, Long.class));
                }
            }
        }

        return list;
    }
}
