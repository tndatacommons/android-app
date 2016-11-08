package org.tndata.android.compass.parser;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.tndata.android.compass.parser.deserializer.ListDeserializer;
import org.tndata.android.compass.parser.deserializer.MapDeserializer;
import org.tndata.android.compass.parser.deserializer.SetDeserializer;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Contains the methods used by the Parser to generate content out of a JSON string.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public abstract class ParserMethods{
    public static final Gson sGson = new GsonBuilder()
            .registerTypeAdapter(Map.class, new MapDeserializer())
            .registerTypeAdapter(List.class, new ListDeserializer())
            .registerTypeAdapter(Set.class, new SetDeserializer())
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .create();
}
