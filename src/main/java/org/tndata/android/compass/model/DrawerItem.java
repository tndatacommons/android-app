package org.tndata.android.compass.model;

import android.support.annotation.DrawableRes;


/**
 * Data holder for drawer items.
 *
 * @author Edited by Ismael Alonso
 * @version 2.0.0
 */
public class DrawerItem{
    private String mCaption;
    private int mIconResId;


    /**
     * Constructor. Text only items.
     *
     * @param caption the caption of the item.
     */
    public DrawerItem(String caption){
        mCaption = caption;
        mIconResId = 0;
    }

    /**
     * Constructor. Text and icon.
     *
     * @param caption the caption of the item.
     * @param iconResId the resource id of the icon.
     */
    public DrawerItem(String caption, @DrawableRes int iconResId){
        mCaption = caption;
        mIconResId = iconResId;
    }

    /**
     * Caption getter.
     *
     * @return the item caption.
     */
    public String getCaption(){
        return mCaption;
    }

    /**
     * Icon resource id getter.
     *
     * @return the icon resource id.
     */
    public int getIconResId(){
        return mIconResId;
    }
}
