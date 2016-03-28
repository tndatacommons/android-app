package org.tndata.android.compass.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;


/**
 * Model representation of an upcoming action. Gathers the necessary data to display and
 * fetch an Action and parent content in the feed or elsewhere.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class UpcomingAction implements Parcelable{
    public static final String API_TYPE = "upcoming_item";

    private static final String USER_ACTION_TYPE = "useraction";
    private static final String CUSTOM_ACTION_TYPE = "customaction";


    @SerializedName("action_id")
    private long mId;
    @SerializedName("action")
    private String mTitle;
    @SerializedName("goal_id")
    private long mGoalId;
    @SerializedName("goal")
    private String mGoalTitle;
    @SerializedName("trigger")
    private String mTrigger;
    @SerializedName("type")
    private String mType;


    /**
     * Constructor. Generates an UpcomingAction from a Goal and an Action.
     *
     * @param goal the goal to be extracted information from.
     * @param action the action to be extracted information from.
     */
    public UpcomingAction(Goal goal, Action action){
        mId = action.getId();
        mTitle = action.getTitle();
        mGoalTitle = goal.getTitle();
        mTrigger = action.getNextReminder().replace('T', ' ');
        mGoalId = goal.getId();
        if (action instanceof UserAction){
            mType = USER_ACTION_TYPE;
        }
        else if (action instanceof CustomAction){
            mType = CUSTOM_ACTION_TYPE;
        }
    }

    /**
     * Id getter.
     *
     * @return the id of the upcoming action.
     */
    public long getId(){
        return mId;
    }

    /**
     * Title getter.
     *
     * @return the title of the upcoming action.
     */
    public String getTitle(){
        return mTitle;
    }

    /**
     * Goal id getter.
     *
     * @return the id of the parent/primary goal for the upcoming action.
     */
    public long getGoalId(){
        return mGoalId;
    }

    /**
     * Goal title getter.
     *
     * @return the goal title of the parent/primary goal for the upcoming action.
     */
    public String getGoalTitle(){
        return mGoalTitle;
    }

    /**
     * Trigger date getter.
     *
     * @return a Date object set to the trigger time.
     */
    public Date getTriggerDate(){
        //TODO parse with DateTime?
        String year = mTrigger.substring(0, mTrigger.indexOf("-"));
        String temp = mTrigger.substring(mTrigger.indexOf("-")+1);
        String month = temp.substring(0, temp.indexOf("-"));
        temp = temp.substring(temp.indexOf("-")+1);
        String day = temp.substring(0, temp.indexOf(" "));

        String time = mTrigger.substring(mTrigger.indexOf(' ')+1);
        String hour = time.substring(0, time.indexOf(':'));
        time = time.substring(time.indexOf(':')+1);
        String minute = time.substring(0, time.indexOf(':'));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.valueOf(year));
        calendar.set(Calendar.MONTH, Integer.valueOf(month)-1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(day));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour));
        calendar.set(Calendar.MINUTE, Integer.valueOf(minute));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * Trigger date display getter.
     *
     * @return a string with the display format of the trigger.
     */
    public String getTriggerDisplay(){
        String time = mTrigger.substring(mTrigger.indexOf(' ')+1, mTrigger.lastIndexOf('-'));
        String hourStr = time.substring(0, time.indexOf(':'));
        time = time.substring(time.indexOf(':')+1);
        try{
            boolean am = true;
            int hour = Integer.valueOf(hourStr);
            if (hour > 12){
                hour -= 12;
                am = false;
            }

            return hour + ":" + time.substring(0, time.indexOf(":")) + (am ? " am" : " pm");
        }
        catch (NumberFormatException nfx){
            nfx.printStackTrace();
            return "";
        }
    }

    /**
     * Tells whether this UpcomingAction represents a UserAction.
     *
     * @return true if it does, false otherwise.
     */
    public boolean isUserAction(){
        return mType.equals(USER_ACTION_TYPE);
    }

    /**
     * Tells whether this UpcomingAction represents a CustomAction.
     *
     * @return true if it does, false otherwise.
     */
    public boolean isCustomAction(){
        return mType.equals(CUSTOM_ACTION_TYPE);
    }

    @Override
    public String toString(){
        String result = "";
        if (isUserAction()){
            result = "UserAction ";
        }
        else if (isCustomAction()){
            result = "CustomAction ";
        }
        result += "#" + mId + ": " + mTitle;
        return result;
    }

    @Override
    public boolean equals(Object o){
        if (o == null || !(o instanceof UpcomingAction)){
            return false;
        }
        UpcomingAction action = (UpcomingAction)o;
        return mType.equals(action.mType) && mId == action.mId;
    }

    //TODO is the editor going to nag me if I combine this with equals()?
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean is(Action action){
        if (isUserAction() && action instanceof UserAction){
            return mId == action.getId();
        }
        if (isCustomAction() && action instanceof CustomAction){
            return mId == action.getId();
        }
        return false;
    }

    /**
     * Updates this UpcomingAction if it is equal to the provided acton.
     *
     * @param action the action where the info should be extracted.
     */
    public void update(Action action){
        if (is(action)){
            mTitle = action.getTitle();
            mTrigger = action.getNextReminder().replace('T', ' ');
        }
    }

    /**
     * Updates a the goal contained by this action if it is it's parent goal.
     *
     * @param customGoal the goal where the info should be extracted.
     */
    public void update(CustomGoal customGoal){
        if (isCustomAction() && mGoalId == customGoal.getId()){
            mGoalTitle = customGoal.getTitle();
        }
    }


    /*------------------*
     * Parcelable STUFF *
     *------------------*/

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeLong(mId);
        dest.writeString(mTitle);
        dest.writeLong(mGoalId);
        dest.writeString(mGoalTitle);
        dest.writeString(mTrigger);
        dest.writeString(mType);
    }

    public static final Parcelable.Creator<UpcomingAction> CREATOR = new Parcelable.Creator<UpcomingAction>(){
        @Override
        public UpcomingAction createFromParcel(Parcel in){
            return new UpcomingAction(in);
        }

        @Override
        public UpcomingAction[] newArray(int size){
            return new UpcomingAction[size];
        }
    };

    /**
     * Constructor to create from parcel.
     *
     * @param in the parcel where the object is stored.
     */
    private UpcomingAction(Parcel in){
        mId = in.readLong();
        mTitle = in.readString();
        mGoalId = in.readLong();
        mGoalTitle = in.readString();
        mTrigger = in.readString();
        mType = in.readString();
    }
}
