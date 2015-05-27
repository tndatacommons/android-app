package org.tndata.android.compass.model;

import java.io.Serializable;


public class SurveyOptions implements Serializable {
    private static final long serialVersionUID = 7660016179070794886L;
    private String text = "";
    private int id = -1;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        return text;
    }
}
