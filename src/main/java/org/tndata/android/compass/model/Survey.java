package org.tndata.android.compass.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;


public class Survey extends TDCBase implements Serializable, Comparable<Survey>{
    private static final long serialVersionUID = 3345647844660418003L;


    public static final String LIKERT = "likertquestion";
    public static final String MULTIPLE_CHOICE = "multiplechoicequestion";
    public static final String BINARY = "binaryquestion";
    public static final String OPEN_ENDED = "openendedquestion";

    public static final String OPEN_ENDED_NUMBER_TYPE = "number";
    public static final String OPEN_ENDED_DATE_TYPE = "datetime";

    public static final String TYPE = "survey";


    private int order = 0;
    private String text = "";
    private boolean available = true;
    private String instructions = "";
    private String question_type = "";
    private String response_url = "";
    private String input_type = "";
    private String response = "";
    private List<SurveyOption> options = null;
    private SurveyOption selectedOption = null;

    public SurveyOption getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(long selectedOptionId){
        if (options != null){
            for (SurveyOption option:options){
                if (option.getId() == selectedOptionId){
                    selectedOption = option;
                    break;
                }
            }
        }
    }

    public void setSelectedOption(SurveyOption selectedOption){
        this.selectedOption = selectedOption;
    }

    public List<SurveyOption> getOptions() {
        return options;
    }

    public void setOptions(List<SurveyOption> options) {
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
    public int compareTo(@NonNull Survey another) {
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
            case BINARY:
                result += "binary";
                break;
            case MULTIPLE_CHOICE:
                result += "multiple choice";
                break;
            case LIKERT:
                result += "likert";
                break;
            case OPEN_ENDED:
                result += "open ended";
                break;
            default:
                result += question_type;
        }

        return result + "): " + text;
    }
}
