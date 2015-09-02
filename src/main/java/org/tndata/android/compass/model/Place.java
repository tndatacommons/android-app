package org.tndata.android.compass.model;

import com.google.android.gms.maps.model.LatLng;


/**
 * A representation of a place.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class Place{
    private String name = "";

    private double latitude = 0;
    private double longitude = 0;


    public void setName(String name){
        this.name = name;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public String getName(){
        return name;
    }

    public LatLng getLocation(){
        return new LatLng(latitude, longitude);
    }
}
