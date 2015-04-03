package org.tndata.android.grow.model;


import java.io.Serializable;

public class MyGoalsViewItem implements Serializable {
    public static final int TYPE_GOAL = 0;
    public static final int TYPE_SURVEY = 1;

    private static final long serialVersionUID = 6477860168863580408L;
    private Goal goal = null;
    private Survey survey = null;
    private int type = -1;

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
        setType(TYPE_GOAL);
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
        setType(TYPE_SURVEY);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
