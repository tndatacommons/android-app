package org.tndata.android.compass.activity;

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
public class AwardActivity extends AppCompatActivity implements ImageLoader.ImageLoaderCallback{
    private static final String TAG = "AwardActivity";

    public static final String BADGE_KEY = "org.tndata.compass.AwardActivity.Badge";


    private View mImageFrame;
    private ImageView mImage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_award);

        Badge badge = getIntent().getParcelableExtra(BADGE_KEY);

        mImageFrame = findViewById(R.id.award_image_frame);
        mImage = (ImageView)findViewById(R.id.award_image);
        TextView name = (TextView)findViewById(R.id.award_name);
        TextView description = (TextView)findViewById(R.id.award_description);

        ImageLoader.Options options = new ImageLoader.Options().setUseDefaultPlaceholder(false);
        ImageLoader.loadBitmap(mImage, badge.getImageUrl(), options, this);
        //I give up... I know these fields are never going to be null though.
        if (name != null){
            name.setText(badge.getName());
        }
        if (description != null){
            description.setText(badge.getDescription());
        }
    }

    @Override
    public void onImageLoadSuccess(){
        Log.i(TAG, "Image loaded");

        int width = mImageFrame.getWidth();
        int height = mImageFrame.getHeight();

        Animation scale = new ScaleAnimation(0, 1 ,0, 1, width/2, height/2);
        scale.setDuration(400);
        mImage.startAnimation(scale);
    }

    @Override
    public void onImageLoadFailure(){
        //Deal with this later
        Log.e(TAG, "Image load failed");
    }
}
