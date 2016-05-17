package org.tndata.android.compass.util;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

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
            picasso.setIndicatorsEnabled(BuildConfig.DEBUG);
            RequestCreator request = picasso.load(url);
            if (options.mUsePlaceholder){
                request.placeholder(R.drawable.ic_compass_white_50dp);
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
}
