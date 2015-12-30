package org.tndata.android.compass.model;

import java.io.Serializable;


/**
 * Model class for user actions.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class UserAction extends TDCBase implements Serializable{
    private static final long serialVersionUID = 291944745632851923L;

    private Action action;
    private String next_reminder_date;

    private Goal primary_goal = null;
    private Category primary_category = null;

    private Trigger trigger;


    public UserAction(Action action, Goal primaryGoal, Category primaryCategory){
        this.action = action;
        this.primary_goal = primaryGoal;
        this.primary_category = primaryCategory;
    }


    /*---------*
     * SETTERS *
     *---------*/

    public void setTrigger(Trigger trigger){
        this.trigger = trigger;
    }


    /*---------*
     * GETTERS *
     *---------*/

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

    public Category getPrimaryCategory(){
        return primary_category;
    }

    public Goal getPrimaryGoal(){
        return primary_goal;
    }

    public Trigger getTrigger(){
        return trigger;
    }


    /*---------*
     * UTILITY *
     *---------*/

    @Override
    public String toString(){
        return "UserAction #" + getId() + " (" + action.toString() + ")";
    }
}
