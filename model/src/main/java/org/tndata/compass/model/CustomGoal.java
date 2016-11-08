package org.tndata.compass.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


/**
 * Model for a custom goal.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CustomGoal extends Goal{
    public static final String TYPE = "customgoal";


    //API delivered values
    @SerializedName("title")
    private String mTitle;

    //Set values
    private List<CustomAction> mActions;


    public CustomGoal(String title){
        mTitle = title;
        mActions = new ArrayList<>();
    }

    @Override
    public long getContentId(){
        return getId();
    }

    @Override
    public void init(){
        if (mActions == null){
            mActions = new ArrayList<>();
        }
    }

    public void setTitle(String title){
        mTitle = title;
    }

    @Override
    public String getTitle(){
        return mTitle;
    }

    @Override
    protected String getType(){
        return TYPE;
    }

    @Override
    public boolean isEditable(){
        return true;
    }

    public void setActions(List<CustomAction> actions){
        mActions = actions;
        for (CustomAction action:mActions){
            action.setGoal(this);
        }
    }

    public List<CustomAction> getActions(){
        return mActions;
    }

    public void addAction(CustomAction action){
        action.setGoal(this);
        mActions.add(action);
    }

    public void removeAction(CustomAction action){
        mActions.remove(action);
    }

    @Override
    public String toString(){
        return "CustomGoal #" + getId() + ": " + mTitle;
    }


    /*------------*
     * PARCELABLE *
     *------------*/

    @Override
    public void writeToParcel(Parcel dest, int flags){
        super.writeToParcel(dest, flags);
        dest.writeString(mTitle);
        dest.writeTypedList(mActions);
    }

    public static final Creator<CustomGoal> CREATOR = new Creator<CustomGoal>(){
        @Override
        public CustomGoal createFromParcel(Parcel source){
            return new CustomGoal(source);
        }

        @Override
        public CustomGoal[] newArray(int size){
            return new CustomGoal[size];
        }
    };

    /**
     * Constructor to create from parcel.
     *
     * @param src the parcel where the object is stored.
     */
    private CustomGoal(Parcel src){
        super(src);
        mTitle = src.readString();
        //Retrieve the actions as an array list and assign the parent goal
        mActions = new ArrayList<>();
        src.readTypedList(mActions, CustomAction.CREATOR);
    }
}
