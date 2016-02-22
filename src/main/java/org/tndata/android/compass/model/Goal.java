package org.tndata.android.compass.model;

import org.tndata.android.compass.ui.ContentContainer;

import java.io.Serializable;


/**
 * Model superclass for anything that can be classified as a goal.
 *
 * @author Ismael Alonso.
 * @version 1.0.0
 */
public abstract class Goal
        extends UserContent
        implements Serializable, ContentContainer.ContainerDisplayable{

    private static final long serialVersionUID = 6532189438426955496L;
}
