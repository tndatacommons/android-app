package org.tndata.android.compass.model;

/**
 * Created by isma on 9/28/15.
 */
public class FeedData{
    private Action mNextAction;
    private int mProgress;


    public void setNextAction(Action nextAction){
        mNextAction = nextAction;
    }

    public Action getNextAction(){
        return mNextAction;
    }

    public void setProgress(int progress){
        mProgress = progress;
    }

    public int getProgress(){
        return mProgress;
    }
}
