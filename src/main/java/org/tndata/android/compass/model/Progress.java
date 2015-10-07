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
    private float daily_actions_progress = 0;

    private int weekly_actions_total = 0;
    private int weekly_actions_completed = 0;
    private float weekly_actions_progress = 0;

    private int actions_total = 0;
    private int actions_completed = 0;
    private float actions_progress = 0;

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
        return weekly_actions_progress;
    }

    public int getWeeklyActionsTotal(){
        return weekly_actions_total;
    }

    public int getWeeklyActionsCompleted(){
        return weekly_actions_completed;
    }

    public float getActionsProgress(){
        return actions_progress;
    }

    public int getActionsTotal(){
        return actions_total;
    }

    public int getActionsCompleted(){
        return actions_completed;
    }

    public float getDailyActionsProgress(){
        return daily_actions_progress;
    }

    public String getReportedOn(){
        return reported_on;
    }
}
