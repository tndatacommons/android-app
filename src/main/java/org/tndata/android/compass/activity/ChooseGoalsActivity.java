package org.tndata.android.compass.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ChooseGoalsAdapter;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.GoalContent;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.CompassUtil;
import org.tndata.android.compass.util.ImageHelper;
import org.tndata.android.compass.util.NetworkRequest;

import java.util.List;


/**
 * The ChooseGoalsActivity is where a user selects Goals within a selected Category.
 *
 * @author Edited by Ismael Alonso
 * @version 2.0.0
 */
public class ChooseGoalsActivity
        extends LibraryActivity
        implements
                NetworkRequest.RequestCallback,
                Parser.ParserCallback,
                ChooseGoalsAdapter.ChooseGoalsListener,
                MenuItemCompat.OnActionExpandListener,
                SearchView.OnQueryTextListener,
                SearchView.OnCloseListener{

    //NOTE: This needs to be regular content because a user may dive down the library
    //  without selecting things. User content ain't available in that use case, but
    //  if it exists it can be retrieved from the UserData bundle
    public static final String CATEGORY_KEY = "org.tndata.compass.ChooseGoalsActivity.Category";

    private static final String TAG = "ChooseGoalsActivity";

    private CategoryContent mCategory;
    private ChooseGoalsAdapter mAdapter;

    //Request codes
    private int mGetGoalsRequestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mCategory = (CategoryContent)getIntent().getSerializableExtra(CATEGORY_KEY);

        FrameLayout header = (FrameLayout)inflateHeader(R.layout.header_choose_goals);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)header.getLayoutParams();
        params.height = CompassUtil.getScreenWidth(this)/3*2;
        header.setLayoutParams(params);
        ImageView tile = (ImageView)header.findViewById(R.id.choose_goals_tile);

        int id = CompassUtil.getCategoryTileResId(mCategory.getTitle());
        Bitmap image = BitmapFactory.decodeResource(getResources(), id);
        Bitmap circle = ImageHelper.getCircleBitmap(image, CompassUtil.getPixels(this, 200));
        tile.setImageBitmap(circle);
        image.recycle();

        mAdapter = new ChooseGoalsAdapter(this, this, mCategory);
        setAdapter(mAdapter);
        setFilter(mAdapter.getFilter());

        if (mCategory != null && !mCategory.getColor().isEmpty()){
            setColor(Color.parseColor(mCategory.getColor()));
        }

        loadGoals();
    }

    /**
     * Fires the goal loader task.
     */
    private void loadGoals(){
        mGetGoalsRequestCode = NetworkRequest.get(this, this, API.getGoalsUrl(mCategory), "");
    }

    @Override
    public void onGoalSelected(@NonNull GoalContent goal){
        if (goal.getBehaviorCount() > 0){
            startActivity(new Intent(this, ChooseBehaviorsActivity.class)
                    .putExtra(ChooseBehaviorsActivity.GOAL_KEY, goal)
                    .putExtra(ChooseBehaviorsActivity.CATEGORY_KEY, mCategory));
        }
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetGoalsRequestCode){
            Parser.parse(result, ParserModels.GoalContentResultSet.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, String message){

    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.GoalContentResultSet){
            List<GoalContent> goals = ((ParserModels.GoalContentResultSet)result).results;
            if (goals != null && !goals.isEmpty()){
                mAdapter.addGoals(goals);
            }
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.GoalContentResultSet){
            mAdapter.update();
        }
    }
}
