package org.tndata.android.compass.util;

import java.io.IOException;
import java.io.InputStream;

import org.tndata.android.compass.util.ImageHelper;
import org.tndata.android.compass.util.ImageLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;


/**
 * Downloads a Bitmap in the background. And notifies the callback when the process is over.
 */
public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap>{
    private final ImageLoader.LoadRequest mRequest;
    private final OnDownloadCompleteCallback mCallback;

    private String mUrl;


    /**
     * Constructor.
     *
     * @param request the request object. It contains all the required information.
     * @param callback the callback object.
     */
    public BitmapWorkerTask(@NonNull ImageLoader.LoadRequest request,
                            @NonNull OnDownloadCompleteCallback callback){
        mRequest = request;
        mCallback = callback;
    }

    @Override
    protected Bitmap doInBackground(String... params){
        Log.d("BitmapWorker", "Download started");
        //The url passed and two streams the first one is to calculate the bounds,
        //  the second to download the bitmap.
        mUrl = mRequest.getUrl()
                ;
        InputStream boundStream = null;
        InputStream downloadStream = null;
        
        Bitmap bitmap = null;
        
        try{
            final BitmapFactory.Options options = new BitmapFactory.Options();
            final ImageView imageView = mRequest.getImageView();

            //Flag to decode only the bounds
            options.inJustDecodeBounds = true;

            //Open the bound stream and get the info
            boundStream = new java.net.URL(mUrl).openStream();
            BitmapFactory.decodeStream(boundStream, null, options);

            Log.d("Worker", mUrl);
            Log.d("Worker", imageView.getMeasuredWidth() + ", " + imageView.getMeasuredHeight());

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
        mRequest.setResult(result);
        mCallback.onDownloadComplete(mRequest, isCancelled());
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
     * @version 1.0.1
     */
    public interface OnDownloadCompleteCallback{
        /**
         * Called when the download is complete.
         *
         * @param loadRequest the request object.
         * @param wasCancelled true if the download was cancelled.
         */
        void onDownloadComplete(ImageLoader.LoadRequest loadRequest, boolean wasCancelled);
    }
}
