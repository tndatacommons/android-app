package org.tndata.android.compass.model;

import com.google.gson.annotations.SerializedName;


/**
 * Model class for a package.
 *
 * @author Ismael Alonso
 * @version 1.1.0
 */
public class TDCPackage extends TDCContent{
    public static final String TYPE = "package";


    @SerializedName("category")
    private PackageContent mCategory;


    @Override
    protected String getType(){
        return TYPE;
    }

    public String getTitle(){
        return mCategory.mTitle;
    }

    public String getDescription(){
        return mCategory.mDescription;
    }

    public String getHtmlDescription(){
        return mCategory.mHtmlDescription;
    }

    public String getConsentSummary(){
        return mCategory.mConsentSummary;
    }

    public String getHtmlConsentSummary(){
        return mCategory.mHtmlConsentSummary;
    }

    public String getConsent(){
        return mCategory.mConsent;
    }

    public String getHtmlConsent(){
        return mCategory.mHtmlConsent;
    }


    /**
     * Holder for the inner package data.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class PackageContent{
        @SerializedName("title")
        private String mTitle;
        @SerializedName("description")
        private String mDescription;
        @SerializedName("html_description")
        private String mHtmlDescription;
        @SerializedName("consent_summary")
        private String mConsentSummary;
        @SerializedName("html_consent_summary")
        private String mHtmlConsentSummary;
        @SerializedName("consent_more")
        private String mConsent;
        @SerializedName("html_consent_more")
        private String mHtmlConsent;
    }
}
