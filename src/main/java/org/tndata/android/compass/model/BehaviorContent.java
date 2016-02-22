package org.tndata.android.compass.model;

import android.content.Context;
import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.R;
import org.tndata.android.compass.ui.ContentContainer;
import org.tndata.android.compass.util.ImageLoader;

import java.io.Serializable;
import java.util.Set;


/**
 * Model class for behaviors.
 *
 * @author Edited by Ismael Alonso
 * @version 1.0.0
 */
public class BehaviorContent extends TDCContent implements Serializable, ContentContainer.ContainerDisplayable{
    private static final long serialVersionUID = 7747989797893422842L;

    public static final String TYPE = "behavior";


    @SerializedName("more_info")
    private String mMoreInfo = "";
    @SerializedName("html_more_info")
    private String mHtmlMoreInfo = "";
    @SerializedName("external_resource")
    private String mExternalResource = "";
    @SerializedName("external_resource_name")
    private String mExternalResourceName = "";

    @SerializedName("goals")
    private Set<Long> mGoalIdSet;
    @SerializedName("actions_count")
    private int mActionCount = 0;


    private String mColor;


    /*---------*
     * GETTERS *
     *---------*/

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

    public Set<Long> getGoalIdSet(){
        return mGoalIdSet;
    }

    public int getActionCount(){
        return mActionCount;
    }

    @Override
    public String getColor(Context context){
        if (mColor == null || mColor.isEmpty()){
            return String.format("#%06X", 0xFFFFFF & context.getResources().getColor(R.color.grow_primary));
        }
        return mColor;
    }

    @Override
    protected String getType(){
        return TYPE;
    }


    /*---------*
     * SETTERS *
     *---------*/

    public void setColor(String color){
        mColor = color;
    }


    /*---------*
     * UTILITY *
     *---------*/

    /**
     * Given a Context and an ImageView, load this Behavior's icon into the ImageView.
     *
     * @param imageView: an ImageView
     */
    public void loadIconIntoView(ImageView imageView){
        String iconUrl = getIconUrl();
        if(iconUrl != null && !iconUrl.isEmpty()) {
            ImageLoader.loadBitmap(imageView, iconUrl, new ImageLoader.Options());
        }
    }

    @Override
    public String toString(){
        return "BehaviorContent #" + getId() + ": " + getTitle();
    }
}
