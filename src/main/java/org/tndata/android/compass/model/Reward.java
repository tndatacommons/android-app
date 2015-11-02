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

    private int id;
    private String message_type;
    private String message;
    private String author;


    public int getId(){
        return id;
    }

    public String getMessageType(){
        return message_type;
    }

    public String getMessage(){
        return message;
    }

    public String getAuthor(){
        return author;
    }
}
