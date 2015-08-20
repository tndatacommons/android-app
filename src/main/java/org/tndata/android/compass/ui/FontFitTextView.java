package org.tndata.android.compass.ui;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;


/**
 * A class that scales down the font size if needed to make it fit inside a TextView
 * of a fixed size.
 *
 * @author Original author unknown
 * @author Edited by Ismael Alonso
 */
public class FontFitTextView extends TextView{
    private static final float THRESHOLD = 0.5f;

    //Attributes
    private Paint mTestPaint;


    /**
     * Constructor.
     *
     * @param context the context.
     */
    public FontFitTextView(Context context){
        super(context);
        initialise();
    }

    /**
     * Constructor. For xml initialisation.
     *
     * @param context the context.
     * @param attrs the attribute set.
     */
    public FontFitTextView(Context context, AttributeSet attrs){
        super(context, attrs);
        initialise();
    }

    /**
     * Creates the test paint, which will be used later on to take measures of the text.
     */
    private void initialise(){
        mTestPaint = new Paint();
        mTestPaint.set(this.getPaint());
    }

    /**
     * Decreases the font size so that the text fits in the TextView in the cases where
     * the text would be wider than the TextView.
     *
     * @param text the TextView's text.
     * @param viewWidth the width of this TextView.
     */
    private void refitText(String text, int viewWidth){
        if (viewWidth > 0){
            int targetWidth = viewWidth-this.getPaddingLeft()-this.getPaddingRight();
            float high, low;

            //Test the current size
            mTestPaint.set(this.getPaint());
            mTestPaint.setTextSize(getTextSize());
            //If the target width is lower than the text width at that size
            if (targetWidth < mTestPaint.measureText(text)){
                //Find values for high and low such that low = high/2 and the target width
                //  is somewhere in between.
                high = getTextSize();
                low = high/2;
                mTestPaint.setTextSize(low);
                while (targetWidth < mTestPaint.measureText(text)){
                    high /= 2;
                    low /= 2;
                    mTestPaint.setTextSize(low);
                }

                //Do a binary search between high and low to find the optimal value
                while (high-low > THRESHOLD){
                    float size = (high+low)/2;
                    mTestPaint.setTextSize(size);
                    if (mTestPaint.measureText(text) >= targetWidth){
                        high = size;
                    }
                    else{
                        low = size;
                    }
                }

                //Finally, set the size as the lower value to avoid overestimating.
                setTextSize(TypedValue.COMPLEX_UNIT_PX, low);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int height = getMeasuredHeight();
        refitText(this.getText().toString(), parentWidth);
        this.setMeasuredDimension(parentWidth, height);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after){
        refitText(text.toString(), this.getWidth());
    }

    @Override
    protected void onSizeChanged(int wid, int hht, int oldWid, int oldHgt){
        if (wid != oldWid){
            refitText(this.getText().toString(), wid);
        }
    }
}
