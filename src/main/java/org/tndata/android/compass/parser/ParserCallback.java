package org.tndata.android.compass.parser;

/**
 * Callback interface for the parser.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public interface ParserCallback{
    /**
     * Called when the parsing is complete.
     *
     * @param requestCode the request code.
     * @param results the result set.
     */
    void onParseSuccess(int requestCode, ParserResults results);
}
