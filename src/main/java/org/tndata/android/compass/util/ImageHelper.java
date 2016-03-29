package org.tndata.android.compass.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ThumbnailUtils;


/**
 * Image utility class.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ImageHelper{
    /**
     * Calculates the sample size given a bitmap's parameters and requested width and height.
     *
     * @param options the bitmap's parameters.
     * @param reqWidth the minimum width.
     * @param reqHeight the minimum height.
     * @return the appropriate inSampleSize value.
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth){
            final int halfHeight = height/2;
            final int halfWidth = width/2;

            //Calculate the largest inSampleSize value that is a power of 2 and keeps both
            //   height and width larger than the requested height and width.
            while ((halfHeight/inSampleSize) > reqHeight && (halfWidth/inSampleSize) > reqWidth){
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Creates a circle bitmap from the provided bitmap.
     *
     * @param bmp the source bitmap.
     * @param size the size of the result.
     * @return the processed bitmap.
     */
    public static Bitmap getCircleBitmap(Bitmap bmp, int size){
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bmp, size, size);
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        int color = Color.RED;
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, size, size);
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(thumbnail, rect, rect, paint);

        //thumbnail.recycle();
        return output;
    }

    /**
     * Crops out the bottom section of a bitmap to turn it a 2:1 ratio bitmap.
     *
     * @param bmp the source bitmap.
     * @return the processed bitmap.
     */
    public static Bitmap cropOutBottom(Bitmap bmp){
        int width = bmp.getWidth();
        int height = width/2;

        return Bitmap.createBitmap(bmp, 0, 0, width, height);
    }
}
