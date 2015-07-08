package org.tndata.android.compass.model;

import android.content.Context;
import android.widget.ImageView;

import org.tndata.android.compass.util.ImageCache;

import java.io.Serializable;
import java.util.ArrayList;

public class Behavior extends TDCBase implements Serializable,
        Comparable<Behavior> {

    private static final long serialVersionUID = 7747989797893422842L;
    private String more_info = "";
    private String html_more_info = "";
    private String external_resource = "";
    private String notification_text = "";
    private String icon_url = "";
    private String image_url = "";
    private ArrayList<Goal> goals = new ArrayList<Goal>();
    private ArrayList<Action> actions = new ArrayList<Action>();

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

    public ArrayList<Goal> getGoals() {
        return goals;
    }

    public void setGoals(ArrayList<Goal> goals) {
        this.goals = goals;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    public void setActions(ArrayList<Action> actions) {
        this.actions = actions;
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
            ImageCache.instance(context).loadBitmap(imageView, iconUrl, false);
        }
    }

}
