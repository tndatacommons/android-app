package org.tndata.android.compass.parser;

import android.content.Context;
import android.support.annotation.NonNull;


/**
 * Main interface to the parser. It receives a string and a callback, generates a request
 * code, and fires up a task to do the parsing in the background, as the parsing may be
 * a heavy task in some instances where the user has a lot of content selected.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public final class Parser{
    private static final int MAX_REQUEST_CODE = 999999;

    //Need to keep track of the last request code delivered to avoid collisions
    private static int sLastRequestCode = 0;


    /**
     * Generates a unique request code.
     *
     * @return the request code.
     */
    private static int generateRequestCode(){
        if (sLastRequestCode >= MAX_REQUEST_CODE){
            sLastRequestCode = 0;
        }
        return ++sLastRequestCode;
    }

    /**
     * Starts the parsing process.
     *
     * @param src the string to parse.
     * @param callback the callback object.
     * @return a request code.
     */
    public static int parse(@NonNull Context context, @NonNull String src,
                            @NonNull ParserCallback callback){
        int requestCode = generateRequestCode();
        new ParserWorker(context, requestCode, src, callback).execute();
        return requestCode;
    }

    /**
     * Modified default constructor. This class should not be instantiable.
     */
    private Parser(){

    }
}
