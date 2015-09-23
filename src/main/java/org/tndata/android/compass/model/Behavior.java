package org.tndata.android.compass.model;

import android.content.Context;
import android.widget.ImageView;

import org.tndata.android.compass.util.ImageLoader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Behavior extends TDCBase implements Serializable,
        Comparable<Behavior> {

    private static final long serialVersionUID = 7747989797893422842L;
    private String more_info = "";
    private String html_more_info = "";
    private String external_resource = "";
    private String notification_text = "";
    private String icon_url = "";
    private String image_url = "";
    private int actions_count = 0;
    private List<Category> userCategories = new ArrayList<>();
    private List<Goal> goals = new ArrayList<>();
    private List<Action> actions = new ArrayList<>();

    public Behavior() {
    }

    public Behavior(int id, int order, String title, String titleSlug,
                    String description, String html_description, String moreInfo, String htmlMoreInfo,
                    String externalResource, String notificationText, String iconUrl, String imageUrl) {
        super(id, title, titleSlug, description, html_description);
        this.more_info = moreInfo;
        this.html_more_info = htmlMoreInfo;
        this.external_resource = externalResource;
        this.notification_text = notificationText;
        this.icon_url = iconUrl;
        this.image_url = imageUrl;
    }

    public Behavior(int id, int order, String title, String titleSlug,
                    String description, String html_description, String moreInfo, String htmlMoreInfo,
                    String externalResource, String notificationText, String iconUrl, String imageUrl,
                    ArrayList<Goal> goals) {
        super(id, title, titleSlug, description, html_description);
        this.more_info = moreInfo;
        this.html_more_info = htmlMoreInfo;
        this.external_resource = externalResource;
        this.notification_text = notificationText;
        this.icon_url = iconUrl;
        this.image_url = imageUrl;
        this.goals = goals;
    }

    public String getMoreInfo() {
        return more_info;
    }

    public String getHTMLMoreInfo() { return html_more_info; }

    public void setMoreInfo(String more_info) {
        this.more_info = more_info;
    }

    public void setHTMLMoreInfo(String html_more_info) { this.html_more_info = html_more_info; }

    public String getExternalResource() {
        return external_resource;
    }

    public void setExternalResource(String external_resource) {
        this.external_resource = external_resource;
    }

    public String getNotificationText() {
        return notification_text;
    }

    public void setNotificationText(String notification_text) {
        this.notification_text = notification_text;
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

    public void setUserCategories(List<Category> categories){
        userCategories = categories;
    }

    public List<Category> getUserCategories(){
        return userCategories;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals(List<Goal> goals) {
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

    public List<Action> getActions() {
        return actions;
    }

    public int getActionCount(){
        return actions_count;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public void addAction(Action action) {
        if(!this.actions.contains(action)) {
            this.actions.add(action);
        }
    }

    public void removeAction(Action action) {
        if(this.actions.contains(action)) {
            this.actions.remove(action);
        }
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null) {
            result = false;
        } else if (object == this) {
            result = true;
        } else if (object instanceof Behavior) {
            if (this.getId() == ((Behavior) object).getId()) {
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
    public int compareTo(Behavior another) {
        if (getId() == another.getId()) {
            return 0;
        } else if (getId() < another.getId()) {
            return -1;
        } else
            return 1;
    }

    /**
     * Given a Context and an ImageView, load this Behavior's icon into the ImageView.
     *
     * @param context: an application context
     * @param imageView: an ImageView
     */
    public void loadIconIntoView(Context context, ImageView imageView) {
        String iconUrl = getIconUrl();
        if(iconUrl != null && !iconUrl.isEmpty()) {
            ImageLoader.loadBitmap(imageView, iconUrl, new ImageLoader.Options());
        }
    }

    @Override
    public String toString(){
        return "Behavior #" + getId() + " (" + getMappingId() + "): " + getTitle();
    }
}
