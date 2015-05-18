package org.tndata.android.compass.model;

import java.io.Serializable;

public class Action extends TDCBase implements Serializable, Comparable<Action> {

    private static final long serialVersionUID = 2919447130236951923L;
    private Behavior behavior = null;
    private int behavior_id = -1;
    private int sequence_order = -1;
    private String more_info = "";
    private String external_resource = "";
    private String notification_text = "";
    private String icon_url = "";
    private String image_url = "";

    public Action() {
    }

    public Action(int id, int order, String title, String titleSlug,
                  String description, int sequenceOrder, String moreInfo,
                  String externalResource, String notificationText, String iconUrl,
                  String imageUrl, int behaviorId) {
        super(id, title, titleSlug, description);
        this.sequence_order = sequenceOrder;
        this.more_info = moreInfo;
        this.external_resource = externalResource;
        this.notification_text = notificationText;
        this.icon_url = iconUrl;
        this.image_url = imageUrl;
        this.behavior_id = behaviorId;
    }

    public Action(int id, int order, String title, String titleSlug,
                  String description, Behavior behavior, int sequenceOrder,
                  String moreInfo, String externalResource,
                  String notificationText, String iconUrl, String imageUrl, int behaviorId) {
        super(id, title, titleSlug, description);
        this.behavior = behavior;
        this.sequence_order = sequenceOrder;
        this.more_info = moreInfo;
        this.external_resource = externalResource;
        this.notification_text = notificationText;
        this.icon_url = iconUrl;
        this.image_url = imageUrl;
        this.behavior_id = behaviorId;
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
}
