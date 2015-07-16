package org.tndata.android.compass.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.task.BitmapWorkerTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Loads images into ImageViews through a url. The process is made up of three steps, first,
 * the memory cache is checked. If the memory cache lookup misses, then the disk cache is
 * checked. Finally, if the disk lookup  misses, the image is downloaded in the background.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ImageLoader implements BitmapWorkerTask.OnDownloadCompleteCallback{
    //Use a 25 MB internal cache and store the files under the "cache" directory
    private static final int DISK_CACHE_SIZE = 1024*1024*25;
    private static final String DISK_CACHE_SUB_DIR = "cache";
    private static final int DISK_CACHE_INDEX = 0;

    //The application context, to get paths
    private Context mContext;

    //The cache and the lock (shared across all instantiations)
    private DiskLruCache mDiskCache;
    private final Object mDiskCacheLock;

    //Flag to indicate that the disk cache is opening, once it's opened it is set to false
    private boolean mDiskCacheOpening;

    //A placeholder used until the intended image is loaded
    private Bitmap mPlaceHolderBitmap;


    /**
     * Constructor. Sets the context and initialises the cache handler if necessary.
     *
     * @param context the application context
     */
    public ImageLoader(Context context){
        mContext = context;
        mDiskCache = null;
        mDiskCacheLock = new Object();
        mDiskCacheOpening = false;

        mPlaceHolderBitmap = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.ic_action_compass_white);

        initCache();
    }

    /**
     * If the cache handler is neither initialised nor initialising, it is initialised.
     */
    public void initCache(){
        if (mDiskCache == null && !mDiskCacheOpening){
            mDiskCacheOpening = true;
            File dir = new File(mContext.getCacheDir().getPath() + File.separator + DISK_CACHE_SUB_DIR);
            new InitDiskCacheTask().execute(dir);
        }
    }

    /**
     * Loads the bitmap at the provided url, but checks the cache first.
     *
     * @param view the view to where the bitmap shall be set.
     * @param url the urk of the bitmap. This acts as a key for the cache.
     * @param flinging avoid downloading on cache miss.
     */
    public void loadBitmap(ImageView view, String url, boolean flinging){
        loadBitmap(view, url, flinging, true);
    }

    /**
     * Loads the bitmap at the provided url, but checks the cache first.
     *
     * @param view the view to where the bitmap shall be set.
     * @param url the urk of the bitmap. This acts as a key for the cache.
     * @param flinging avoid downloading on cache miss.
     * @param usePlaceholder use a placeholder while the bitmap loads.
     */
    public void loadBitmap(ImageView view, String url, boolean flinging, boolean usePlaceholder){
        //1.- Check memory cache
        Bitmap bitmap = ImageCache.instance(mContext).getBitmapFromMemCache(url);
        //2.- On hit, load, on miss, check disk cache
        if (bitmap == null){
            Log.d("MemoryCache", "Miss: " + url);
            //3.- On hit, load, on miss, download, then write to cache
            if (isBitmapInDiskCache(hashKeyForDisk(url))){
                new ReadFromDiskCacheTask(view).execute(url);
            }
            else{
                if (flinging){
                    view.setImageBitmap(mPlaceHolderBitmap);
                }
                else if (cancelPotentialWork(url, view)){
                    //If the image needs to be downloaded, the proper task is created
                    BitmapWorkerTask task = new BitmapWorkerTask(mContext, view, this);
                    //An AsyncDrawable is created with the selected configuration and set
                    //  as the drawable of the ImageView
                    final NewAsyncDrawable asyncDrawable;
                    if (usePlaceholder){
                        asyncDrawable = new NewAsyncDrawable(mContext.getResources(),
                                mPlaceHolderBitmap, task);
                    }
                    else{
                        asyncDrawable = new NewAsyncDrawable(mContext.getResources(), task);
                    }
                    view.setImageDrawable(asyncDrawable);

                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
                }
            }
        }
        else{
            Log.d("MemoryCache", "Hit: " + url);
            view.setImageBitmap(bitmap);
        }
    }

    /**
     * Tells whether the key exists in the disk cache.
     *
     * @param key the key to check for.
     * @return true if it exists, false otherwise.
     */
    private boolean isBitmapInDiskCache(String key){
        if (mDiskCache != null){
            try{
                DiskLruCache.Snapshot snapshot = mDiskCache.get(key);
                if (snapshot == null){
                    Log.d("DiskCache", "Miss: " + key);
                    return false;
                }
                else{
                    Log.d("DiskCache", "Hit: " + key);
                    snapshot.close();
                    return true;
                }
            }
            catch (IOException iox){
                iox.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Closes the cache.
     */
    public void closeCache(){
        if (mDiskCache != null){
            try{
                mDiskCache.close();
                mDiskCache = null;
                mDiskCacheOpening = false;
            }
            catch (IOException iox){
                //Again, this ain't happening either
                iox.printStackTrace();
            }
        }
    }

    @Override
    public void onDownloadComplete(String url, @Nullable Bitmap result, boolean wasCancelled){
        Log.d("ImageLoader", "Download complete");
        if (result != null){
            ImageCache.instance(mContext).addBitmapToMemoryCache(url, result);
            new WriteToDiskCacheTask(result).execute(url);
        }
    }

    /**
     * A hashing method that changes a string (like a URL) into a hash suitable for using as a
     * disk filename.
     */
    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try{
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        }
        catch (NoSuchAlgorithmException nfx){
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for (byte b:bytes){
            String hex = Integer.toHexString(0xFF&b);
            if (hex.length() == 1){
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * Holds a reference to the task downloading a bitmap for this BitmapDrawable's
     * holding ImageView.
     */
    static class NewAsyncDrawable extends BitmapDrawable{
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        /**
         * Constructor. Sets the task.
         *
         * @param res a reference to Resources.
         * @param bitmapWorkerTask the task that will do the work.
         */
        @SuppressWarnings("deprecation")
        public NewAsyncDrawable(Resources res, BitmapWorkerTask bitmapWorkerTask){
            super(res);
            bitmapWorkerTaskReference = new WeakReference<>(bitmapWorkerTask);
        }

        /**
         * Constructor. Sets the task and a default Bitmap.
         *
         * @param res a reference to Resources.
         * @param bitmap the default bitmap to be loaded.
         * @param bitmapWorkerTask the task that will do the work.
         */
        public NewAsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask){
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<>(bitmapWorkerTask);
        }

        /**
         * BitmapWorkerTask getter,
         *
         * @return the worker task assigned to this AsyncDrawable.
         */
        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    /**
     * Cancels the work to be done related to the provided view if the work to be assigned
     * next is not the same and returns whether the caller is clear to proceed with the new
     * job or not.
     *
     * @param url the url of the image to be downloaded. This parameter acts as an id.
     * @param imageView the ImageView to where the Bitmap will be loaded.
     * @return true if the new job is clear to proceed, false otherwise.
     */
    public static boolean cancelPotentialWork(String url, ImageView imageView){
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        //If the provided view has a worker
        if (bitmapWorkerTask != null){
            //And the worker is not doing the new job already (from a previous call)
            if (!bitmapWorkerTask.workingOn(url)){
                //Cancel previous task
                bitmapWorkerTask.cancel(true);
            }
            else{
                //The new job is already in progress
                return false;
            }
        }
        //No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    /**
     * Returns the {@code BitmapWorkerTask} from the provided image view.
     *
     * @param imageView the view from which the worker task is to be extracted
     * @return the task if it exists, {@code null} otherwise.
     */
    public static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView){
        //If the imageView is not null, extract its drawable
        if (imageView != null){
            final Drawable drawable = imageView.getDrawable();
            //If the drawable is an AsyncDrawable, return the task it holds
            if (drawable instanceof NewAsyncDrawable){
                return ((NewAsyncDrawable)drawable).getBitmapWorkerTask();
            }
        }
        //If none of those conditions apply
        return null;
    }


    /**
     * Creates the disk cache handler.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class InitDiskCacheTask extends AsyncTask<File, Void, Void>{
        @Override
        protected Void doInBackground(File... params){
            synchronized (mDiskCacheLock) {
                File cacheDir = params[0];
                try{
                    mDiskCache = DiskLruCache.open(cacheDir, 1, 1, DISK_CACHE_SIZE);
                }
                catch (IOException iox){
                    //Locally, this ain't ever happening
                    iox.printStackTrace();
                }
                mDiskCacheOpening = false; // Finished initialization
                mDiskCacheLock.notifyAll(); // Wake any waiting threads
            }
            return null;
        }
    }


    /**
     * Loads the bitmap at the key in the view if it exists.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class ReadFromDiskCacheTask extends AsyncTask<String, Void, Bitmap>{
        private final ImageView mView;
        private String mUrl;


        /**
         * Constructor.
         *
         * @param view the target view.
         */
        public ReadFromDiskCacheTask(@NonNull ImageView view){
            mView = view;
        }

        @Override
        protected Bitmap doInBackground(String... params){
            mUrl = params[0];
            String key = hashKeyForDisk(mUrl);

            if (mDiskCache != null){
                Bitmap bitmap = null;
                try{
                    DiskLruCache.Snapshot snapshot = mDiskCache.get(key);
                    if (snapshot != null){
                        //Load the Bitmap file from the cache and close the snapshot (closing
                        //  the snapshot also closes the InputStream)
                        FileInputStream inputStream = (FileInputStream)snapshot
                                .getInputStream(DISK_CACHE_INDEX);
                        bitmap = BitmapFactory.decodeFileDescriptor(inputStream.getFD());
                        snapshot.close();
                    }
                }
                catch (IOException iox){
                    iox.printStackTrace();
                }
                return bitmap;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result){
            ImageCache.instance(mContext).addBitmapToMemoryCache(mUrl, result);
            mView.setImageBitmap(result);
        }
    }


    /**
     * Writes a Bitmap to the disk cache.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class WriteToDiskCacheTask extends AsyncTask<String, Void, Void>{
        private Bitmap mBitmap;


        /**
         * Constructor.
         *
         * @param bitmap the bitmap to write.
         */
        public WriteToDiskCacheTask(Bitmap bitmap){
            this.mBitmap = bitmap;
        }

        @Override
        protected Void doInBackground(String... params){
            String key = hashKeyForDisk(params[0]);

            //Wait until the cache is free
            synchronized (mDiskCacheLock){
                //Wait until the cache interface opens up
                while (mDiskCacheOpening){
                    try{
                        mDiskCacheLock.wait();
                    }
                    catch (InterruptedException ix){
                        //Nothing to do here
                    }
                }
                try{
                    DiskLruCache.Editor editor = mDiskCache.edit(key);
                    if (editor != null){
                        OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
                        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        editor.commit();
                    }
                }
                catch (IOException iox){
                    iox.printStackTrace();
                }
            }
            return null;
        }
    }
}
