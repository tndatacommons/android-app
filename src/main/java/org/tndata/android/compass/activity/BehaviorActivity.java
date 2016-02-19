package org.tndata.android.compass.activity;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.BehaviorAdapter;
import org.tndata.android.compass.model.BehaviorContent;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.util.CompassUtil;


/**
 * Activity that displays a single behavior for the user to accept or dismiss.
 *
 * @author Ismael Alonso.
 * @version 1.0.0
 */
public class BehaviorActivity extends LibraryActivity implements BehaviorAdapter.BehaviorListener{
    //Argument keys; since this activity does NOT handle the addition ops, only
    //  the category and the behavior need to be passed
    public static final String CATEGORY_KEY = "org.tndata.compass.Behavior.Category";
    public static final String BEHAVIOR_KEY = "org.tndata.compass.Behavior.Behavior";


    private CategoryContent mCategory;
    private BehaviorContent mBehavior;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mCategory = (CategoryContent)getIntent().getSerializableExtra(CATEGORY_KEY);
        mBehavior = (BehaviorContent)getIntent().getSerializableExtra(BEHAVIOR_KEY);

        setHeader();
        setAdapter(new BehaviorAdapter(this, this, mCategory, mBehavior));
        setColor(Color.parseColor(mCategory.getColor()));
    }

    @SuppressWarnings("deprecation")
    private void setHeader(){
        FrameLayout header = (FrameLayout)inflateHeader(R.layout.header_choose_behaviors);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)header.getLayoutParams();
        params.height = CompassUtil.getScreenWidth(this)/3*2;
        header.setLayoutParams(params);
        RelativeLayout circle = (RelativeLayout)header.findViewById(R.id.choose_behaviors_circle);
        ImageView icon = (ImageView)header.findViewById(R.id.choose_behaviors_icon);

        GradientDrawable gradientDrawable = (GradientDrawable) circle.getBackground();
        if (mCategory != null && !mCategory.getSecondaryColor().isEmpty()){
            gradientDrawable.setColor(Color.WHITE);
        }
        else{
            gradientDrawable.setColor(Color.WHITE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            circle.setBackground(gradientDrawable);
        }
        else{
            circle.setBackgroundDrawable(gradientDrawable);
        }
        mBehavior.loadIconIntoView(icon);
    }

    @Override
    public void dismissBehavior(){
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void acceptBehavior(){
        //Set the result as OK and let ChooseBehaviorsActivity handle the networking
        //  and addition to the library
        setResult(RESULT_OK);
        finish();
    }
}
