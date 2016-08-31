package org.tndata.android.compass.tour;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;


/**
 * Created by isma on 8/31/16.
 */
class CoachMarkView extends FrameLayout implements View.OnClickListener{
    private CoachMark mCoachMark;
    private ViewGroup mCurrentTooltip;
    private Tour.TourListener mListener;

    private Bitmap mCutawayBitmap;
    private Canvas mCutawayCanvas;
    private Paint mCutawayPaint;


    public CoachMarkView(Context context, Tour.TourListener listener){
        super(context);

        setWillNotDraw(false);

        int x = context.getResources().getDisplayMetrics().widthPixels;
        int y = context.getResources().getDisplayMetrics().heightPixels;

        mCoachMark = null;
        mCurrentTooltip = null;
        mListener = listener;

        mCutawayBitmap = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
        mCutawayCanvas = new Canvas(mCutawayBitmap);
        mCutawayPaint = new Paint();
        mCutawayPaint.setColor(Color.WHITE);
        mCutawayPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCutawayPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view){

            }
        });
    }

    void setCoachMark(CoachMark coachMark){
        if (mCurrentTooltip != null){
            removeView(mCurrentTooltip);
        }
        mCoachMark = coachMark;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mCurrentTooltip = (ViewGroup)inflater.inflate(R.layout.card_tooltip, this, false);
        ((TextView)mCurrentTooltip.findViewById(R.id.tooltip_title)).setText(coachMark.getTooltip().getTitle());
        ((TextView)mCurrentTooltip.findViewById(R.id.tooltip_description)).setText(coachMark.getTooltip().getDescription());

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (mCoachMark.getTarget() == null){
            params.gravity = Gravity.CENTER;
        }
        else{
            params.gravity = Gravity.CENTER_HORIZONTAL;
            View target = mCoachMark.getTarget();
            float y = target.getY();
            float hgt = getContext().getResources().getDisplayMetrics().heightPixels;
            if (y < hgt/2){
                params.topMargin = (int)(y+200);
            }
            else{
                params.topMargin = (int)(y-200);
            }
        }
        addView(mCurrentTooltip, params);

        mCurrentTooltip.setOnClickListener(this);
    }

    void clear(){

    }

    @Override
    protected void onDraw(Canvas canvas){
        Log.d("CoachMarkView", "onDraw()");
        super.onDraw(canvas);
        if (mCoachMark != null){
            mCutawayBitmap.eraseColor(Color.TRANSPARENT);
            mCutawayCanvas.drawColor(mCoachMark.getOverlayColor());
            if (mCoachMark.getCutawayType() != CoachMark.CutawayType.NONE){
                if (mCoachMark.getCutawayType() == CoachMark.CutawayType.CIRCLE){
                    View target = mCoachMark.getTarget();
                    float x = target.getX() + target.getWidth()/2;
                    float y = target.getY() + target.getHeight()/2;
                    float radius = mCoachMark.getCutawayRadius();
                    if (radius == -1){
                        float wid = target.getWidth();
                        float hgt = target.getHeight();
                        if (wid > hgt){
                            radius = wid;
                        }
                        else{
                            radius = hgt;
                        }
                    }
                    int x2 = getContext().getResources().getDisplayMetrics().widthPixels;
                    int y2 = getContext().getResources().getDisplayMetrics().heightPixels;
                    mCutawayCanvas.drawCircle(x, y, radius, mCutawayPaint);
                }
            }
            canvas.drawBitmap(mCutawayBitmap, 0, 0, null);
        }
    }

    @Override
    public void onClick(View view){
        mListener.onTooltipClick();
    }
}
