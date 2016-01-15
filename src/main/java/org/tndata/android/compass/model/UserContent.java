package org.tndata.android.compass.model;

/**
 * Superclass of all classes that represent UserContent. The rationale behind this class
 * is that in order for the MapDeserializer to work as expected we need a generic, direct
 * way to retrieve the ID of the piece of content embedded into the UserContent object.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public abstract class UserContent extends TDCBase{
    /**
     * Getter for the id of the regular content class embedded into the user content class.
     *
     * @return the id of such piece of content.
     */
    public abstract int getObjectId();
}
