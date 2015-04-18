package org.tndata.android.grow.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Category extends TDCBase implements Serializable,
        Comparable<Category> {

    private static final long serialVersionUID = -1751642109285216370L;
    private int order = -1;
    private String icon_url = "";
    private String image_url = "";
    private ArrayList<Goal> goals = new ArrayList<Goal>();
    private int color = 0;

    public Category() {
    }

    public Category(int id, int order, String title, String titleSlug,
                    String description, String iconUrl, String imageUrl) {
        super(id, title, titleSlug, description);
        this.order = order;
        this.icon_url = iconUrl;
        this.image_url = imageUrl;
        this.goals = new ArrayList<Goal>();
    }

    public Category(int id, int order, String title, String titleSlug,
                    String description, String iconUrl, String imageUrl, ArrayList<Goal> goals) {
        super(id, title, titleSlug, description);
        this.order = order;
        this.icon_url = iconUrl;
        this.image_url = imageUrl;
        this.goals = goals;
    }

    public Category(int id, int order, String title, String titleSlug,
                    String description, String iconUrl, String imageUrl, int color) {
        super(id, title, titleSlug, description);
        this.order = order;
        this.icon_url = iconUrl;
        this.image_url = imageUrl;
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

    public String getImageUrl() {
        return image_url;
    }

    public void setImageUrl(String image_url) {
        this.image_url = image_url;
    }

    public ArrayList<Goal> getGoals() {
        return goals;
    }

    public void setGoals(ArrayList<Goal> goals) {
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
