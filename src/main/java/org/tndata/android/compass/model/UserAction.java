package org.tndata.android.compass.model;

import org.tndata.android.compass.parser.ParserModels;

import java.io.Serializable;


/**
 * Model class for user actions.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class UserAction extends UserContent implements Serializable, ParserModels.ResultSet{
    private static final long serialVersionUID = 291944745632851923L;

    //Values retrieved from the API
    private ActionContent action;
    private String next_reminder_date;

    private int primary_goal;
    private int primary_category;

    private Trigger trigger;

    private UserBehavior parent_userbehavior;
    private UserGoal parent_usergoal;
    private UserCategory parent_usercategory;

    //Values set during post-processing
    private UserBehavior behavior;
    private UserGoal primaryGoal;
    private UserCategory primaryCategory;


    public UserAction(ActionContent action, UserGoal primaryGoal, UserCategory primaryCategory){
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

    public void setPrimaryCategory(UserCategory primaryCategory){
        this.primaryCategory = primaryCategory;
    }

    public void setPrimaryGoal(UserGoal primaryGoal){
        this.primaryGoal = primaryGoal;
    }


    /*---------*
     * GETTERS *
     *---------*/

    public ActionContent getAction(){
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

    public int getPrimaryCategoryId(){
        return primary_category;
    }

    public int getPrimaryGoalId(){
        return primary_goal;
    }

    public UserCategory getPrimaryCategory(){
        return primaryCategory;
    }

    public UserGoal getPrimaryGoal(){
        return primaryGoal;
    }

    public Trigger getTrigger(){
        return trigger;
    }

    public boolean hasTrigger(){
        return trigger != null;
    }

    public UserBehavior getParentUserBehavior(){
        return parent_userbehavior;
    }

    public UserGoal getParentUserGoal(){
        return parent_usergoal;
    }

    public UserCategory getParentUserCategory(){
        return parent_usercategory;
    }


    /*---------*
     * UTILITY *
     *---------*/

    @Override
    /* no-op */
    public void init(){
        //This method is not necessary here
    }

    @Override
    public String toString(){
        return "UserAction #" + getId() + " (" + action.toString() + ")";
    }
}
