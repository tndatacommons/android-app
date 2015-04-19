package org.tndata.android.grow.model;


import org.tndata.android.grow.util.Constants;

import java.io.Serializable;

public class MyGoalsViewItem implements Serializable {
    public static final int TYPE_GOAL = 0;
    public static final int TYPE_SURVEY_LIKERT = 1;
    public static final int TYPE_SURVEY_MULTICHOICE = 2;
    public static final int TYPE_SURVEY_BINARY = 3;
    public static final int TYPE_SURVEY_OPENENDED = 4;

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
        if (survey.getQuestionType().equalsIgnoreCase(Constants.SURVEY_LIKERT))
            setType(TYPE_SURVEY_LIKERT);
        else if (survey.getQuestionType().equalsIgnoreCase(Constants.SURVEY_MULTICHOICE))
            setType(TYPE_SURVEY_MULTICHOICE);
        else if (survey.getQuestionType().equalsIgnoreCase(Constants.SURVEY_BINARY))
            setType(TYPE_SURVEY_BINARY);
        else if (survey.getQuestionType().equalsIgnoreCase(Constants.SURVEY_OPENENDED))
            setType(TYPE_SURVEY_OPENENDED);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
