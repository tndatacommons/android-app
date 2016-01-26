package org.tndata.android.compass.model;

import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.util.ImageLoader;

import java.io.Serializable;


/**
 * Model class for categories.
 *
 * @author Edited by Ismael Alonso
 * @version 1.0.0
 */
public class CategoryContent extends TDCContent implements Serializable{
    private static final long serialVersionUID = -1751642109285216370L;

    public static final String TYPE = "category";


    @SerializedName("order")
    private int mOrder = -1;
    @SerializedName("image_url")
    private String mImageUrl = "";
    @SerializedName("color")
    private String mColor = "";
    @SerializedName("secondary_color")
    private String mSecondaryColor = "";

    @SerializedName("packaged_content")
    private boolean mPackagedContent = false;


    /*---------*
     * GETTERS *
     *---------*/

    public int getOrder(){
        return mOrder;
    }

    public String getImageUrl(){
        return mImageUrl;
    }

    public String getColor(){
        return mColor;
    }

    public String getSecondaryColor(){
        return this.mSecondaryColor;
    }

    public boolean isPackagedContent(){
        return mPackagedContent;
    }

    @Override
    protected String getType(){
        return TYPE;
    }

    /*---------*
     * UTILITY *
     *---------*/

    public void loadImageIntoView(ImageView imageView){
        String url = getImageUrl();
        if (url != null && !url.isEmpty()){
            ImageLoader.Options options = new ImageLoader.Options()
                    .setUsePlaceholder(false)
                    .setCropToCircle(true);
            ImageLoader.loadBitmap(imageView, url, options);
        }
    }

    @Override
    public String toString(){
        return "CategoryContent #" + getId() + ": " + getTitle();
    }
}
