package org.tndata.android.grow.model;


import java.io.Serializable;
import java.util.ArrayList;

public class Instrument implements Serializable {
    private static final long serialVersionUID = 3492049583975743778L;
    private int id = -1;
    private String title = "";
    private String description = "";
    private String instructions = "";
    private ArrayList<Survey> questions = new ArrayList<Survey>();

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

    public ArrayList<Survey> getQuestions() {
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

    public void setQuestions(ArrayList<Survey> questions) {
        this.questions = questions;
    }
}
