package org.tndata.android.compass.model;

/**
 * Interface for content that has been selected by the user from the TDC library.
 *
 * @author Ismael Alonso
 */
public interface UserSelectedContent{
    long getContentId();
    String getTitle();
    String getDescription();
    String getHTMLDescription();
    String getIconUrl();
}
