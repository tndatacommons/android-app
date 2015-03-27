package org.tndata.android.grow.task;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.tndata.android.grow.util.ImageCache;
import org.tndata.android.grow.util.ImageHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
    private Context mContext = null;
    public String mUrl = null;
    private final WeakReference<ImageView> mImageViewReference;

    public BitmapWorkerTask(ImageView imageView, Context context) {
        mImageViewReference = new WeakReference<ImageView>(imageView);
        mContext = context;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        mUrl = params[0];
        Bitmap bitmap = null;
        try {

            InputStream in = new java.net.URL(mUrl).openStream();
            InputStream is = new java.net.URL(mUrl).openStream();

            final BitmapFactory.Options options = new BitmapFactory.Options();

            if (mImageViewReference != null) {
                final ImageView imageView = mImageViewReference.get();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in, null, options);
                // Calculate inSampleSize
                options.inSampleSize = ImageHelper.calculateInSampleSize(
                        options, imageView.getMeasuredWidth(),
                        imageView.getMeasuredHeight());
            }
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(is, null, options);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap result) {
        if (isCancelled()) {
            result = null;
        }

        if (mImageViewReference != null && result != null) {
            final ImageView imageView = mImageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask = ImageCache
                    .getBitmapWorkerTask(imageView);
            if (this == bitmapWorkerTask && imageView != null) {
                imageView.setImageBitmap(result);
                ImageCache.instance(mContext).addBitmapToMemoryCache(mUrl,
                        result);
            }
        }

    }

}
