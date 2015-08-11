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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;


/**
 * Loads images into ImageViews through a url. The process is made up of three steps, first,
 * the memory cache is checked. If the memory cache lookup misses, then the disk cache is
 * checked. Finally, if the disk lookup  misses, the image is downloaded in the background.
 *
 * @author Ismael Alonso
 * @version 2.0.0
 */
public final class ImageLoader{
    //Use a 25 MB internal cache and store the files under the "cache" directory
    private static final int DISK_CACHE_SIZE = 1024*1024*25;
    private static final String DISK_CACHE_SUB_DIR = "cache";
    private static final int DISK_CACHE_INDEX = 0;

    //The application context, to get paths
    private static Context mContext = null;

    //The cache and the lock
    private static DiskLruCache mDiskCache;

    //Queues
    private static LinkedList<LoadRequest> mLoadQueue;
    private static LinkedList<WriteRequest> mWriteQueue;

    private static CacheWorkerTask workerTask;

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

            mDiskCache = null;

            mLoadQueue = new LinkedList<>();
            mWriteQueue = new LinkedList<>();

            workerTask = null;

            mPlaceHolderBitmap = BitmapFactory.decodeResource(mContext.getResources(),
                    R.drawable.ic_action_compass_white);
        }
    }

    /**
     * Queues a load request. Thread safe.
     *
     * @param request the request to be queued.
     */
    private static synchronized void queueLoadRequest(@NonNull LoadRequest request){
        mLoadQueue.addLast(request);
    }

    /**
     * Dequeues a load request. Thread safe.
     *
     * @return the next request in the provided queue.
     */
    private static synchronized LoadRequest dequeueLoadRequest(){
        return mLoadQueue.removeFirst();
    }

    /**
     * Tells whether the load request queue is empty.
     *
     * @return true if it is empty, false otherwise.
     */
    private static synchronized boolean isLoadQueueEmpty(){
        return mLoadQueue.isEmpty();
    }

    /**
     * Queues a request to the writing queue. Thread safe.
     *
     * @param request the request to be queued.
     */
    private static synchronized void queueWriteRequest(@NonNull WriteRequest request){
        mWriteQueue.addLast(request);
    }

    /**
     * Dequeues a write request. Thread safe.
     *
     * @return the next write request in the list.
     */
    private static synchronized WriteRequest dequeueWriteRequest(){
        if (!mWriteQueue.isEmpty()){
            return mWriteQueue.removeFirst();
        }
        return null;
    }

    /**
     * Tells whether the write request queue is empty. Thread safe.
     *
     * @return true if the write request queue is empty, false otherwise.
     */
    private static synchronized boolean isWriteQueueEmpty(){
        return mWriteQueue.isEmpty();
    }

    /**
     * Loads the bitmap at the provided url, but checks the cache first.
     *
     * @param view the view to where the bitmap shall be set.
     * @param url the urk of the bitmap. This acts as a key for the cache.
     * @param flinging avoid downloading on cache miss.
     */
    public static void loadBitmap(ImageView view, String url, boolean flinging){
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
    public static void loadBitmap(ImageView view, String url, boolean flinging, boolean usePlaceholder){
        //1.- Check memory cache
        Bitmap bitmap = MemoryCache.instance().getBitmapFromMemCache(url);
        //2.- On hit, load, on miss, check disk cache
        if (bitmap != null){
            Log.d("MemoryCache", "Hit: " + url);
            view.setImageBitmap(bitmap);
        }
        else{
            Log.d("MemoryCache", "Miss: " + url);

            //Add to queue and start the task if necessary
            queueLoadRequest(new LoadRequest(view, url, flinging, usePlaceholder));
            if (workerTask == null){
                File dir = new File(mContext.getCacheDir().getPath() + File.separator + DISK_CACHE_SUB_DIR);
                workerTask = new CacheWorkerTask();
                workerTask.execute(dir);
            }
        }
    }

    public static void loadBitmap(ImageView view, int width, int height, String url,
                                  boolean flinging, boolean usePlaceholder){

        //1.- Check memory cache
        Bitmap bitmap = MemoryCache.instance().getBitmapFromMemCache(url);
        //2.- On hit, load, on miss, check disk cache
        if (bitmap != null){
            Log.d("MemoryCache", "Hit: " + url);
            view.setImageBitmap(bitmap);
        }
        else{
            Log.d("MemoryCache", "Miss: " + url);

            //Add to queue and start the task if necessary
            queueLoadRequest(new LoadRequest(view, width, height, url, flinging, usePlaceholder));
            if (workerTask == null){
                File dir = new File(mContext.getCacheDir().getPath() + File.separator + DISK_CACHE_SUB_DIR);
                workerTask = new CacheWorkerTask();
                workerTask.execute(dir);
            }
        }
    }

    /**
     * Tells whether the key exists in the disk cache.
     *
     * @param url the key to check for.
     * @return true if it exists, false otherwise.
     */
    private static boolean isBitmapInDiskCache(String url){
        String key = hashKeyForDisk(url);
        if (mDiskCache != null){
            try{
                DiskLruCache.Snapshot snapshot = mDiskCache.get(key);
                if (snapshot == null){
                    Log.d("DiskCache", "Miss: " + url);
                    return false;
                }
                else{
                    Log.d("DiskCache", "Hit: " + url);
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
     * Reads a Bitmap from the disk cache.
     *
     * @param url the url of the resource.
     * @return the decoded Bitmap.
     */
    private static Bitmap readBitmapFromDiskCache(String url){
        String key = hashKeyForDisk(url);

        //This shouldn't happen, but better safe than sorry
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

    /**
     * Writes a Bitmap to the disk cache.
     *
     * @param request the write request to be served.
     */
    private static void writeBitmapToDiskCache(WriteRequest request){
        try{
            String key = hashKeyForDisk(request.mUrl);
            //This shouldn't happen either, but again, better safe than sorry
            if (mDiskCache != null){
                DiskLruCache.Editor editor = mDiskCache.edit(key);
                if (editor != null){
                    OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
                    request.mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    editor.commit();
                }
            }
            else{
                Log.d("DiskCache", "Cache is closed!");
            }
        }
        catch (IOException iox){
            iox.printStackTrace();
        }
    }

    /**
     * Closes the disk cache.
     */
    private static void closeCache(){
        if (mDiskCache != null){
            try{
                mDiskCache.close();
                mDiskCache = null;
            }
            catch (IOException iox){
                //Again, this ain't happening either
                iox.printStackTrace();
            }
        }
    }

    /**
     * Tells whether the cache needs to be reopened. This might happen under a very tight
     * set of circumstances.
     *
     * @return true if there are requests queued, false otherwise.
     */
    private static synchronized boolean isReopenNeeded(){
        return !isLoadQueueEmpty();
    }

    /**
     * A hashing method that changes a string (like a URL) into a hash suitable for using as a
     * disk filename.
     *
     * @param key the key to be hashed.
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

    /**
     * Converts an array of bytes to hex.
     *
     * @param bytes the byte array to be converted.
     * @return the output array as a String in hex.
     */
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
     *
     * @author Edited by Ismael Alonso
     * @version 2.0.0
     */
    static class AsyncDrawable extends BitmapDrawable{
        private BitmapWorkerTask bitmapWorkerTask;


        /**
         * Constructor. Sets the task.
         *
         * @param res a reference to Resources.
         * @param bitmapWorkerTask the task that will do the work.
         */
        @SuppressWarnings("deprecation")
        public AsyncDrawable(Resources res, BitmapWorkerTask bitmapWorkerTask){
            super(res);
            this.bitmapWorkerTask = bitmapWorkerTask;
        }

        /**
         * Constructor. Sets the task and a default Bitmap.
         *
         * @param res a reference to Resources.
         * @param bitmap the default bitmap to be loaded.
         * @param bitmapWorkerTask the task that will do the work.
         */
        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask){
            super(res, bitmap);
            this.bitmapWorkerTask = bitmapWorkerTask;
        }

        /**
         * BitmapWorkerTask getter,
         *
         * @return the worker task assigned to this AsyncDrawable.
         */
        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTask;
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
            if (drawable instanceof AsyncDrawable){
                return ((AsyncDrawable)drawable).getBitmapWorkerTask();
            }
        }
        //If none of those conditions apply
        return null;
    }


    /**
     * A request unit. Used to queue the requests.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private static class LoadRequest{
        private final ImageView mImageView;
        private final int mWidth;
        private final int mHeight;
        private final String mUrl;
        private final boolean mFlinging;
        private final boolean mUsePlaceholder;
        private Bitmap mResult;


        /**
         * Constructor.
         *
         * @param imageView the target ImageView.
         * @param url the source url.
         */
        private LoadRequest(ImageView imageView, String url, boolean flinging, boolean usePlaceholder){
            mImageView = imageView;
            mWidth = -1;
            mHeight = -1;
            mUrl = url;
            mFlinging = flinging;
            mUsePlaceholder = usePlaceholder;
            mResult = null;
        }

        /**
         * Constructor.
         *
         * @param imageView the target ImageView.
         * @param url the source url.
         */
        private LoadRequest(ImageView imageView, int width, int height, String url,
                            boolean flinging, boolean usePlaceholder){
            mImageView = imageView;
            mWidth = width;
            mHeight = height;
            mUrl = url;
            mFlinging = flinging;
            mUsePlaceholder = usePlaceholder;
            mResult = null;
        }

        /**
         * Sets the result of the load operation.
         *
         * @param result the downloaded bitmap.
         */
        private void setResult(Bitmap result){
            mResult = result;
        }
    }


    /**
     * A write request unit. Writing to disk cache may take some time, so requests need
     * to be queued before proceeding.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private static class WriteRequest{
        private final Bitmap mBitmap;
        private final String mUrl;


        /**
         * Constructor.
         *
         * @param bitmap the bitmap to be written to cache.
         * @param url the url the bitmap was fetched from.
         */
        private WriteRequest(Bitmap bitmap, String url){
            mBitmap = bitmap;
            mUrl = url;
        }
    }


    /**
     * The task that serves all the disk cache access requests and spawns the tasks to
     * download new assets.
     *
     * @author Ismael Alonso
     */
    private static class CacheWorkerTask
            extends AsyncTask<File, LoadRequest, Void>
            implements BitmapWorkerTask.OnDownloadCompleteCallback{

        private int pendingRequests;


        /**
         * Constructor.
         */
        private CacheWorkerTask(){
            pendingRequests = 0;
        }

        /**
         * Adds a request to the pending count. Thread safe.
         */
        private synchronized void addRequest(){
            pendingRequests++;
        }

        /**
         * Removes a request from the pending count. Thread safe.
         */
        private synchronized void closeRequest(){
            if (pendingRequests > 0){
                pendingRequests--;
            }
        }

        /**
         * Tells whether there are pending requests. Thread safe.
         *
         * @return true if there are pending requests, false otherwise.
         */
        private synchronized boolean arePendingRequests(){
            return pendingRequests != 0;
        }

        /**
         * Tells whether there are open requests, namely if the queues contain requests to
         * be processed.
         *
         * @return true if there are open requests, false otherwise.
         */
        private boolean areOpenRequests(){
            return !isWriteQueueEmpty() || !isLoadQueueEmpty();
        }

        @Override
        protected Void doInBackground(File... params){
            //First of all, the cache handler is created.
            File cacheDir = params[0];
            try{
                mDiskCache = DiskLruCache.open(cacheDir, 1, 1, DISK_CACHE_SIZE);
            }
            catch (IOException iox){
                //Locally, this ain't ever happening
                iox.printStackTrace();
            }

            //As long as there is something to do, this thread must be kept alive
            while (areOpenRequests() || arePendingRequests()){
                Log.d("CacheWorker", "Iteration");
                //However, it there are only downloads in progress, the thread can be put to sleep
                if (!areOpenRequests()){
                    Log.d("CacheWorker", "Sleeping: " + pendingRequests + " pending requests.");
                    try{
                        Thread.sleep(500);
                    }
                    catch (InterruptedException ix){
                        ix.printStackTrace();
                    }
                }
                //First the load queue is checked
                if (!isLoadQueueEmpty()){
                    LoadRequest request = dequeueLoadRequest();
                    //If the bitmap was found in the disk cache it is loaded, otherwise downloaded.
                    if (isBitmapInDiskCache(request.mUrl)){
                        request.setResult(readBitmapFromDiskCache(request.mUrl));
                    }
                    else{
                        request.setResult(null);
                    }
                    //A pending request is added and the request is published as processed
                    addRequest();
                    publishProgress(request);
                }
                else if (!isWriteQueueEmpty()){
                    writeBitmapToDiskCache(dequeueWriteRequest());
                }
            }

            Log.d("CacheWorker", "Closing cache");
            closeCache();
            return null;
        }

        @Override
        protected void onProgressUpdate(LoadRequest... values){
            //For each published request
            for (LoadRequest request:values){
                //The bitmap needs to be downloaded
                if (request.mResult == null){
                    //If the download is NOT to be performed
                    if (request.mFlinging){
                        //The placeholder is set to the view and the request is closed.
                        request.mImageView.setImageBitmap(mPlaceHolderBitmap);
                        closeRequest();
                    }
                    //If the download is to be performed
                    else if (cancelPotentialWork(request.mUrl, request.mImageView)){
                        BitmapWorkerTask task = getBitmapWorkerTask(request.mImageView);
                        //If the reference was missed and picked by the GC, the task will
                        //  get killed and will never call onDownloadComplete(), so the
                        //  request needs to get closed here.
                        if (task != null){
                            closeRequest();
                        }

                        //The proper task is created
                        if (request.mWidth == -1){
                            task = new BitmapWorkerTask(mContext, request.mImageView, this);
                        }
                        else{
                            task = new BitmapWorkerTask(mContext, request.mImageView, request.mWidth,
                                    request.mHeight, this);
                        }
                        //An AsyncDrawable is created with the selected configuration and set
                        //  as the drawable of the ImageView
                        final AsyncDrawable asyncDrawable;
                        if (request.mUsePlaceholder){
                            asyncDrawable = new AsyncDrawable(mContext.getResources(),
                                    mPlaceHolderBitmap, task);
                        }
                        else{
                            asyncDrawable = new AsyncDrawable(mContext.getResources(), task);
                        }
                        request.mImageView.setImageDrawable(asyncDrawable);

                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request.mUrl);
                    }
                }
                else{
                    MemoryCache.instance().addBitmapToMemoryCache(request.mUrl, request.mResult);
                    request.mImageView.setImageBitmap(request.mResult);
                    closeRequest();
                }
            }
        }

        @Override
        public void onDownloadComplete(BitmapWorkerTask caller, String url, @Nullable Bitmap result,
                                       boolean wasCancelled){
            Log.d("ImageLoader", "Download complete: " + url);
            //When a download is completed, if the task wasn't cancelled the content is queued
            //  to be written to cache. If the task was cancelled, the Bitmap might be corrupt.
            if (!wasCancelled && result != null){
                MemoryCache.instance().addBitmapToMemoryCache(url, result);
                queueWriteRequest(new WriteRequest(result, url));
            }
            //The request is closed
            closeRequest();
        }

        @Override
        protected void onPostExecute(Void result){
            //If after terminating there are still requests in the load queue, the task needs
            //  to be restarted to serve them. This will barely happen, but it is plausible.
            if (isReopenNeeded()){
                Log.d("CacheWorker", "Reopening");
                File dir = new File(mContext.getCacheDir().getPath() + File.separator + DISK_CACHE_SUB_DIR);
                workerTask = new CacheWorkerTask();
                workerTask.execute(dir);
            }
            else{
                workerTask = null;
            }
        }
    }
}
