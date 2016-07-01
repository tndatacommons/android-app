package org.tndata.android.compass.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ChooseGoalsAdapter;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.model.TDCGoal;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.ImageLoader;

import java.util.List;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * The ChooseGoalsActivity is where a user selects Goals within a selected Category.
 *
 * @author Edited by Ismael Alonso
 * @version 2.0.0
 */
public class ChooseGoalsActivity
        extends MaterialActivity
        implements
                View.OnClickListener,
                DialogInterface.OnCancelListener,
                HttpRequest.RequestCallback,
                Parser.ParserCallback,
                ChooseGoalsAdapter.ChooseGoalsListener{

    //NOTE: This needs to be regular content because a user may dive down the library
    //  without selecting things. User content ain't available in that use case, but
    //  if it exists it can be retrieved from the UserData bundle
    public static final String CATEGORY_KEY = "org.tndata.compass.ChooseGoalsActivity.Category";

    private static final String TAG = "ChooseGoalsActivity";

    //Activity request codes
    private static final int GOAL_ACTIVITY_RC = 5173;


    public CompassApplication mApplication;

    private TDCCategory mCategory;
    private TDCGoal mSelectedGoal;
    private ChooseGoalsAdapter mAdapter;

    //Request codes and urls
    private int mGetGoalsRC;
    private int mPostGoalRC;
    private String mGetGoalsNextUrl;

    private AlertDialog mShareDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mApplication = (CompassApplication)getApplication();

        //Pull the content
        mCategory = getIntent().getParcelableExtra(CATEGORY_KEY);

        //Set up the loading process and the adapter
        mGetGoalsNextUrl = API.getGoalsUrl(mCategory);
        mAdapter = new ChooseGoalsAdapter(this, this, mCategory);

        setColor(Color.parseColor(mCategory.getColor()));
        setHeader();
        setAdapter(mAdapter);
    }

    private void setHeader(){
        View header = inflateHeader(R.layout.header_hero);
        ImageView hero = (ImageView)header.findViewById(R.id.header_hero_image);
        if (mCategory.getImageUrl() != null && !mCategory.getImageUrl().isEmpty()){
            ImageLoader.Options options = new ImageLoader.Options()
                    .setPlaceholder(R.drawable.compass_master_illustration);
            ImageLoader.loadBitmap(hero, mCategory.getImageUrl(), options);
        }
        else{
            hero.setImageResource(R.drawable.compass_master_illustration);
        }
    }

    @Override
    public void onGoalSelected(@NonNull TDCGoal goal){
        Log.i(TAG, "Selected: " + goal.toString());
        mSelectedGoal = goal;
        startActivityForResult(new Intent(this, GoalActivity.class)
                .putExtra(GoalActivity.GOAL_KEY, goal)
                .putExtra(GoalActivity.CATEGORY_KEY, mCategory), GOAL_ACTIVITY_RC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == GOAL_ACTIVITY_RC && resultCode == RESULT_OK){
            Log.i(TAG, "Accepted: " + mSelectedGoal.toString());
            mPostGoalRC = HttpRequest.post(this, API.getPostGoalUrl(mSelectedGoal),
                    API.getPostGoalBody(mCategory));

            ViewGroup rootView = (ViewGroup)findViewById(android.R.id.content);
            LayoutInflater inflater = LayoutInflater.from(this);
            View mDialogRootView = inflater.inflate(R.layout.dialog_enrollment, rootView, false);
            mDialogRootView.findViewById(R.id.dialog_enrollment_ok).setOnClickListener(this);

            mShareDialog = new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setView(mDialogRootView)
                    .setOnCancelListener(this)
                    .create();
            mShareDialog.show();

            setResult(RESULT_OK);
        }
        else if (resultCode == RESULT_CANCELED){
            Log.i(TAG, "Rejected: " + mSelectedGoal.toString());
            mSelectedGoal = null;
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.dialog_enrollment_ok:
                mShareDialog.cancel();
                break;
        }
    }

    @Override
    public void onCancel(DialogInterface dialog){
        dismiss();
    }

    private void dismiss(){
        mAdapter.remove(mSelectedGoal);
        mSelectedGoal = null;
        if (mAdapter.isEmpty()){
            finish();
        }
    }

    @Override
    public void loadMore(){
        if (API.STAGING && mGetGoalsNextUrl.startsWith("https")){
            mGetGoalsNextUrl = mGetGoalsNextUrl.replaceFirst("s", "");
        }
        mGetGoalsRC = HttpRequest.get(this, mGetGoalsNextUrl);
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetGoalsRC){
            Parser.parse(result, ParserModels.GoalContentResultSet.class, this);
        }
        else if (requestCode == mPostGoalRC){
            Log.i(TAG, "Goal POSTed successfully");
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        if (requestCode == mGetGoalsRC){
            Log.e(TAG, "GET goals failed");
            mAdapter.displayError("Couldn't load goals");
        }
        else if (requestCode == mPostGoalRC){
            Log.e(TAG, "POST goal failed");
        }
        Log.e(TAG, error.toString());
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){

    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.GoalContentResultSet){
            ParserModels.GoalContentResultSet set = (ParserModels.GoalContentResultSet)result;
            mGetGoalsNextUrl = set.next;
            List<TDCGoal> goals = set.results;
            if (goals != null && !goals.isEmpty()){
                mAdapter.add(goals, mGetGoalsNextUrl != null);
            }
            else{
                mAdapter.displayError("You have selected all content");
            }
        }
    }
}
