package org.tndata.android.compass.util;

import java.lang.ref.WeakReference;

import org.tndata.android.compass.R;
import org.tndata.android.compass.task.BitmapWorkerTask;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

public class ImageCache {

    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruCache mDiskCache;
    private static ImageCache mImageCache = null;
    private static Context mContext;
    private static Bitmap mPlaceHolderBitmap;

    public static ImageCache instance(Context context) {
        mContext = context;
        if (mImageCache != null) {
            return mImageCache;
        } else {
            mImageCache = new ImageCache();
        }

        mPlaceHolderBitmap = BitmapFactory.decodeResource(
                mContext.getResources(), R.drawable.ic_action_compass_white);
        return mImageCache;

    }

    private ImageCache() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (key == null || bitmap == null) {
            return;
        }
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public void loadBitmap(ImageView imageView, String id, boolean flinging) {
        loadBitmap(imageView, id, flinging, true);
    }

    public void loadBitmap(ImageView imageView, String id, boolean flinging,
                           boolean usePlaceholder) {
        final Bitmap bitmap = getBitmapFromMemCache(id);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else if (flinging) {
            imageView.setImageBitmap(mPlaceHolderBitmap);
        } else if (cancelPotentialWork(id, imageView)) {
            BitmapWorkerTask task = new BitmapWorkerTask(imageView, mContext);
            final AsyncDrawable asyncDrawable;
            if (usePlaceholder) {
                asyncDrawable = new AsyncDrawable(
                        mContext.getResources(), mPlaceHolderBitmap, task);
            } else {
                asyncDrawable = new AsyncDrawable(
                        mContext.getResources(), task);
            }
            imageView.setImageDrawable(asyncDrawable);

            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id);
        }
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, BitmapWorkerTask bitmapWorkerTask) {
            super(res);
            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(
                    bitmapWorkerTask);
        }

        @Deprecated
        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(
                    bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    public static boolean cancelPotentialWork(String url, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.mUrl;
            if ((bitmapData == null) || (!bitmapData.equals(url))) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was
        // cancelled
        return true;
    }

    public static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

}
