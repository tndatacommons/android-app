package org.tndata.android.compass.model;


/**
 * Model class for a package.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class Package{
    private int id;
    private String title;
    private String description;
    private String html_description;
    private String consent_summary;
    private String html_consent_summary;
    private String consent_more;
    private String html_consent_more;


    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public String getHtmlDescription(){
        return html_description;
    }

    public String getConsentSummary(){
        return consent_summary;
    }

    public String getHtmlConsentSummary(){
        return html_consent_summary;
    }

    public String getConsent(){
        return consent_more;
    }

    public String getHtmlConsent(){
        return html_consent_more;
    }
}
