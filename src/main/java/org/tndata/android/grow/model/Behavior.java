package org.tndata.android.grow.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Behavior extends TDCBase implements Serializable,
        Comparable<Behavior> {

    private static final long serialVersionUID = 7747989797893422842L;
    private String narrative_block = "";
    private String external_resource = "";
    private String notification_text = "";
    private String icon_url = "";
    private String image_url = "";
    private ArrayList<Goal> goals = new ArrayList<Goal>();

    public Behavior() {
    }

    public Behavior(int id, int order, String title, String titleSlug,
                    String description, String narrativeBlock, String externalResource,
                    String notificationText, String iconUrl, String imageUrl) {
        super(id, title, titleSlug, description);
        this.narrative_block = narrativeBlock;
        this.external_resource = externalResource;
        this.notification_text = notificationText;
        this.icon_url = iconUrl;
        this.image_url = imageUrl;
    }

    public Behavior(int id, int order, String title, String titleSlug,
                    String description, String narrativeBlock, String externalResource,
                    String notificationText, String iconUrl, String imageUrl,
                    ArrayList<Goal> goals) {
        super(id, title, titleSlug, description);
        this.narrative_block = narrativeBlock;
        this.external_resource = externalResource;
        this.notification_text = notificationText;
        this.icon_url = iconUrl;
        this.image_url = imageUrl;
        this.goals = goals;
    }

    public String getNarrativeBlock() {
        return narrative_block;
    }

    public void setNarrativeBlock(String narrative_block) {
        this.narrative_block = narrative_block;
    }

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

}
