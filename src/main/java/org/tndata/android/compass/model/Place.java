package org.tndata.android.compass.model;

import java.io.Serializable;


/**
 * A model representation of a place.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class Place implements Serializable{
    static final long serialVersionUID = 9654318439L;

    private String name = "";

    private boolean primary = false;
    private boolean set = false;


    public Place(String name){
        this.name = name;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setPrimary(boolean primary){
        this.primary = primary;
    }

    public void setSet(boolean set){
        this.set = set;
    }

    public String getName(){
        return name;
    }

    public boolean isPrimary(){
        return primary;
    }

    public boolean isSet(){
        return set;
    }

    @Override
    public String toString(){
        return name;
    }
}
