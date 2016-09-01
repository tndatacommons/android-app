package org.tndata.android.compass.tour;


import android.app.Activity;
import android.view.View;


/**
 * Created by isma on 8/31/16.
 */
public class CoachMark{
    private View mTarget;
    private Tour.Tooltip mTooltip;
    private int mOverlayColor;
    private CutawayType mCutawayType;
    private int mCutawayRadius;
    private Activity mHost;


    public CoachMark(){
        mCutawayRadius = -1;
    }

    public CoachMark setTarget(View target){
        mTarget = target;
        return this;
    }

    public CoachMark setTooltip(Tour.Tooltip tooltip){
        mTooltip = tooltip;
        return this;
    }

    public CoachMark setOverlayColor(int overlayColor){
        mOverlayColor = overlayColor;
        return this;
    }

    public CoachMark setCutawayType(CutawayType cutawayType){
        mCutawayType = cutawayType;
        return this;
    }

    public CoachMark setCutawayRadius(int radius){
        mCutawayRadius = radius;
        return this;
    }

    CoachMark setHost(Activity host){
        mHost = host;
        return this;
    }

    public View getTarget(){
        return mTarget;
    }

    public Tour.Tooltip getTooltip(){
        return mTooltip;
    }

    public int getOverlayColor(){
        return mOverlayColor;
    }

    public CutawayType getCutawayType(){
        return mCutawayType;
    }

    public int getCutawayRadius(){
        return mCutawayRadius;
    }

    public Activity getHost(){
        return mHost;
    }

    boolean isValid(){
        return mTooltip != null;
    }

    public enum CutawayType{
        CIRCLE, SQUARE, NONE;
    }
}
