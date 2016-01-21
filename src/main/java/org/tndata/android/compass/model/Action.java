package org.tndata.android.compass.model;

import java.io.Serializable;


/**
 * Model class for actions.
 *
 * @author Edited by Ismael Alonso
 * @version 1.0.0
 */
public class Action extends TDCBase implements Serializable{
    private static final long serialVersionUID = 2919447130236951923L;

    private int sequence_order = -1;
    private String more_info = "";
    private String html_more_info = "";
    private String external_resource = "";
    private String external_resource_name = "";
    private Trigger trigger;

    private int behavior;


    /*---------*
     * GETTERS *
     *---------*/

    public int getSequenceOrder(){
        return sequence_order;
    }

    public String getMoreInfo(){
        return more_info;
    }

    public String getHTMLMoreInfo(){
        return html_more_info;
    }

    public String getExternalResource(){
        return external_resource;
    }

    public String getExternalResourceName(){
        return external_resource_name;
    }

    public Trigger getTrigger(){
        return trigger != null ? trigger : new Trigger();
    }

    public int getBehavior(){
        return behavior;
    }


    /*---------*
     * UTILITY *
     *---------*/

    @Override
    public String toString(){
        return "Action #" + getId() + ": " + getTitle();
    }
}
