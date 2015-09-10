package org.tndata.android.compass.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;


/**
 * A representation of a place.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class Place implements Serializable{
    static final long serialVersionUID = 9654318439L;

    private int id = 0;

    private String name = "";

    private double latitude = 0;
    private double longitude = 0;

    private boolean primary = false;
    private boolean set = false;


    public void setName(String name){
        this.name = name;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public void setPrimary(boolean primary){
        this.primary = primary;
    }

    public void setSet(boolean set){
        this.set = set;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public LatLng getLocation(){
        return new LatLng(latitude, longitude);
    }

    public boolean isPrimary(){
        return primary;
    }

    public boolean isSet(){
        return set;
    }

    @Override
    public boolean equals(Object o){
        return (o instanceof Place) && (((Place)o).name.equals(name));
    }

    @Override
    public String toString(){
        return "(" + id + ") " + name + ": " + latitude + ", " + longitude;
    }

    public String getDisplayString(){
        return name + ((primary&&!set) ? " (not set)" : "");
    }
}
