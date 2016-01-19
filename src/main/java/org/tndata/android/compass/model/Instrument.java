package org.tndata.android.compass.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Model class for an instrument.
 *
 * @author Edited by Ismael Alonso
 * @version 1.0.0
 */
public class Instrument implements Serializable{
    private static final long serialVersionUID = 3492049583975743778L;

    private int id = -1;
    private String title = "";
    private String description = "";
    private String instructions = "";
    private List<Survey> questions = new ArrayList<>();


    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getInstructions() {
        return instructions;
    }

    public List<Survey> getQuestions() {
        return questions;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setQuestions(List<Survey> questions) {
        this.questions = questions;
    }
}
