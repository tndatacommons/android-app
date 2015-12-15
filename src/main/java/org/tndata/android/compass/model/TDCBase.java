package org.tndata.android.compass.model;

import java.io.Serializable;

public class TDCBase implements Serializable{
    private static final long serialVersionUID = -7297141782846963404L;

    private int id = -1;
    private String title = "";
    private String title_slug = "";
    private String description = "";
    private String html_description = "";
    private int mappingId = -1;

    private boolean editable = true;


    public TDCBase(){

    }

    public TDCBase(int id, String name, String nameSlug, String description, String html_description){
        this.setId(id);
        this.setTitle(name);
        this.setTitleSlug(nameSlug);
        this.setDescription(description);
        this.setHTMLDescription(html_description);
    }

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getTitleSlug() {
        return this.title_slug;
    }

    public String getDescription() {
        return this.description;
    }

    public String getHTMLDescription() { return this.html_description; }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitleSlug(String titleSlug) {
        this.title_slug = titleSlug;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHTMLDescription(String html_description) { this.html_description = html_description; }

    public int getMappingId() {
        return mappingId;
    }

    public void setMappingId(int mappingId) {
        this.mappingId = mappingId;
    }

    @Deprecated
    public void setCustomTriggersAllowed(boolean customTriggersAllowed){
        editable = customTriggersAllowed;
    }

    @Deprecated
    public boolean areCustomTriggersAllowed(){
        return editable;
    }

    public void setEditable(boolean editable){
        this.editable = editable;
    }

    public boolean isEditable(){
        return editable;
    }
}
