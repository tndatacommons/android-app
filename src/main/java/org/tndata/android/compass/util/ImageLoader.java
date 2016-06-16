package org.tndata.android.compass.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

import org.tndata.android.compass.BuildConfig;
import org.tndata.android.compass.R;


/**
 * Loads images into ImageViews.
 *
 * @author Ismael Alonso
 * @version 3.0.0
 */
public final class ImageLoader{
    //The application context, to get paths
    private static Context mContext = null;


    /**
     * Initializes the loader
     *
     * @param context the application context.
     */
    public static void initialize(Context context){
        mContext = context;
    }

    /**
     * Loads the bitmap at the provided url, but checks the cache first.
     *
     * @param view the view to where the bitmap shall be set.
     * @param url the urk of the bitmap. This acts as a key for the cache.
     */
    public static void loadBitmap(ImageView view, String url){
        loadBitmap(view, url, new Options());
    }

    /**
     * Loads the bitmap at the provided url, but checks the cache first.
     *
     * @param view the view to where the bitmap shall be set.
     * @param url the urk of the bitmap. This acts as a key for the cache.
     * @param options the option bundle.
     */
    public static void loadBitmap(ImageView view, String url, Options options){
        if (url.isEmpty()){
            view.setImageResource(R.drawable.ic_compass_white_50dp);
        }
        else{
            Picasso picasso = Picasso.with(mContext);
            picasso.setIndicatorsEnabled(API.STAGING && BuildConfig.DEBUG);
            RequestCreator request = picasso.load(url);
            if (options.mUsePlaceholder){
                request.placeholder(R.drawable.ic_compass_white_50dp);
            }
            if (options.mCropToCircle){
                request.transform(new CircleCropTransformation());
            }
            request.into(view);
        }
    }


    /**
     * A bundle of image loading options.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public static final class Options{
        private boolean mFlinging;
        private boolean mUsePlaceholder;
        private boolean mCropToCircle;

        /**
         * Constructor. Defaults all options to false except for the use of a placeholder,
         * which defaults to true.
         */
        public Options(){
            mFlinging = false;
            mUsePlaceholder = true;
            mCropToCircle = false;
        }

        /**
         * Sets the flinging.
         *
         * @param flinging true to avoid downloading on cache miss, false otherwise.
         * @return this bundle.
         */
        public Options setFlinging(boolean flinging){
            mFlinging = flinging;
            return this;
        }

        /**
         * Sets the flag to use a placeholder.
         *
         * @param usePlaceholder true use a placeholder image while loading, false otherwise.
         * @return this bundle.
         */
        public Options setUsePlaceholder(boolean usePlaceholder){
            mUsePlaceholder = usePlaceholder;
            return this;
        }

        /**
         * Sets the flag to crop the loaded image to a circle before setting it to the target.
         *
         * @param cropToCircle true if the image should be cropped to a circle, false otherwise.
         * @return this bundle.
         */
        public Options setCropToCircle(boolean cropToCircle){
            mCropToCircle = cropToCircle;
            return this;
        }
    }

    private static class CircleCropTransformation implements Transformation{
        /**
         * Tells whether a bitmap is square or not.
         *
         * @param src the test subject.
         * @return true if the bitmap is a square, false otherwise.
         */
        private boolean isSquare(Bitmap src){
            return src.getWidth() == src.getHeight();
        }

        @Override
        public Bitmap transform(Bitmap src){
            //Get the size and square the bitmap if necessary
            int size;
            if (isSquare(src)){
                size = src.getWidth();
            }
            else{
                size = src.getWidth() < src.getHeight() ? src.getWidth() : src.getHeight();
                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(src, size, size);
                if (src != thumbnail){
                    src.recycle();
                }
                src = thumbnail;
            }

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
            canvas.drawBitmap(src, rect, rect, paint);

            src.recycle();
            return output;
        }

        @Override
        public String key(){
            return "circleCrop()";
        }
    }
}
