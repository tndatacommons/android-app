package org.tndata.android.compass.model;

import android.os.Parcel;


/**
 * Model superclass for anything that can be classified as a goal.
 *
 * @author Ismael Alonso.
 * @version 1.0.0
 */
public abstract class Goal extends UserContent{
    protected Goal(){

    }

    protected Goal(Parcel src){
        super(src);
    }

    public abstract String getTitle();
}
