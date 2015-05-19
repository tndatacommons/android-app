package org.tndata.android.compass.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Goal extends TDCBase implements Serializable, Comparable<Goal> {

    private static final long serialVersionUID = 7109406671934150671L;
    private String subtitle = "";
    private String outcome = "";
    private String icon_url = "";
    private ArrayList<Category> categories = new ArrayList<Category>();
    private ArrayList<Behavior> behaviors = new ArrayList<Behavior>();

    public Goal() {
    }

    public Goal(int id, int order, String title, String titleSlug,
                String description, String subtitle, String outcome, String iconUrl) {
        super(id, title, titleSlug, description);
        this.subtitle = subtitle;
        this.outcome = outcome;
        this.icon_url = iconUrl;
        this.categories = new ArrayList<Category>();
    }

    public Goal(int id, int order, String title, String titleSlug,
                String description, String subtitle, String outcome,
                String iconUrl, ArrayList<Category> categories) {
        super(id, title, titleSlug, description);
        this.subtitle = subtitle;
        this.outcome = outcome;
        this.icon_url = iconUrl;
        this.categories = categories;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getIconUrl() {
        return icon_url;
    }

    public void setIconUrl(String icon_url) {
        this.icon_url = icon_url;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public ArrayList<Behavior> getBehaviors() {
        return behaviors;
    }

    public void setBehaviors(ArrayList<Behavior> behaviors) {
        this.behaviors = behaviors;
    }


    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null) {
            result = false;
        } else if (object == this) {
            result = true;
        } else if (object instanceof Goal) {
            if (this.getId() == ((Goal) object).getId()) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 7 * hash + this.getTitle().hashCode();
        return hash;
    }

    @Override
    public int compareTo(Goal another) {
        if (getId() == another.getId()) {
            return 0;
        } else if (getId() < another.getId()) {
            return -1;
        } else
            return 1;
    }
}
