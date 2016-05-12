package org.tndata.android.compass.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.tndata.android.compass.BuildConfig;
import org.tndata.android.compass.R;


/**
 * Loads images into ImageViews through a url. The process is made up of three steps, first,
 * the memory cache is checked. If the memory cache lookup misses, then the disk cache is
 * checked. Finally, if the disk lookup  misses, the image is downloaded in the background.
 *
 * @author Ismael Alonso
 * @version 2.0.0
 */
public final class ImageLoader{
    //The application context, to get paths
    private static Context mContext = null;

    //A placeholder used until the intended image is loaded
    private static Bitmap mPlaceHolderBitmap;


    /**
     * Initializes the caching system.
     *
     * @param context the application context.
     */
    public static void initialize(Context context){
        if (mContext == null){
            mContext = context;

            mPlaceHolderBitmap = BitmapFactory.decodeResource(mContext.getResources(),
                    R.drawable.ic_compass_white_50dp);
        }
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
        Picasso picasso = Picasso.with(mContext);
        picasso.setIndicatorsEnabled(BuildConfig.DEBUG);
        picasso.load(url).into(view);
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
}
