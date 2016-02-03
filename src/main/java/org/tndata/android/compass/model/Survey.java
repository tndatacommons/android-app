package org.tndata.android.compass.model;

import org.tndata.android.compass.util.Constants;

import java.io.Serializable;
import java.util.List;


public class Survey extends TDCBase implements Serializable, Comparable<Survey>{
    private static final long serialVersionUID = 3345647844660418003L;

    public static final String TYPE = "survey";


    private int order = 0;
    private String text = "";
    private boolean available = true;
    private String instructions = "";
    private String question_type = "";
    private String response_url = "";
    private String input_type = "";
    private String response = "";
    private List<SurveyOptions> options = null;
    private SurveyOptions selectedOption = null;

    public SurveyOptions getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(SurveyOptions selectedOption) {
        this.selectedOption = selectedOption;
    }

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

    public String getInputType() {
        return input_type;
    }

    public void setInputType(String inputType) {
        this.input_type = inputType;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    protected String getType(){
        return TYPE;
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

    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null) {
            result = false;
        } else if (object == this) {
            result = true;
        } else if (object instanceof Survey) {
            if (this.getId() == ((Survey) object).getId()) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 7 * hash + this.getText().hashCode();
        return hash;
    }

    @Override
    public int compareTo(Survey another) {
        if (getId() == another.getId()) {
            return 0;
        } else if (getId() < another.getId()) {
            return -1;
        } else
            return 1;
    }

    @Override
    public String toString(){
        String result = getId() + " (";

        switch (question_type){
            case Constants.SURVEY_BINARY:
                result += "binary";
                break;
            case Constants.SURVEY_MULTICHOICE:
                result += "multiple choice";
                break;
            case Constants.SURVEY_LIKERT:
                result += "likert";
                break;
            case Constants.SURVEY_OPENENDED:
                result += "open ended";
                break;
            default:
                result += question_type;
        }

        return result + "): " + text;
    }
}
