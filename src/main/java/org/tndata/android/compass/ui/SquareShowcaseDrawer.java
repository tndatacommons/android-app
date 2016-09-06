package org.tndata.android.compass.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.view.View;

import com.github.amlcurran.showcaseview.ShowcaseDrawer;

import org.tndata.android.compass.util.CompassUtil;


/**
 * Custom drawer for a square cutaway in the showcase style tour.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class SquareShowcaseDrawer implements ShowcaseDrawer{
    private int mBackgroundColor;
    private View mTarget;
    private Paint mEraserPaint;
    private Paint mBasicPaint;


    public SquareShowcaseDrawer(@NonNull View target){
        mTarget = target;

        mEraserPaint = new Paint();
        mEraserPaint.setColor(0xFFFFFF);
        mEraserPaint.setAlpha(0);
        mEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        mEraserPaint.setAntiAlias(true);

        mBasicPaint = new Paint();
    }

    @Override
    public void setShowcaseColour(@ColorInt int color){

    }

    @Override
    public void drawShowcase(Bitmap buffer, float x, float y, float scaleMultiplier){
        int margin = CompassUtil.getPixels(mTarget.getContext(), 8);
        float left = x - getShowcaseWidth()/2 - margin;
        float top = y - getShowcaseHeight()/2 - margin;
        float right = x + getShowcaseWidth()/2 + margin;
        float bottom = y + getShowcaseHeight()/2 + margin;

        Canvas bufferCanvas = new Canvas(buffer);
        bufferCanvas.drawRect(left, top, right, bottom, mEraserPaint);
    }

    @Override
    public int getShowcaseWidth(){
        return mTarget.getWidth();
    }

    @Override
    public int getShowcaseHeight(){
        return mTarget.getHeight();
    }

    @Override
    public float getBlockedRadius(){
        return getShowcaseHeight()/2;
    }

    @Override
    public void setBackgroundColour(@ColorInt int backgroundColor){
        mBackgroundColor = backgroundColor;
    }

    @Override
    public void erase(Bitmap bitmapBuffer){
        bitmapBuffer.eraseColor(mBackgroundColor);
    }

    @Override
    public void drawToCanvas(Canvas canvas, Bitmap bitmapBuffer){
        canvas.drawBitmap(bitmapBuffer, 0, 0, mBasicPaint);
    }
}
