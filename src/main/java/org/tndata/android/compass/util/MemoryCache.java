package org.tndata.android.compass.util;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;


/**
 * The memory cache handler.
 *
 * @author Edited by Ismael Alonso
 * @version 2.0.0
 */
public class MemoryCache{
    private LruCache<String, Bitmap> mMemoryCache;
    private static MemoryCache mMemoryCacheInstance = null;


    /**
     * Returns an instance of the MemoryCache handler.
     *
     * @return an instance of the MemoryCache handler.
     */
    public static MemoryCache instance(){
        if (mMemoryCacheInstance != null){
            return mMemoryCacheInstance;
        } else {
            mMemoryCacheInstance = new MemoryCache();
        }
        return mMemoryCacheInstance;

    }

    /**
     * Constructor. Initializes the handler.
     */
    private MemoryCache(){
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    /**
     * Adds a Bitmap to the memory cache.
     *
     * @param key the url of the Bitmap.
     * @param bitmap the Bitmap to be added.
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap){
        if (key == null || bitmap == null){
            return;
        }
        if (getBitmapFromMemCache(key) == null){
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * Retrieves a Bitmap from the memory cache.
     *
     * @param key the ur of the bitmap.
     * @return a bitmap if it exists, null otherwise.
     */
    public Bitmap getBitmapFromMemCache(String key){
        return mMemoryCache.get(key);
    }
}
