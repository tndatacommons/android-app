package org.tndata.android.compass.model;

import android.content.Context;

import org.tndata.android.compass.R;

import java.io.Serializable;


/**
 * Model superclass for anything that can be classified as a goal.
 *
 * @author Ismael Alonso.
 * @version 1.0.0
 */
public abstract class Goal extends UserContent implements Serializable{
    private static final long serialVersionUID = 6532189438426955496L;

    public abstract String getTitle();

    public abstract String getIconUrl();

    public abstract String getColor(Context context);
}
