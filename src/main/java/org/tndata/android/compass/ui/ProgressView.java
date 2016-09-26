package org.tndata.android.compass.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import org.tndata.android.compass.R;
import org.tndata.android.compass.util.CompassUtil;


/**
 * Custom UI component that displays a percentage as a vertical color filling image.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ProgressView extends View{
    //Percentage text markers
    private static final String m100 = "100%";
    private static final String m50 = "50%";
    private static final String m0 = "0%";


    //Bitmap related objects
    private Bitmap mProgressBitmap;
    private Paint mGrayscalePaint;
    private Rect mGrayscaleSrcBounds;
    private Rect mGrayscaleDstBounds;
    private Rect mColorSrcBounds;
    private Rect mColorDstBounds;

    //Percentages related paint
    private Paint mPercentagesPaint;

    //Test rect to calculate bounds at random
    private Rect mTestBounds;

    //Inner padding
    private int mHorizontalPadding;
    private int mVerticalPadding;

    //Data
    private int mProgress;


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

    /**
     * Initialization method. Allocates all the necessary objects.
     */
    private void init(){
        mProgressBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_quote_150dp);
        mGrayscalePaint = new Paint();
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        mGrayscalePaint.setColorFilter(new ColorMatrixColorFilter(matrix));
        mGrayscaleSrcBounds = new Rect();
        mGrayscaleDstBounds = new Rect();
        mColorSrcBounds = new Rect();
        mColorDstBounds = new Rect();

        mPercentagesPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPercentagesPaint.setTextSize(getPxFromSp(18));
        mPercentagesPaint.setTextAlign(Paint.Align.RIGHT);

        mTestBounds = new Rect();

        mProgress = 0;
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
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        int w = getMeasuredWidth();
        int h = getMeasuredHeight();

        mPercentagesPaint.getTextBounds(m100, 0, 4, mTestBounds);

        //Src bound primitives
        int srcBoundary = (int)(mProgressBitmap.getHeight()*((100-mProgress)/100f));

        //Dst bound primitives
        int left = mHorizontalPadding;
        int bottom = h - mVerticalPadding;
        int right = w - mTestBounds.width() - mHorizontalPadding;
        int top = mVerticalPadding;
        int dstBoundary = (int)(top+(bottom-top)*((100-mProgress)/100f));

        //Set the right values in the bound objects
        mGrayscaleSrcBounds.set(0, 0, mProgressBitmap.getWidth(), srcBoundary);
        mGrayscaleDstBounds.set(left, top, right, dstBoundary);
        mColorSrcBounds.set(0, srcBoundary, mProgressBitmap.getWidth(), mProgressBitmap.getHeight());
        mColorDstBounds.set(left, dstBoundary, right, bottom);

        //Draw the bitmap
        canvas.drawBitmap(mProgressBitmap, mGrayscaleSrcBounds, mGrayscaleDstBounds, mGrayscalePaint);
        canvas.drawBitmap(mProgressBitmap, mColorSrcBounds, mColorDstBounds, null);

        //Draw the percentages
        canvas.drawText(m100, w-mHorizontalPadding, mTestBounds.height()+mVerticalPadding, mPercentagesPaint);
        canvas.drawText(m50, w-mHorizontalPadding, h/2+mTestBounds.height()/2, mPercentagesPaint);
        canvas.drawText(m0, w-mHorizontalPadding, h-mVerticalPadding, mPercentagesPaint);
    }

    /**
     * Utility method to convert scale pixels to pixels.
     *
     * @param sp the value to convert.
     * @return the value in pixels.
     */
    private float getPxFromSp(float sp){
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics()
        );
    }

    /**
     * Sets the progress to be displayed by this view.
     *
     * @param progress the progress value.
     */
    public void setProgressValue(int progress){
        mProgress = Math.min(100, Math.max(0, progress));
        invalidate();
    }

    /**
     * Gets the currently displayed progress.
     *
     * @return the currently displayed progress.
     */
    public int getProgressValue(){
        return mProgress;
    }
}
