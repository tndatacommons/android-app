package org.tndata.android.compass.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
public class BadgeActivity extends AppCompatActivity implements ImageLoader.ImageLoaderCallback{
    public static final String BADGE_KEY = "org.tndata.compass.BadgeActivity.Badge";


    private View mImageFrame;
    private ImageView mImage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge);

        Badge badge = getIntent().getParcelableExtra(BADGE_KEY);

        mImageFrame = findViewById(R.id.badge_image_frame);
        mImage = (ImageView)findViewById(R.id.badge_image);
        TextView name = (TextView)findViewById(R.id.badge_name);
        TextView description = (TextView)findViewById(R.id.badge_description);

        ImageLoader.loadBitmap(mImage, badge.getImageUrl(), this);
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
        int width = mImageFrame.getWidth();
        int height = mImageFrame.getHeight();
        int dimension = (int)((width < height ? width : height)*0.8);

        mImage.startAnimation(new ScaleAnimation(0, dimension, 0, dimension));
    }

    @Override
    public void onImageLoadFailure(){
        //Deal with this later
    }
}
