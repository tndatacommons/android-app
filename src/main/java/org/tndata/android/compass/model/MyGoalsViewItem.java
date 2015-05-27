package org.tndata.android.compass.model;


import org.tndata.android.compass.util.Constants;

import java.io.Serializable;

public class MyGoalsViewItem implements Serializable {
    public static final int TYPE_CATEGORY = 0;
    public static final int TYPE_SURVEY_LIKERT = 1;
    public static final int TYPE_SURVEY_MULTICHOICE = 2;
    public static final int TYPE_SURVEY_BINARY = 3;
    public static final int TYPE_SURVEY_OPENENDED = 4;
    public static final int TYPE_DEFAULT_NO_CONTENT = 5;

    private static final long serialVersionUID = 6477860168863580408L;
    private Category category = null;
    private Survey survey = null;
    private int type = TYPE_DEFAULT_NO_CONTENT;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
        setType(TYPE_CATEGORY);
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
