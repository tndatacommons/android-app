package org.tndata.android.grow.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Category extends TDCBase implements Serializable,
        Comparable<Category> {

    private static final long serialVersionUID = -1751642109285216370L;
    private int order = -1;
    private String icon_url = "";
    private List<Goal> goals = null;
    private int color = 0;

    public Category() {
    }

    public Category(int id, int order, String title, String titleSlug,
            String description, String iconUrl) {
        super(id, title, titleSlug, description);
        this.order = order;
        this.icon_url = iconUrl;
        this.goals = new ArrayList<Goal>();
    }
    
    public Category(int id, int order, String title, String titleSlug,
            String description, String iconUrl, List<Goal> goals) {
        super(id, title, titleSlug, description);
        this.order = order;
        this.icon_url = iconUrl;
        this.goals = goals;
    }

    public Category(int id, int order, String title, String titleSlug,
            String description, String iconUrl, int color) {
        super(id, title, titleSlug, description);
        this.order = order;
        this.icon_url = iconUrl;
        this.goals = new ArrayList<Goal>();
        this.color = color;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getIconUrl() {
        return icon_url;
    }

    public void setIconUrl(String icon_url) {
        this.icon_url = icon_url;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null) {
            result = false;
        } else if (object == this) {
            result = true;
        } else if (object instanceof Category) {
            if (this.getId() == ((Category) object).getId()) {
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
    public int compareTo(Category another) {
        if (getId() == another.getId()) {
            return 0;
        } else if (getId() < another.getId()) {
            return -1;
        } else
            return 1;
    }

}
