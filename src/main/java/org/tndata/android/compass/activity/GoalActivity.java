package org.tndata.android.compass.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.GoalAdapter;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.model.TDCGoal;
import org.tndata.android.compass.tour.CoachMark;
import org.tndata.android.compass.tour.Tour;
import org.tndata.android.compass.util.ImageLoader;

import java.util.LinkedList;
import java.util.Queue;


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


    private GoalAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        TDCCategory category = getIntent().getParcelableExtra(CATEGORY_KEY);
        TDCGoal goal = getIntent().getParcelableExtra(GOAL_KEY);

        mAdapter = new GoalAdapter(this, this, category, goal);
        setHeader(category);
        setAdapter(mAdapter);
        setColor(Color.parseColor(category.getColor()));

        getRecyclerView().scrollToPosition(1);
    }

    /**
     * Sets the header of the material activity.
     *
     * @param category the category whose tile is to be picked.
     */
    @SuppressWarnings("deprecation")
    private void setHeader(TDCCategory category){
        View header = inflateHeader(R.layout.header_tile);
        ImageView tile = (ImageView)header.findViewById(R.id.header_tile);

        ImageLoader.Options options = new ImageLoader.Options()
                .setUseDefaultPlaceholder(false)
                .setCropToCircle(true);
        ImageLoader.loadBitmap(tile, category.getIconUrl(), options);
    }

    private void fireTour(View target){
        Queue<CoachMark> marks = new LinkedList<>();
        for (Tour.Tooltip tooltip:Tour.getTooltipsFor(Tour.Section.GOAL)){
            switch (tooltip){
                case GOAL_GENERAL:
                    marks.add(new CoachMark().setOverlayColor(getResources().getColor(R.color.tour_overlay))
                            .setCutawayType(CoachMark.CutawayType.SQUARE)
                            .setTarget(target)
                            .setTooltip(Tour.Tooltip.GOAL_GENERAL));
                    break;
            }
        }
        Tour.display(this, marks, null);
    }

    @Override
    public void onGoalCardLoaded(){
        fireTour(mAdapter.getSignMeUpButton());
    }

    @Override
    public void acceptGoal(){
        //Set the result as OK and let ChooseGoalsActivity handle the networking
        //  and addition to the library
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void declineGoal(){
        //Set the result as CANCELLED
        setResult(RESULT_CANCELED);
        finish();
    }
}
