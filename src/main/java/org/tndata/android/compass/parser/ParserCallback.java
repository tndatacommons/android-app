package org.tndata.android.compass.parser;

/**
 * Callback interface for the parser.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public interface ParserCallback{
    /**
     * Called to potentially process the parsing result in the background if needed.
     *
     * @param requestCode the request code.
     * @param result the result set.
     */
    void onProcessResult(int requestCode, ParserModels.ResultSet result);

    /**
     * Called in the foreground when parsing is complete.
     *
     * @param requestCode the request code.
     * @param result the result set.
     */
    void onParseSuccess(int requestCode, ParserModels.ResultSet result);
}
