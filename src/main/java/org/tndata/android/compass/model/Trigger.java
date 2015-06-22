package org.tndata.android.compass.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Trigger implements Serializable, Comparable<Trigger> {

    private static final long serialVersionUID = 7914473023695112323L;
    private int id = -1;
    private String recurrences_display = "";
    private String recurrences = ""; // utf RFC2445 string
    private String time = "";
    private String name = "";
    private String name_slug = "";
    private String location = "";

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRecurrencesDisplay() {
        return recurrences_display;
    }

    public void setRecurrencesDisplay(String recurrences_display) {
        this.recurrences_display = recurrences_display;
    }

    public String getRecurrences() {
        return recurrences;
    }

    public void setRecurrences(String recurrences) {
        this.recurrences = recurrences;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameSlug() {
        return name_slug;
    }

    public void setNameSlug(String name_slug) {
        this.name_slug = name_slug;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null) {
            result = false;
        } else if (object == this) {
            result = true;
        } else if (object instanceof Trigger) {
            if (this.getId() == ((Trigger) object).getId()) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 7 * hash + this.getName().hashCode();
        return hash;
    }

    @Override
    public int compareTo(Trigger another) {
        if (getId() == another.getId()) {
            return 0;
        } else if (getId() < another.getId()) {
            return -1;
        } else {
            return 1;
        }
    }

    public Date getParsedTime() {
        Date date = new Date();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("H:m", Locale.getDefault());
            if(!time.isEmpty()) {
                date = sdf.parse(time.substring(0, 5));
            }
        }
        catch (ParseException e) {
            return date;
        }
        return date;
    }
}
