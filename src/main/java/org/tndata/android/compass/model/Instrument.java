package org.tndata.android.compass.model;


import org.tndata.android.compass.parser.ParserModels;

import java.util.List;


/**
 * Model class for an instrument.
 *
 * @author Edited by Ismael Alonso
 * @version 1.0.0
 */
public class Instrument extends TDCBase implements ParserModels.ResultSet{
    public static final String TYPE = "instrument";


    private String title;
    private String description;
    private String instructions;
    private List<Survey> questions;


    @Override
    protected String getType(){
        return null;
    }

    public String getTitle(){
        return this.title;
    }

    public String getDescription(){
        return this.description;
    }

    public String getInstructions(){
        return instructions;
    }

    public List<Survey> getQuestions(){
        return questions;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setInstructions(String instructions){
        this.instructions = instructions;
    }

    public void setQuestions(List<Survey> questions){
        this.questions = questions;
    }
}
