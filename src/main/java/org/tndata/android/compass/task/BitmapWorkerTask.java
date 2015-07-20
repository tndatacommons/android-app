package org.tndata.android.compass.task;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.tndata.android.compass.util.ImageHelper;
import org.tndata.android.compass.util.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;


/**
 * Downloads a Bitmap in the background. And notifies the callback when the process is over.
 */
public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap>{
    private final Context mContext;
    private final OnDownloadCompleteCallback mCallback;
    private final WeakReference<ImageView> mImageViewReference;

    private String mUrl;


    /**
     * Constructor.
     *
     * @param context the application context.
     * @param imageView the ImageView to which the new downloaded Bitmap shall be set.
     * @param callback the callback object.
     */
    public BitmapWorkerTask(@NonNull Context context, ImageView imageView,
                            @NonNull OnDownloadCompleteCallback callback){
        mContext = context;
        mCallback = callback;

        mImageViewReference = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... params){
        Log.d("BitmapWorker", "Download started");
        //The url passed and two streams the first one is to calculate the bounds,
        //  the second to download the bitmap.
        mUrl = params[0];
        InputStream boundStream = null;
        InputStream downloadStream = null;
        
        Bitmap bitmap = null;
        
        try{
            final BitmapFactory.Options options = new BitmapFactory.Options();
            final ImageView imageView = mImageViewReference.get();

            //Flag to decode only the bounds
            options.inJustDecodeBounds = true;

            //Open the bound stream and get the info
            boundStream = new java.net.URL(mUrl).openStream();
            BitmapFactory.decodeStream(boundStream, null, options);

            //Calculate inSampleSize and flag to decode the entire Bitmap
            options.inSampleSize = ImageHelper.calculateInSampleSize(options,
                    imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
            options.inJustDecodeBounds = false;

            //Open the download stream and download the bitmap
            downloadStream = new java.net.URL(mUrl).openStream();
            bitmap =  BitmapFactory.decodeStream(downloadStream, null, options);

            Log.d("BitmapWorkerException", "happened: " + isCancelled());
        }
        catch (Exception e){
            Log.d("BitmapWorkerException", e.getMessage());
            e.printStackTrace();
        }
        finally{
            //Close the two streams
            if (boundStream != null){
                try{
                    boundStream.close();
                }
                catch (IOException iox){
                    iox.printStackTrace();
                }
            }
            if (downloadStream != null){
                try{
                    downloadStream.close();
                }
                catch (IOException iox){
                    iox.printStackTrace();
                }
            }
        }

        //If at any point the download failed this will return null
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result){
        if (result != null){
            final ImageView imageView = mImageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask = ImageLoader.getBitmapWorkerTask(imageView);
            if (this == bitmapWorkerTask){
                imageView.setImageBitmap(result);
            }
        }

        mCallback.onDownloadComplete(this, mUrl, result, isCancelled());
    }

    /**
     * Tells whether this Task is working on the download of the given url.
     *
     * @param url the url to check.
     * @return true if it is, false otherwise.
     */
    public boolean workingOn(String url){
        return mUrl != null && mUrl.equals(url);
    }


    /**
     * Callback interface for download complete events.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface OnDownloadCompleteCallback{
        /**
         * Called when the download is complete.
         *
         * @param caller the calling BitmapWorkerTask
         * @param url the url of the downloaded bitmap
         * @param result the bitmap obtained through the process; null if the download failed.
         * @param wasCancelled true if the download was cancelled.
         */
        void onDownloadComplete(BitmapWorkerTask caller, String url, @Nullable Bitmap result,
                                boolean wasCancelled);
    }
}
