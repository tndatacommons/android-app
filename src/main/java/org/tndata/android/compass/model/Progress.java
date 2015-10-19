package org.tndata.android.compass.model;

import java.io.Serializable;


/**
 * Model class for progress reports.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class Progress implements Serializable{
    private static final long serialVersionUID = 7523638276434150671L;


    private String object_type = "";

    private float current_score = 0;
    private float current_total = 0;
    private float max_total = 0;

    private int daily_actions_total = 0;
    private int daily_actions_completed = 0;
    private float daily_action_progress = 0;
    private int daily_action_progress_percent = 0;

    private int weekly_actions_total = 0;
    private int weekly_actions_completed = 0;
    private float weekly_action_progress = 0;
    private int weekly_action_progress_percent = 0;

    private int actions_total = 0;
    private int actions_completed = 0;
    private float action_progress = 0;
    private int action_progress_percent = 0;

    private String reported_on = "";


    public String getObjectType(){
        return object_type;
    }

    public float getCurrentScore(){
        return current_score;
    }

    public float getCurrentTotal(){
        return current_total;
    }

    public float getMaxTotal(){
        return max_total;
    }

    public int getDailyActionsTotal(){
        return daily_actions_total;
    }

    public int getDailyActionsCompleted(){
        return daily_actions_completed;
    }

    public float getWeeklyActionsProgress(){
        return weekly_action_progress;
    }

    public int getWeeklyActionsProgressPercent(){
        return weekly_action_progress_percent;
    }

    public int getWeeklyActionsTotal(){
        return weekly_actions_total;
    }

    public int getWeeklyActionsCompleted(){
        return weekly_actions_completed;
    }

    public float getActionsProgress(){
        return action_progress;
    }

    public int getActionsProgressPercent(){
        return action_progress_percent;
    }

    public int getActionsTotal(){
        return actions_total;
    }

    public int getActionsCompleted(){
        return actions_completed;
    }

    public float getDailyActionsProgress(){
        return daily_action_progress;
    }

    public int getDailyActionsProgressPercent(){
        return daily_action_progress_percent;
    }

    public String getReportedOn(){
        return reported_on;
    }
}
