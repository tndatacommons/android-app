package org.tndata.android.compass.model;

import android.support.annotation.NonNull;

import java.io.Serializable;


/**
 * Base class for the content. Use it for both, user and regular content and rewire
 * its methods in user content to the instance of regular content.
 *
 * @author Edited by Ismael Alonso
 * @version 1.0.0
 */
public abstract class TDCBase implements Serializable, Comparable<TDCBase>{
    private static final long serialVersionUID = -7297141782846963404L;

    //TODO make the id a long to avoid range problems. Not a problem yet.
    private int id = -1;
    private String title = "";
    private String title_slug = "";
    private String description = "";
    private String html_description = "";
    private String icon_url = "";
    private boolean editable = false;

    //TODO this is not necessary anymore since user-content is going to use the ID
    private int mappingId = -1;


    /*---------*
     * SETTERS *
     *---------*/

    public void setId(int id){
        this.id = id;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setTitleSlug(String titleSlug){
        this.title_slug = titleSlug;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setHTMLDescription(String htmlDescription){
        this.html_description = htmlDescription;
    }

    public void setIconUrl(String iconUrl){
        this.icon_url = iconUrl;
    }

    public void setEditable(boolean editable){
        this.editable = editable;
    }


    /*---------*
     * GETTERS *
     *---------*/

    public int getId() {
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getTitleSlug(){
        return title_slug;
    }

    public String getDescription(){
        return description;
    }

    public String getHTMLDescription(){
        return html_description;
    }

    public String getIconUrl(){
        return icon_url;
    }

    public boolean isEditable(){
        return editable;
    }


    /*---------*
     * UTILITY *
     *---------*/

    @Override
    public boolean equals(Object object){
        boolean result = false;
        if (object == null){
            result = false;
        }
        else if (object == this){
            result = true;
        }
        else if (object instanceof TDCBase){
            if (this.getId() == ((TDCBase)object).getId()){
                result = true;
            }
        }
        return result;
    }

    @Override
    public int hashCode(){
        return 21 + title.hashCode();
    }

    @Override
    public int compareTo(@NonNull TDCBase another){
        if (getId() == another.getId()){
            return 0;
        }
        else if (getId() < another.getId()){
            return -1;
        }
        else{
            return 1;
        }
    }
}
