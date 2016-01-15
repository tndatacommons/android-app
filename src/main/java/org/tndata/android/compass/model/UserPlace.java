package org.tndata.android.compass.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;


/**
 * Model for a UserPlace.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class UserPlace implements Serializable{
    static final long serialVersionUID = 9650185439L;


    private Place place;

    private int id;

    private double latitude = 0;
    private double longitude = 0;


    public UserPlace(Place place, int id, double latitude, double longitude){
        this.place = place;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public Place getPlace(){
        return place;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return place.getName();
    }

    public LatLng getLocation(){
        return new LatLng(latitude, longitude);
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public boolean isPrimary(){
        return place.isPrimary();
    }

    public boolean isSet(){
        return place.isSet();
    }

    @Override
    public boolean equals(Object o){
        return (o instanceof UserPlace) && (((UserPlace)o).id == id);
    }

    @Override
    public String toString(){
        return "(" + id + ") " + place.toString() + ": " + latitude + ", " + longitude;
    }

    public String getDisplayString(){
        return place.getName() + ((place.isPrimary() && !place.isSet()) ? " (not set)" : "");
    }
}
