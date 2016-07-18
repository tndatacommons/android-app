package org.tndata.android.compass.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Badge;
import org.tndata.android.compass.util.ImageLoader;


/**
 * Activity used to display a badge.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class BadgeActivity
        extends AppCompatActivity
        implements
                View.OnClickListener,
                ImageLoader.ImageLoaderCallback{

    private static final String TAG = "BadgeActivity";

    public static final String BADGE_KEY = "org.tndata.compass.BadgeActivity.Badge";


    private ImageView mImage;
    private Badge mBadge;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge);

        mBadge = getIntent().getParcelableExtra(BADGE_KEY);

        mImage = (ImageView)findViewById(R.id.badge_image);
        TextView name = (TextView)findViewById(R.id.badge_name);
        TextView description = (TextView)findViewById(R.id.badge_description);
        View share = findViewById(R.id.badge_share);

        //Warning killers...
        assert name != null;
        assert description != null;
        assert share != null;

        ImageLoader.Options options = new ImageLoader.Options().setUseDefaultPlaceholder(false);
        ImageLoader.loadBitmap(mImage, mBadge.getImageUrl(), options, this);

        name.setText(getString(R.string.badge_header, mBadge.getName().toUpperCase()));
        description.setText(mBadge.getDescription());
        share.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        if (view.getId() == R.id.badge_share){
            String content = getString(R.string.badge_share, mBadge.getName());

            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        }
    }

    @Override
    public void onImageLoadSuccess(){
        Log.i(TAG, "Image loaded");

        // We need the bitmap images size here, because the view will be
        // 0x0 until after the animation.
        Bitmap imageBitmap = ((BitmapDrawable)mImage.getDrawable()).getBitmap();
        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();
        Log.d(TAG, "Image size: (" + width + ", " + height + ")");

        Animation scale = new ScaleAnimation(0, 1 ,0, 1, width/2, height/2);
        scale.setDuration(900);
        mImage.startAnimation(scale);
    }

    @Override
    public void onImageLoadFailure(){
        //Deal with this later
        Log.e(TAG, "Image load failed");
    }
}
