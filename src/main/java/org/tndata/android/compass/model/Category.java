package org.tndata.android.compass.model;

import android.content.Context;
import android.widget.ImageView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.util.ImageLoader;

import java.io.Serializable;
import java.util.ArrayList;

public class Category extends TDCBase implements Serializable,
        Comparable<Category> {

    private static final long serialVersionUID = -1751642109285216370L;
    private int order = -1;
    private String icon_url = "";
    private String image_url = "";
    private ArrayList<Goal> goals = new ArrayList<Goal>();
    private String color = "";
    private String secondary_color = "";
    private int goals_count = 0;
    private double progress_value = 0.0; // Only used for UserCategories

    public Category() {
    }

    public Category(int id, int order, String title, String titleSlug,
                    String description, String html_description, String iconUrl, String imageUrl) {
        super(id, title, titleSlug, description, html_description);
        this.order = order;
        this.icon_url = iconUrl;
        this.image_url = imageUrl;
        this.goals = new ArrayList<Goal>();
    }

    public Category(int id, int order, String title, String titleSlug,
                    String description, String html_description, String iconUrl, String imageUrl, ArrayList<Goal> goals) {
        super(id, title, titleSlug, description, html_description);
        this.order = order;
        this.icon_url = iconUrl;
        this.image_url = imageUrl;
        this.goals = goals;
    }

    public Category(int id, int order, String title, String titleSlug,
                    String description, String html_description, String iconUrl, String imageUrl, String color) {
        super(id, title, titleSlug, description, html_description);
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

    public int getGoalCount(){
        return goals_count;
    }

    public ArrayList<Goal> getGoals() {
        return goals;
    }

    public void setGoals(ArrayList<Goal> goals) {
        this.goals = goals;
    }

    public void addGoal(Goal goal) {
        if(!this.goals.contains(goal)) {
            this.goals.add(goal);
        }
    }

    public void removeGoal(Goal goal) {
        if(this.goals.contains(goal)) {
            this.goals.remove(goal);
        }
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSecondaryColor() {
        return this.secondary_color;
    }

    public void setSecondaryColor(String secondary_color) {
        this.secondary_color = secondary_color;
    }

    public void setProgressValue(double value) {
        this.progress_value = value;
    }

    public double getProgressValue() {
        return this.progress_value;
    }

    public int getProgressIcon() {
        double value = getProgressValue();
        if (value < 0.125) {
            return R.drawable.compass_9_s;
        } else if (value < 0.25) {
            return R.drawable.compass_8_sse;
        } else if (value < 0.375) {
            return R.drawable.compass_7_se;
        } else if (value < 0.5) {
            return R.drawable.compass_6_ees;
        } else if (value < 0.625) {
            return R.drawable.compass_5_e;
        } else if (value < 0.75) {
            return R.drawable.compass_4_nee;
        } else if (value < 0.875) {
            return R.drawable.compass_3_ne;
        } else if (value < 0.95) {
            return R.drawable.compass_2_nne;
        } else {
            return R.drawable.compass_1_n;
        }
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

    /**
     * Given a Context and an ImageView, load this Goal's icon (if the user has selected
     * no Behaviors) or load the goal's Progress Icons.
     *
     * @param context: an application context
     * @param imageView: an ImageView
     */
    public void loadIconIntoView(Context context, ImageView imageView) {
        String iconUrl = getIconUrl();
        if(iconUrl != null && !iconUrl.isEmpty()){
            ImageLoader.loadBitmap(imageView, iconUrl, false);
        }
    }

    public void loadImageIntoView(Context context, ImageView imageView) {
        String url = getImageUrl();
        if(url != null && !url.isEmpty()){
            ImageLoader.loadBitmap(imageView, url, false);
        }
    }
}
