package org.tndata.android.compass.model;

import android.widget.ImageView;

import org.tndata.android.compass.util.ImageLoader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Model class for behaviors.
 *
 * @author Edited by Ismael Alonso
 * @version 1.0.0
 */
public class Behavior extends TDCBase implements Serializable{
    private static final long serialVersionUID = 7747989797893422842L;

    private String more_info = "";
    private String html_more_info = "";

    private List<Goal> goals = new ArrayList<>();
    private int actions_count = 0;


    /*---------*
     * SETTERS *
     *---------*/

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }


    /*---------*
     * GETTERS *
     *---------*/

    public String getMoreInfo(){
        return more_info;
    }

    public String getHTMLMoreInfo(){
        return html_more_info;
    }

    public List<Goal> getGoals(){
        return goals;
    }

    public int getActionCount(){
        return actions_count;
    }


    /*---------*
     * UTILITY *
     *---------*/

    /**
     * Given a Context and an ImageView, load this Behavior's icon into the ImageView.
     *
     * @param imageView: an ImageView
     */
    public void loadIconIntoView(ImageView imageView) {
        String iconUrl = getIconUrl();
        if(iconUrl != null && !iconUrl.isEmpty()) {
            ImageLoader.loadBitmap(imageView, iconUrl, new ImageLoader.Options());
        }
    }

    @Override
    public String toString(){
        return "Behavior #" + getId() + ": " + getTitle();
    }
}
