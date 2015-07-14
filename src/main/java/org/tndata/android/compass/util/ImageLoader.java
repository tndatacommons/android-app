package org.tndata.android.compass.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

/**
 * Created by isma on 7/14/15.
 */
public class ImageLoader{
    //Use a 25 MB internal cache and store the files under the "cache" directory
    private static final int DISK_CACHE_SIZE = 1024*1024*25; // 25MB
    private static final String DISK_CACHE_SUBDIR = "cache";

    //The application context, to get paths
    private Context mContext;

    //The cache and the lock
    private DiskLruCache mDiskCache;
    private final Object mDiskCacheLock;

    //Flag to indicate that the disk cache is opening, once it's opened it is set to false
    private boolean mDiskCacheOpening;


    public ImageLoader(Context context){
        mContext = context;
        mDiskCacheLock = new Object();
        mDiskCacheOpening = true;
        initCache();
    }

    private void initCache(){
        File dir = new File(mContext.getCacheDir().getPath() + File.separator + DISK_CACHE_SUBDIR);
        new InitDiskCacheTask().execute(dir);
    }

    public void loadImage(String url, ImageView view){
        //1.- Check memory cache
        //2.- On hit, load, on miss, check disk cache
        //3.- On hit, load, on miss, download, then write to cache, then load

        //Might need to implement a system to prevent loading bitmaps in recycled views
    }

    public Bitmap getBitmapFromDiskCache(String key){
        //Wait until the cache is free
        synchronized (mDiskCacheLock){
            //Wait until the cache interface opens
            while (mDiskCacheOpening){
                try{
                    mDiskCacheLock.wait();
                }
                catch (InterruptedException ix){
                    //Nothing to do here
                }
            }
            if (mDiskCache != null){
                try{
                    //Load from the IS or FD
                    mDiskCache.get(key).getInputStream(0);
                }
                catch (IOException iox){
                    iox.printStackTrace();
                }
            }
        }
        return null;
    }


    public void close(){
        if (mDiskCache != null){
            try{
                mDiskCache.close();
                mDiskCache = null;
                mDiskCacheOpening = true;
            }
            catch (IOException iox){
                //Again, this ain't happening either
                iox.printStackTrace();
            }
        }
    }

    class InitDiskCacheTask extends AsyncTask<File, Void, Void>{
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
}
