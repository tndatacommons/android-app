package org.tndata.android.compass.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


/**
 * Model class for actions.
 *
 * @author Edited by Ismael Alonso
 * @version 1.0.0
 */
public class ActionContent extends TDCContent implements Serializable{
    private static final long serialVersionUID = 2919447130236951923L;

    public static final String TYPE = "action";


    @SerializedName("sequence_order")
    private int mSequenceOrder;
    @SerializedName("more_info")
    private String mMoreInfo;
    @SerializedName("html_more_info")
    private String mHtmlMoreInfo;
    @SerializedName("external_resource")
    private String mExternalResource;
    @SerializedName("external_resource_name")
    private String mExternalResourceName;
    @SerializedName("default_trigger")
    private Trigger mTrigger;

    @SerializedName("behavior")
    private int mBehaviorId;


    /*---------*
     * GETTERS *
     *---------*/

    public int getSequenceOrder(){
        return mSequenceOrder;
    }

    public String getMoreInfo(){
        return mMoreInfo;
    }

    public String getHTMLMoreInfo(){
        return mHtmlMoreInfo;
    }

    public String getExternalResource(){
        return mExternalResource;
    }

    public String getExternalResourceName(){
        return mExternalResourceName;
    }

    public Trigger getTrigger(){
        return mTrigger;
    }

    public int getBehaviorId(){
        return mBehaviorId;
    }

    @Override
    protected String getType(){
        return TYPE;
    }


    /*---------*
     * UTILITY *
     *---------*/

    @Override
    public String toString(){
        return "ActionContent #" + getId() + ": " + getTitle();
    }
}
