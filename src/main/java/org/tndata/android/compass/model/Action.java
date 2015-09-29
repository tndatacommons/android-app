package org.tndata.android.compass.model;

import java.io.Serializable;

public class Action extends TDCBase implements Serializable, Comparable<Action> {

    private static final long serialVersionUID = 2919447130236951923L;
    private Goal primary_goal = null;
    private Behavior behavior = null;
    private int behavior_id = -1;
    private int sequence_order = -1;
    private String more_info = "";
    private String html_more_info = "";
    private String external_resource = "";
    private String notification_text = "";
    private String icon_url = "";
    private String image_url = "";
    private Trigger custom_trigger;
    private Trigger default_trigger;

    public Action() {
    }

    public Action(int id, int order, String title, String titleSlug,
                  String description, String html_description, int sequenceOrder, String moreInfo,
                  String htmlMoreInfo, String externalResource, String notificationText, String iconUrl,
                  String imageUrl, int behaviorId) {
        super(id, title, titleSlug, description, html_description);
        this.sequence_order = sequenceOrder;
        this.more_info = moreInfo;
        this.external_resource = externalResource;
        this.notification_text = notificationText;
        this.icon_url = iconUrl;
        this.image_url = imageUrl;
        this.behavior_id = behaviorId;
        this.html_more_info = htmlMoreInfo;
    }

    public Action(int id, int order, String title, String titleSlug,
                  String description, String html_description, Behavior behavior, int sequenceOrder,
                  String moreInfo, String htmlMoreInfo, String externalResource,
                  String notificationText, String iconUrl, String imageUrl, int behaviorId) {
        super(id, title, titleSlug, description, html_description);
        this.behavior = behavior;
        this.sequence_order = sequenceOrder;
        this.more_info = moreInfo;
        this.html_more_info = htmlMoreInfo;
        this.external_resource = externalResource;
        this.notification_text = notificationText;
        this.icon_url = iconUrl;
        this.image_url = imageUrl;
        this.behavior_id = behaviorId;
    }

    public Goal getPrimaryGoal(){
        return primary_goal;
    }

    public void setPrimaryGoal(Goal primary_goal){
        this.primary_goal = primary_goal;
    }

    public Behavior getBehavior() {
        return behavior;
    }

    public void setBehavior(Behavior behavior) {
        this.behavior = behavior;
    }

    public int getSequenceOrder() {
        return sequence_order;
    }

    public void setSequenceOrder(int sequence_order) {
        this.sequence_order = sequence_order;
    }

    public String getMoreInfo() {
        return more_info;
    }

    public void setMoreInfo(String more_info    ) {
        this.more_info = more_info;
    }

    public String getHTMLMoreInfo() { return html_more_info;}

    public void setHTMLMoreInfo(String htmlMoreInfo) { this.html_more_info = htmlMoreInfo; }

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

    public int getBehavior_id() {
        return behavior_id;
    }

    public void setBehavior_id(int behavior_id) {
        this.behavior_id = behavior_id;
    }

    public Trigger getTrigger() {
        // Return the custom trigger if it exists, otherwise return the default trigger.
        if (custom_trigger != null && !custom_trigger.isDisabled()){
            return custom_trigger;
        }
        else if(default_trigger != null){
            return default_trigger;
        }
        else{
            return new Trigger();
        }
    }

    public Trigger getCustomTrigger() {
        return custom_trigger;
    }

    public void setCustomTrigger(Trigger trigger) {
        this.custom_trigger = trigger;
    }

    public Trigger getDefaultTrigger() {
        return default_trigger;
    }

    public void setDefaultTrigger(Trigger trigger) {
        this.default_trigger = trigger;
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null) {
            result = false;
        } else if (object == this) {
            result = true;
        } else if (object instanceof Action) {
            if (this.getId() == ((Action) object).getId()) {
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
    public int compareTo(Action another) {
        if (getId() == another.getId()) {
            return 0;
        } else if (getId() < another.getId()) {
            return -1;
        } else
            return 1;
    }

    @Override
    public String toString(){
        return "Action #" + getId() + " (" + getMappingId() + "): " + getTitle();
    }
}
