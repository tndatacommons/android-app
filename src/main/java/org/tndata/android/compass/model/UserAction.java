package org.tndata.android.compass.model;

import java.io.Serializable;


/**
 * Model class for user actions.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class UserAction extends UserContent implements Serializable{
    private static final long serialVersionUID = 291944745632851923L;

    //Values retrieved from the API
    private Action action;
    private String next_reminder_date;

    private int primary_goal;
    private int primary_category;

    private Trigger trigger;

    //Values set during post-processing
    private UserBehavior behavior;
    private Goal primaryGoal;
    private Category primaryCategory;


    public UserAction(Action action, Goal primaryGoal, Category primaryCategory){
        this.action = action;
        this.primaryGoal = primaryGoal;
        this.primaryCategory = primaryCategory;
    }


    /*---------*
     * SETTERS *
     *---------*/

    public void setBehavior(UserBehavior behavior){
        this.behavior = behavior;
    }

    public void setTrigger(Trigger trigger){
        this.trigger = trigger;
    }

    public void setNextReminderDate(String nextReminderDate){
        next_reminder_date = nextReminderDate;
    }


    /*---------*
     * GETTERS *
     *---------*/

    public Action getAction(){
        return action;
    }

    @Override
    public int getObjectId(){
        return action.getId();
    }

    @Override
    public String getTitle(){
        return action.getTitle();
    }

    @Override
    public String getDescription(){
        return action.getDescription();
    }

    @Override
    public String getHTMLDescription(){
        return action.getHTMLDescription();
    }

    @Override
    public String getIconUrl(){
        return action.getIconUrl();
    }

    public String getRawNextReminderDate(){
        return next_reminder_date;
    }

    public String getNextReminderDate(){
        if (next_reminder_date == null){
            return "";
        }

        String time = next_reminder_date.substring(next_reminder_date.indexOf('T')+1);
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

    public UserBehavior getBehavior(){
        return behavior;
    }

    public Category getPrimaryCategory(){
        return primaryCategory;
    }

    public Goal getPrimaryGoal(){
        return primaryGoal;
    }

    public Trigger getTrigger(){
        return trigger;
    }

    public boolean hasTrigger(){
        return trigger != null;
    }


    /*---------*
     * UTILITY *
     *---------*/

    @Override
    public String toString(){
        return "UserAction #" + getId() + " (" + action.toString() + ")";
    }
}
