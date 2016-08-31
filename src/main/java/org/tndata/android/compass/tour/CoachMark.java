package org.tndata.android.compass.tour;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;


/**
 * Created by isma on 8/31/16.
 */
public class CoachMark{
    private View mTarget;
    private Tour.Tooltip mTooltip;
    private int mOverlayColor;
    private CutawayType mCutawayType;
    private int mCutawayRadius;


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

    boolean isValid(){
        return mTooltip != null;
    }

    public enum CutawayType{
        CIRCLE, SQUARE, NONE;
    }
}
