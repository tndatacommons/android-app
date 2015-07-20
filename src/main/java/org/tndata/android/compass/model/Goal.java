package org.tndata.android.compass.model;

import android.content.Context;
import android.widget.ImageView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.util.ImageLoader;

import java.io.Serializable;
import java.util.ArrayList;

public class Goal extends TDCBase implements Serializable, Comparable<Goal> {

    private static final long serialVersionUID = 7109406671934150671L;
    private String subtitle = "";
    private String outcome = "";
    private String icon_url = "";
    private ArrayList<Category> categories = new ArrayList<Category>();
    private ArrayList<Behavior> behaviors = new ArrayList<Behavior>();
    private double progress_value = 0.0; // Only used for UserGoals

    public Goal() {
    }

    public Goal(int id, int order, String title, String titleSlug,
                String description, String html_description, String subtitle, String outcome, String iconUrl) {
        super(id, title, titleSlug, description, html_description);
        this.subtitle = subtitle;
        this.outcome = outcome;
        this.icon_url = iconUrl;
        this.categories = new ArrayList<Category>();
    }

    public Goal(int id, int order, String title, String titleSlug,
                String description, String html_description, String subtitle, String outcome,
                String iconUrl, ArrayList<Category> categories) {
        super(id, title, titleSlug, description, html_description);
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

    public void removeCategory(Category category) {
        if(this.categories.contains(category)) {
            this.categories.remove(category);
        }
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public void addCategory(Category category) {
        if(!this.categories.contains(category)) {
            this.categories.add(category);
        }
    }

    public ArrayList<Behavior> getBehaviors() {
        return behaviors;
    }

    public void setBehaviors(ArrayList<Behavior> behaviors) {
        this.behaviors = behaviors;
    }

    public void addBehavior(Behavior behavior) {
        if(!this.behaviors.contains(behavior)) {
            this.behaviors.add(behavior);
        }
    }

    public void removeBehavior(Behavior behavior) {
        if(this.behaviors.contains(behavior)) {
            this.behaviors.remove(behavior);
        }
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

    /**
     * Given a Context and an ImageView, load this Goal's icon (if the user has selected
     * no Behaviors) or load the goal's Progress Icons.
     *
     * @param context: an application context
     * @param imageView: an ImageView
     */
    public void loadIconIntoView(Context context, ImageView imageView) {
        String iconUrl = getIconUrl();
        if(iconUrl != null && !iconUrl.isEmpty()) {
            ImageLoader.loadBitmap(imageView, iconUrl, false);
        }
        /*
        // TODO: only show goal icons (above) until we figure out what to do here.
        if(getBehaviors().isEmpty()) {
            if(iconUrl != null && !iconUrl.isEmpty()) {
                ImageCache.instance(context).loadBitmap(imageView, iconUrl, false);
            }
        } else {
            imageView.setImageResource(getProgressIcon());
        }
        */
    }
}
