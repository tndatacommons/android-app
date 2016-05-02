package org.tndata.android.compass.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.GoalAdapter;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.GoalContent;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.ImageHelper;


/**
 * Activity used to display a goal and a button for the user to accept.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class GoalActivity extends MaterialActivity implements GoalAdapter.GoalListener{
    //Argument keys; since this activity does NOT handle the addition ops, only
    //  the category and the behavior need to be passed
    public static final String CATEGORY_KEY = "org.tndata.compass.Goal.Category";
    public static final String GOAL_KEY = "org.tndata.compass.Goal.Goal";


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        CategoryContent category = getIntent().getParcelableExtra(CATEGORY_KEY);
        GoalContent goal = getIntent().getParcelableExtra(GOAL_KEY);

        setHeader(category);
        setAdapter(new GoalAdapter(this, this, goal));
        setColor(Color.parseColor(category.getColor()));
    }

    /**
     * Sets the header of the material activity.
     *
     * @param category the category whose tile is to be picked.
     */
    @SuppressWarnings("deprecation")
    private void setHeader(CategoryContent category){
        View header = inflateHeader(R.layout.header_tile);
        ImageView tile = (ImageView)header.findViewById(R.id.header_tile);

        int id = CompassUtil.getCategoryTileResId(category.getTitle());
        Bitmap image = BitmapFactory.decodeResource(getResources(), id);
        Bitmap circle = ImageHelper.getCircleBitmap(image, CompassUtil.getPixels(this, 200));
        tile.setImageBitmap(circle);
        image.recycle();
    }

    @Override
    public void acceptGoal(){
        //Set the result as OK and let ChooseGoalsActivity handle the networking
        //  and addition to the library
        setResult(RESULT_OK);
        finish();
    }
}
