package org.tndata.compass.model;

import android.os.Parcel;
import android.os.Parcelable;

//import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;


/**
 * Model for a UserPlace.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class UserPlace implements Parcelable{
    @SerializedName("id")
    private long mId;

    @SerializedName("place")
    private Place mPlace;

    @SerializedName("latitude")
    private double mLatitude = 0;
    @SerializedName("longitude")
    private double mLongitude = 0;


    public UserPlace(String name){
        this(new Place(name), -1, 0, 0);
    }

    public UserPlace(Place place, long id, double latitude, double longitude){
        mPlace = place;
        mId = id;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public void setId(long id){
        mId = id;
    }

    public void setLatitude(double latitude){
        mLatitude = latitude;
    }

    public void setLongitude(double longitude){
        mLongitude = longitude;
    }

    public Place getPlace(){
        return mPlace;
    }

    public long getId(){
        return mId;
    }

    public String getName(){
        return mPlace.getName();
    }

    //public LatLng getLocation(){
    //    return new LatLng(mLatitude, mLongitude);
    //}

    public double getLatitude(){
        return mLatitude;
    }

    public double getLongitude(){
        return mLongitude;
    }

    public boolean isPrimary(){
        return mPlace.isPrimary();
    }

    public boolean isSet(){
        return mPlace.isSet();
    }

    @Override
    public boolean equals(Object o){
        return (o instanceof UserPlace) && (((UserPlace)o).mId == mId);
    }

    public boolean is(Place place){
        return place.getName().equals(getName());
    }

    @Override
    public String toString(){
        return "(" + mId + ") " + mPlace.toString() + ": " + mLatitude + ", " + mLongitude;
    }

    public String getDisplayString(){
        return mPlace.getName() + ((mPlace.isPrimary() && !mPlace.isSet()) ? " (not set)" : "");
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeLong(mId);
        dest.writeParcelable(mPlace, flags);
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
    }

    public static final Creator<UserPlace> CREATOR = new Creator<UserPlace>(){
        @Override
        public UserPlace createFromParcel(Parcel source){
            return new UserPlace(source);
        }

        @Override
        public UserPlace[] newArray(int size){
            return new UserPlace[size];
        }
    };

    /**
     * Constructor to create from parcel.
     *
     * @param src the source parcel.
     */
    private UserPlace(Parcel src){
        mId = src.readLong();
        mPlace = src.readParcelable(Place.class.getClassLoader());
        mLatitude = src.readDouble();
        mLongitude = src.readDouble();
    }
}
