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

import org.tndata.android.compass.R;
import org.tndata.android.compass.util.CompassUtil;


/**
 * Created by isma on 9/23/16.
 */
public class ProgressView extends View{
    private static final String m100 = "100%";
    private static final String m50 = "50%";
    private static final String m0 = "0%";


    private Paint mPercentagesPaint;
    private Paint mTextPaint;

    private Bitmap mSample;

    private Rect mPercentagesBounds;
    private Rect mBitmapBounds;
    private Rect mTextBounds;

    private Rect mTestBounds;

    //Inner padding
    private int mHorizontalPadding;
    private int mVerticalPadding;


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

        mTestBounds = new Rect();

        mPercentagesPaint.getTextBounds("100%", 0, 4, mPercentagesBounds);

        mSample = BitmapFactory.decodeResource(getResources(), R.drawable.ic_quote_150dp);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        //Defaults for wrapping content, width will eventually be bigger, since this
        //  view cannot be square
        final int defaultWidth = CompassUtil.getPixels(getContext(), 150);
        final int defaultHeight = CompassUtil.getPixels(getContext(), 150);

        //Modes and sizes requested
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //Calculations for dimension and excess are stored here
        int width = defaultWidth;
        int height = defaultHeight;
        int excessWidth = 0;
        int excessHeight = 0;

        //Test the longer text, in this case m100, to calculate the parenthesis section width
        mPercentagesPaint.getTextBounds(m100, 0, m100.length(), mTestBounds);

        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY){
            width = widthSize;
            height = heightSize;

            if (width - mTestBounds.width() > height){
                //If the width is larger than it needs, calculate the excess
                excessWidth = (width-mTestBounds.width())-height;
            }
            else{
                //Same drill for height
                excessHeight = height-(width-mTestBounds.width());
            }
        }
        else if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.AT_MOST){
            //Calculate the height as a function of the width
            width = widthSize;
            height = width-mTestBounds.width();

            if (height > heightSize){
                //If the height is larger than the maximum allowed, calculate the excess width
                //  and set the height to the biggest it can be
                excessWidth = height-heightSize;
                height = heightSize;
            }
        }
        else if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.EXACTLY){
            //Calculate the width as a function of the height
            height = heightSize;
            width = height+mTestBounds.width();

            if (width > widthSize){
                //If the width is larger than the maximum allowed, calculate the excess height
                //  and set the width to the biggest it can be
                excessHeight = width-widthSize;
                width = widthSize;
            }
        }
        else if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST){
            //Make the view as big as possible keeping the ratio
            if (widthSize - mTestBounds.width() > heightSize){
                height = heightSize;
                width = height-(widthSize-mTestBounds.width());
            }
            else{
                width = widthSize;
                height = (width-mTestBounds.width())-heightSize;
            }
        }

        mHorizontalPadding = excessWidth/2;
        mVerticalPadding = excessHeight/2;
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

        mPercentagesPaint.getTextBounds("100%", 0, 4, mTestBounds);

        int left = mHorizontalPadding;
        int bottom = h - mVerticalPadding;
        int right = w - mTestBounds.width() - mHorizontalPadding;
        int top = mVerticalPadding;
        mBitmapBounds.set(left, top, right, bottom);


        canvas.drawText("100%", w-mHorizontalPadding, mTestBounds.height()+mVerticalPadding, mPercentagesPaint);
        canvas.drawText("50%", w-mHorizontalPadding, h/2+mTestBounds.height()/2, mPercentagesPaint);
        canvas.drawText("0%", w-mHorizontalPadding, h-mVerticalPadding, mPercentagesPaint);
        canvas.drawBitmap(mSample, null, mBitmapBounds, null);
    }

    private float getPxFromSp(float sp){
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics()
        );
    }
}
