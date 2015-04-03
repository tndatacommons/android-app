package org.tndata.android.grow.model;

import java.io.Serializable;
import java.util.List;


public class Survey implements Serializable {

    private static final long serialVersionUID = 3345647844660418003L;
    private int id = -1;
    private int order = 0;
    private String text = "";
    private boolean available = true;
    private String instructions = "";
    private String question_type = "";
    private String response_url = "";
    private List<SurveyOptions> options = null;

    public List<SurveyOptions> getOptions() {
        return options;
    }

    public void setOptions(List<SurveyOptions> options) {
        this.options = options;
    }

    public String getQuestionType() {
        return question_type;
    }

    public void setQuestionType(String questionType) {
        this.question_type = questionType;
    }

    public String getResponseUrl() {
        return response_url;
    }

    public void setResponseUrl(String responseUrl) {
        this.response_url = responseUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

}
