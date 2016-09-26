package org.tndata.android.compass.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.util.CompassUtil;


/**
 * Created by isma on 9/23/16.
 */
public class ProgressView extends View{
    private Paint mPercentagesPaint;
    private Paint mTextPaint;

    private Bitmap mSample;

    private Rect mPercentagesBounds;
    private Rect mBitmapBounds;
    private Rect mTextBounds;

    //Inner padding
    private int mLeft;
    private int mBottom;
    private int mRight;
    private int mTop;


    public ProgressView(Context context){
        super(context);
        init();
    }

    public ProgressView(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPercentagesPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPercentagesPaint.setTextSize(getPxFromSp(18));
        mPercentagesPaint.setTextAlign(Paint.Align.RIGHT);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPercentagesBounds = new Rect();
        mBitmapBounds = new Rect();
        mTextBounds = new Rect();

        mPercentagesPaint.getTextBounds("100%", 0, 4, mPercentagesBounds);

        mSample = BitmapFactory.decodeResource(getResources(), R.drawable.ic_quote_150dp);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        final int defaultWidth = CompassUtil.getPixels(getContext(), 150);
        final int defaultHeight = CompassUtil.getPixels(getContext(), 150);

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = defaultWidth;
        int height = defaultHeight;

        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY){
            width = widthSize;
            height = heightSize;

            if (width - mPercentagesBounds.width() > height){
                
            }
        }
        else if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.AT_MOST){
            width = widthSize;
            height = heightSize;

        }
        else if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.EXACTLY){
            width = widthSize;
            height = heightSize;

        }
        else if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST){
            width = widthSize;
            height = heightSize;

        }

        else if (widthMode == MeasureSpec.AT_MOST){
            width = Math.min(widthSize, defaultWidth);
        }

        if (heightMode == MeasureSpec.EXACTLY){
            height = heightSize;
        }
        else if (heightMode == MeasureSpec.AT_MOST){
            height = Math.min(heightSize, defaultHeight);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("ProgressView", "width: " + w + ", height: " + h);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        int w = getMeasuredWidth();
        int h = getMeasuredHeight();

        mPercentagesPaint.getTextBounds("100%", 0, 4, mPercentagesBounds);
        mBitmapBounds.set(w - h - mPercentagesBounds.width(), 0, w - mPercentagesBounds.width(), h);


        canvas.drawText("100%", w, mPercentagesBounds.height(), mPercentagesPaint);
        canvas.drawText("50%", w, h/2+mPercentagesBounds.height()/2, mPercentagesPaint);
        canvas.drawText("0%", w, h, mPercentagesPaint);
        canvas.drawBitmap(mSample, null, mBitmapBounds, null);
    }

    private float getPxFromSp(float sp){
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics()
        );
    }
}
