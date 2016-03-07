package org.tndata.android.compass.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ChooseBehaviorsAdapter;
import org.tndata.android.compass.model.BehaviorContent;
import org.tndata.android.compass.model.CategoryContent;
import org.tndata.android.compass.model.GoalContent;
import org.tndata.android.compass.model.UserBehavior;
import org.tndata.android.compass.model.UserGoal;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.NetworkRequest;

import java.util.List;


/**
 * The ChooseBehaviorsActivity is where a user selects Behaviors for a chosen Goal.
 *
 * @author Edited by Ismael Alonso
 * @version 2.0.0
 */
public class ChooseBehaviorsActivity
        extends MaterialActivity
        implements
                View.OnClickListener,
                DialogInterface.OnCancelListener,
                NetworkRequest.RequestCallback,
                Parser.ParserCallback,
                ChooseBehaviorsAdapter.ChooseBehaviorsListener{

    //Bundle keys
    //NOTE: These need to be regular content because a user may dive down the library
    //  without selecting things. User content ain't available in that use case, but
    //  if it exists it can be retrieved from the UserData bundle
    public static final String CATEGORY_KEY = "org.tndata.compass.ChooseBehaviors.Category";
    public static final String GOAL_KEY = "org.tndata.compass.ChooseBehaviors.Goal";

    //Activity tag
    private static final String TAG = "ChooseBehaviorsActivity";

    //Activity request codes
    private static final int BEHAVIOR_ACTIVITY_RQ = 5469;


    public CompassApplication mApplication;

    private CategoryContent mCategory;
    private GoalContent mGoal;
    private BehaviorContent mSelectedBehavior;
    private ChooseBehaviorsAdapter mAdapter;

    private AlertDialog mShareDialog;

    //Network request codes and urls
    private int mGetBehaviorsRequestCode;
    private int mPostBehaviorRequestCode;
    private String mGetBehaviorsNextUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mApplication = (CompassApplication)getApplication();

        //Pull the goal
        mGoal = (GoalContent)getIntent().getSerializableExtra(GOAL_KEY);
        mCategory = (CategoryContent)getIntent().getSerializableExtra(CATEGORY_KEY);

        mGetBehaviorsNextUrl = API.getBehaviorsUrl(mGoal);
        mAdapter = new ChooseBehaviorsAdapter(this, this, mCategory, mGoal);

        setHeader();
        setAdapter(mAdapter);
        if (mCategory != null && !mCategory.getColor().isEmpty()){
            setColor(Color.parseColor(mCategory.getColor()));
        }

        mSelectedBehavior = null;
    }

    @SuppressWarnings("deprecation")
    private void setHeader(){
        View header = inflateHeader(R.layout.header_icon);
        RelativeLayout circle = (RelativeLayout)header.findViewById(R.id.header_icon_circle);
        ImageView icon = (ImageView)header.findViewById(R.id.header_icon_icon);

        GradientDrawable gradientDrawable = (GradientDrawable) circle.getBackground();
        if (mCategory != null && !mCategory.getSecondaryColor().isEmpty()){
            gradientDrawable.setColor(Color.parseColor(mCategory.getSecondaryColor()));
        }
        else{
            gradientDrawable.setColor(getResources().getColor(R.color.grow_accent));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            circle.setBackground(gradientDrawable);
        }
        else{
            circle.setBackgroundDrawable(gradientDrawable);
        }
        mGoal.loadIconIntoView(icon);
    }

    @Override
    public void onBehaviorSelected(BehaviorContent behavior){
        if (mSelectedBehavior == null){
            Log.d(TAG, "Selected behavior: " + behavior);
            mSelectedBehavior = behavior;
            startActivityForResult(new Intent(this, BehaviorActivity.class)
                    .putExtra(BehaviorActivity.CATEGORY_KEY, mCategory)
                    .putExtra(BehaviorActivity.BEHAVIOR_KEY, behavior), BEHAVIOR_ACTIVITY_RQ);
        }
    }

    @Override
    public void loadMore(){
        if (API.STAGING && mGetBehaviorsNextUrl.startsWith("https")){
            mGetBehaviorsNextUrl = mGetBehaviorsNextUrl.replaceFirst("s", "");
        }
        mGetBehaviorsRequestCode = NetworkRequest.get(this, this, mGetBehaviorsNextUrl,
                mApplication.getToken());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == BEHAVIOR_ACTIVITY_RQ && resultCode == RESULT_OK){
            mPostBehaviorRequestCode = NetworkRequest.post(this, this,
                    API.getPostBehaviorUrl(), mApplication.getToken(),
                    API.getPostBehaviorBody(mSelectedBehavior, mGoal, mCategory));

            ViewGroup rootView = (ViewGroup)findViewById(android.R.id.content);
            LayoutInflater inflater = LayoutInflater.from(this);
            View mDialogRootView = inflater.inflate(R.layout.dialog_library_behavior, rootView, false);
            mDialogRootView.findViewById(R.id.dialog_behavior_activities).setOnClickListener(this);
            mDialogRootView.findViewById(R.id.dialog_behavior_ok).setOnClickListener(this);

            mShareDialog = new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setView(mDialogRootView)
                    .setOnCancelListener(this)
                    .create();
            mShareDialog.show();
        }
        else if (resultCode == RESULT_CANCELED){
            mSelectedBehavior = null;
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.dialog_behavior_activities:
                showActivities();
            case R.id.dialog_behavior_ok:
                mShareDialog.cancel();
                break;
        }
    }

    @Override
    public void onCancel(DialogInterface dialog){
        dismiss();
    }

    private void showActivities(){
        UserBehavior userBehavior = mApplication.getUserData().getBehavior(mSelectedBehavior);
        UserGoal userGoal = mApplication.getUserData().getGoal(mGoal);
        startActivity(new Intent(this, ReviewActionsActivity.class)
                .putExtra(ReviewActionsActivity.USER_GOAL_KEY, userGoal)
                .putExtra(ReviewActionsActivity.USER_BEHAVIOR_KEY, userBehavior));
    }

    private void dismiss(){
        Log.d("ChooseBehaviorsActivity", "dismiss() called");
        mAdapter.remove(mSelectedBehavior);
        mSelectedBehavior = null;
        if (mAdapter.isEmpty()){
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetBehaviorsRequestCode){
            Parser.parse(result, ParserModels.BehaviorContentResultSet.class, this);
        }
        else if (requestCode == mPostBehaviorRequestCode){
            Parser.parse(result, UserBehavior.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, String message){
        if (requestCode == mGetBehaviorsRequestCode){
            mAdapter.displayError("Couldn't load behaviors");
        }
        else if (requestCode == mPostBehaviorRequestCode){
            if (mShareDialog != null){
                ((ViewSwitcher)mShareDialog.findViewById(R.id.dialog_behavior_switcher)).showNext();
            }
        }
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof UserBehavior){
            UserBehavior userBehavior = (UserBehavior)result;
            Log.d(TAG, "(Post) " + userBehavior.toString());

            mApplication.getUserData().addCategory(userBehavior.getParentUserCategory());
            mApplication.getUserData().addGoal(userBehavior.getParentUserGoal());
            mApplication.getUserData().addBehavior(userBehavior);
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.BehaviorContentResultSet){
            ParserModels.BehaviorContentResultSet set = (ParserModels.BehaviorContentResultSet)result;
            mGetBehaviorsNextUrl = set.next;
            List<BehaviorContent> behaviorList = set.results;
            //If the list isn't empty
            if (!behaviorList.isEmpty()){
                //Add the behaviors to the adapter
                mAdapter.add(behaviorList, mGetBehaviorsNextUrl != null);
            }
            else{
                //If the list is empty AND the adapter has no behaviors, then the
                //  user has already selected all of the behaviors; let him know
                //  through the error channel
                if (mAdapter.isEmpty()){
                    mAdapter.displayError("You have already selected all behaviors");
                }
            }
        }
        else if (result instanceof UserBehavior){
            if (mShareDialog != null){
                ((ViewSwitcher)mShareDialog.findViewById(R.id.dialog_behavior_switcher)).showNext();
            }
        }
    }
}
