package org.tndata.android.compass.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ShareEvent;

import org.tndata.android.compass.R;
import org.tndata.android.compass.databinding.ActivityBadgeBinding;
import org.tndata.android.compass.model.Badge;
import org.tndata.android.compass.util.ImageLoader;


/**
 * Activity used to display a badge.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class BadgeActivity extends AppCompatActivity implements ImageLoader.Callback{
    private static final String TAG = "BadgeActivity";

    public static final String BADGE_KEY = "org.tndata.compass.BadgeActivity.Badge";

    
    private ActivityBadgeBinding mBinding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_badge);

        mBinding.setActivity(this);
        mBinding.setBadge((Badge)getIntent().getParcelableExtra(BADGE_KEY));

        ImageLoader.Options options = new ImageLoader.Options().setUseDefaultPlaceholder(false);
        ImageLoader.loadBitmap(mBinding.badgeImage, mBinding.getBadge().getImageUrl(), options, this);
    }

    /**
     * Called when the share button is clicked.
     */
    public void onShareClick(){
        String content = getString(R.string.badge_share, mBinding.getBadge().getName());

        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
        startActivity(Intent.createChooser(shareIntent, "Share via"));

        // Log the attempt to share the badge info.
        Answers.getInstance().logShare(new ShareEvent()
                .putContentType("Badge")
                .putContentName(content)
                .putContentId("" + mBinding.getBadge().getId()));
    }

    @Override
    public void onImageLoadSuccess(){
        Log.i(TAG, "Image loaded");

        // We need the bitmap images size here, because the view will be
        // 0x0 until after the animation.
        Bitmap imageBitmap = ((BitmapDrawable)mBinding.badgeImage.getDrawable()).getBitmap();
        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();
        Log.d(TAG, "Image size: (" + width + ", " + height + ")");

        Animation scale = new ScaleAnimation(0, 1 ,0, 1, width/2, height/2);
        scale.setDuration(900);
        mBinding.badgeImage.startAnimation(scale);
    }

    @Override
    public void onImageLoadFailure(){
        //Deal with this later
        Log.e(TAG, "Image load failed");
    }
}
