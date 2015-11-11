package org.tndata.android.compass.model;

import java.io.Serializable;


/**
 * Model class for reward content.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class Reward implements Serializable{
    static final long serialVersionUID = 96156188439L;

    private static final String QUOTE = "quote";
    private static final String FORTUNE = "fortune";
    private static final String FACT = "fact";
    private static final String JOKE = "joke";


    private int id;
    private String message_type;
    private String message;
    private String author;


    public Reward(int id, String message_type, String message, String author){
        this.id = id;
        this.message_type = message_type;
        this.message = message;
        this.author = author;
    }

    public int getId(){
        return id;
    }

    public String getMessage(){
        return message;
    }

    public String getAuthor(){
        return author;
    }

    public boolean isQuote(){
        return message_type.equals(QUOTE);
    }

    public boolean isFortune(){
        return message_type.equals(FORTUNE);
    }

    public boolean isFunFact(){
        return message_type.equals(FACT);
    }

    public boolean isJoke(){
        return message_type.equals(JOKE);
    }
}
