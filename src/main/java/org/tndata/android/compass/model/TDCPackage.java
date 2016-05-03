package org.tndata.android.compass.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;


/**
 * Model class for a package.
 *
 * @author Ismael Alonso
 * @version 1.1.0
 */
public class TDCPackage extends TDCBase{
    public static final String TYPE = "package";


    @SerializedName("accepted")
    private boolean mAccepted;
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

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        super.writeToParcel(dest, flags);
        dest.writeByte((byte)(mAccepted ? 1 : 0));
        dest.writeParcelable(mCategory, flags);
    }

    public static final Creator<TDCPackage> CREATOR = new Creator<TDCPackage>(){
        @Override
        public TDCPackage createFromParcel(Parcel source){
            return null;
        }

        @Override
        public TDCPackage[] newArray(int size){
            return new TDCPackage[size];
        }
    };

    private TDCPackage(Parcel src){
        super(src);
        mAccepted = src.readByte() == 1;
        mCategory = src.readParcelable(PackageContent.class.getClassLoader());
    }


    /**
     * Holder for the inner package data.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private static class PackageContent extends TDCBase{
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

        @Override
        public int describeContents(){
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags){
            super.writeToParcel(dest, flags);
            dest.writeString(mTitle);
            dest.writeString(mDescription);
            dest.writeString(mHtmlDescription);
            dest.writeString(mConsentSummary);
            dest.writeString(mHtmlConsentSummary);
            dest.writeString(mConsent);
            dest.writeString(mHtmlConsent);
        }

        public static final Creator<PackageContent> CREATOR = new Creator<PackageContent>(){
            @Override
            public PackageContent createFromParcel(Parcel source){
                return new PackageContent(source);
            }

            @Override
            public PackageContent[] newArray(int size){
                return new PackageContent[size];
            }
        };

        private PackageContent(Parcel src){
            super(src);
            mTitle = src.readString();
            mDescription = src.readString();
            mHtmlDescription = src.readString();
            mConsentSummary = src.readString();
            mHtmlConsentSummary = src.readString();
            mConsent = src.readString();
            mHtmlConsent = src.readString();
        }

        @Override
        protected String getType(){
            return "";
        }
    }
}
