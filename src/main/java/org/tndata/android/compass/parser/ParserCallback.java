package org.tndata.android.compass.parser;

/**
 * Callback interface for the parser.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public interface ParserCallback<T>{
    void onBackgroundProcessing(int requestCode, T result);

    /**
     * Called when the parsing is complete.
     *
     * @param requestCode the request code.
     * @param result the result set.
     */
    void onParseSuccess(int requestCode, T result);
}
