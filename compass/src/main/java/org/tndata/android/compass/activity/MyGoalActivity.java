package org.tndata.android.compass.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.MyGoalAdapter;
import org.tndata.android.compass.model.CustomAction;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.model.UserGoal;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.ImageLoader;
import org.tndata.android.compass.util.ItemSpacing;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * Activity used to display a goal currently selected by the user.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class MyGoalActivity
        extends MaterialActivity
        implements
                HttpRequest.RequestCallback,
                Parser.ParserCallback,
                MyGoalAdapter.Listener{

    public static final int GOAL_REMOVED_RC = 3836;

    private static final String USER_GOAL_KEY = "org.tndata.compass.MyGoal.UserGoal";
    private static final String USER_GOAL_ID_KEY = "org.tndata.compass.MyGoal.UserGoalId";

    public static final String REMOVED_GOAL_KEY = "org.tndata.compass.MyGoal.RemovedGoal";


    /**
     * Gets the Intent used to launch this activity when a goal is unavailable.
     *
     * @param context a reference to the context.
     * @param userGoalId the id of the user goal to be loaded.
     * @return the intent that launches this activity correctly.
     */
    public static Intent getIntent(Context context, long userGoalId){
        return new Intent(context, MyGoalActivity.class)
                .putExtra(USER_GOAL_ID_KEY, userGoalId);
    }

    /**
     * Gets the Intent used to launch this activity when a goal is available.
     *
     * @param context a reference to the context.
     * @param userGoal the id of the user goal to be loaded.
     * @return the intent that launches this activity correctly.
     */
    public static Intent getIntent(Context context, UserGoal userGoal){
        return new Intent(context, MyGoalActivity.class)
                .putExtra(USER_GOAL_KEY, userGoal);
    }


    private CompassApplication mApp;
    private MyGoalAdapter mAdapter;

    private UserGoal mUserGoal;

    //Poor man's "time in activity" timer
    private long mStartTime;

    //Request codes
    private int mGetUserGoalRC;
    private int mGetCategoryRC;
    private int mGetCustomActionsRC;
    private int mPostCustomActionRC;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mApp = (CompassApplication)getApplication();

        getRecyclerView().addItemDecoration(new ItemSpacing(this, 8));

        UserGoal userGoal = getIntent().getParcelableExtra(USER_GOAL_KEY);
        if (userGoal == null){
            long userGoalId = getIntent().getLongExtra(USER_GOAL_ID_KEY, -1);
            mGetUserGoalRC = HttpRequest.get(this, API.URL.getUserGoal(userGoalId));
        }
        else{
            setGoal(userGoal);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // We want to get a rough idea of the amount of time the user spends in this
        // activity prior to tapping "Got It". Therefore, we'll grab a timestamp here,
        // then again when they tap the button, and log the difference.
        mStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        long endTime = System.currentTimeMillis();
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName(mUserGoal.getTitle())
                .putContentType("UserGoal")
                .putContentId("" + mUserGoal.getId())
                .putCustomAttribute("Duration", (endTime - mStartTime) / 1000));
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_my_goal, menu);
        return true;
    }

    @Override
    protected boolean menuItemSelected(MenuItem item){
        if (item.getItemId() == R.id.my_goal_remove){
            removeGoal();
        }
        return super.menuItemSelected(item);
    }

    @Override
    protected void onHomeTapped(){
        finish();
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetUserGoalRC){
            Parser.parse(result, UserGoal.class, this);
        }
        else if (requestCode ==mGetCategoryRC){
            Parser.parse(result, TDCCategory.class, this);
        }
        else if (requestCode == mGetCustomActionsRC){
            Parser.parse(result, ParserModels.CustomActionResultSet.class, this);
        }
        else if (requestCode == mPostCustomActionRC){
            Parser.parse(result, CustomAction.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        if (requestCode == mGetUserGoalRC){
            displayMessage("Couldn't load goal data...");
        }
        else if (requestCode == mGetCategoryRC){
            setCategory(null);
        }
        else if (requestCode == mGetCustomActionsRC){
            mAdapter.fetchCustomActionsFailed();
        }
        else if (requestCode == mPostCustomActionRC){
            mAdapter.addCustomActionFailed();
        }
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        //no-op
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof UserGoal){
            setGoal((UserGoal)result);
        }
        else if (result instanceof TDCCategory){
            setCategory((TDCCategory)result);
        }
        else if (result instanceof ParserModels.CustomActionResultSet){
            mAdapter.setCustomActions(((ParserModels.CustomActionResultSet)result).results);
        }
        else if (result instanceof CustomAction){
            CustomAction customAction = (CustomAction)result;
            mAdapter.customActionAdded(customAction);
            Intent intent = new Intent(MyGoalActivity.this, TriggerActivity.class)
                    .putExtra(TriggerActivity.GOAL_TITLE_KEY, mUserGoal.getTitle())
                    .putExtra(TriggerActivity.ACTION_KEY, customAction);
            startActivity(intent);
        }
    }

    @Override
    public void onParseFailed(int requestCode){
        if (mAdapter == null){
            displayMessage("Couldn't load goal data...");
        }
        else if (mAdapter.areCustomActionsSet()){
            mAdapter.addCustomActionFailed();
        }
        else{
            mAdapter.fetchCustomActionsFailed();
        }
    }

    /**
     * Initializes the activity with the provided goal.
     *
     * @param userGoal the loaded goal.
     */
    private void setGoal(UserGoal userGoal){
        mUserGoal = userGoal;

        long categoryId = mUserGoal.getPrimaryCategoryId();
        TDCCategory category = mApp.getAvailableCategories().get(categoryId);
        if (category == null){
            mGetCategoryRC = HttpRequest.get(this, API.URL.getCategory(categoryId));
        }
        else{
            setCategory(category);
        }

        mGetCustomActionsRC = HttpRequest.get(this, API.URL.getCustomActions(mUserGoal));
    }

    private void setCategory(TDCCategory category){
        if (category != null){
            setColor(category.getColorInt());
            View header = inflateHeader(R.layout.header_hero);
            ImageView image = (ImageView) header.findViewById(R.id.header_hero_image);
            if (category.getImageUrl() == null || category.getImageUrl().isEmpty()){
                image.setImageResource(R.drawable.compass_master_illustration);
            }
            else{
                ImageLoader.Options options = new ImageLoader.Options()
                        .setPlaceholder(R.drawable.compass_master_illustration);
                ImageLoader.loadBitmap(image, category.getImageUrl(), options);
            }

            mAdapter = new MyGoalAdapter(this, this, mUserGoal, category);
            setAdapter(mAdapter);
        }
    }

    private void removeGoal(){
        HttpRequest.delete(null, API.URL.deleteGoal(mUserGoal));
        setResult(GOAL_REMOVED_RC, new Intent().putExtra(REMOVED_GOAL_KEY, mUserGoal));
        finish();
    }

    @Override
    public void retryCustomActionLoad(){
        mGetCustomActionsRC = HttpRequest.get(this, API.URL.getCustomActions(mUserGoal));
    }

    @Override
    public void addCustomAction(String title){
        mPostCustomActionRC = HttpRequest.post(
                this, API.URL.postCustomAction(), API.BODY.postPutCustomAction(title, mUserGoal)
        );
    }

    @Override
    public void saveCustomAction(CustomAction action){
        HttpRequest.put(
                null, API.URL.putCustomAction(action),
                API.BODY.postPutCustomAction(action.getTitle(), mUserGoal)
        );
    }

    @Override
    public void deleteCustomAction(CustomAction action){
        HttpRequest.delete(null, API.URL.deleteAction(action));
    }

    @Override
    public void editTrigger(CustomAction action){
        startActivity(new Intent(this, TriggerActivity.class)
                .putExtra(TriggerActivity.GOAL_TITLE_KEY, mUserGoal.getTitle())
                .putExtra(TriggerActivity.ACTION_KEY, action));
    }
}
