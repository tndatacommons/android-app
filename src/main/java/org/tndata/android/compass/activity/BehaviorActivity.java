package org.tndata.android.compass.activity;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.BehaviorAdapter;
import org.tndata.android.compass.model.BehaviorContent;
import org.tndata.android.compass.model.CategoryContent;


/**
 * Activity that displays a single behavior for the user to accept or dismiss.
 *
 * @author Ismael Alonso.
 * @version 1.0.0
 */
public class BehaviorActivity extends MaterialActivity implements BehaviorAdapter.BehaviorListener{
    //Argument keys; since this activity does NOT handle the addition ops, only
    //  the category and the behavior need to be passed
    public static final String CATEGORY_KEY = "org.tndata.compass.Behavior.Category";
    public static final String BEHAVIOR_KEY = "org.tndata.compass.Behavior.Behavior";


    private BehaviorContent mBehavior;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        CategoryContent category = (CategoryContent)getIntent().getSerializableExtra(CATEGORY_KEY);
        mBehavior = getIntent().getParcelableExtra(BEHAVIOR_KEY);

        setHeader();
        setAdapter(new BehaviorAdapter(this, this, category, mBehavior));
        setColor(Color.parseColor(category.getColor()));
    }

    @SuppressWarnings("deprecation")
    private void setHeader(){
        View header = inflateHeader(R.layout.header_icon);
        RelativeLayout circle = (RelativeLayout)header.findViewById(R.id.header_icon_circle);
        ImageView icon = (ImageView)header.findViewById(R.id.header_icon_icon);

        GradientDrawable gradientDrawable = (GradientDrawable) circle.getBackground();
        gradientDrawable.setColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            circle.setBackground(gradientDrawable);
        }
        else{
            circle.setBackgroundDrawable(gradientDrawable);
        }
        mBehavior.loadIconIntoView(icon);
    }

    @Override
    public void acceptBehavior(){
        //Set the result as OK and let ChooseBehaviorsActivity handle the networking
        //  and addition to the library
        setResult(RESULT_OK);
        finish();
    }
}
