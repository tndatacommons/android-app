package org.tndata.android.compass.activity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.Toast;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ChooseActionsAdapter;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.task.ActionLoaderTask;
import org.tndata.android.compass.task.AddActionTask;
import org.tndata.android.compass.task.DeleteActionTask;
import org.tndata.android.compass.ui.SpacingItemDecoration;
import org.tndata.android.compass.ui.parallaxrecyclerview.HeaderLayoutManagerFixed;
import org.tndata.android.compass.util.CompassTagHandler;
import org.tndata.android.compass.util.Constants;

import java.util.List;


/**
 * The ChooseActionsActivity is where a user selects Actions for a chosen Behavior.
 */
public class ChooseActionsActivity
        extends AppCompatActivity
        implements
                ActionLoaderTask.ActionLoaderListener,
                AddActionTask.AddActionTaskListener,
                DeleteActionTask.DeleteActionTaskListener,
                ChooseActionsAdapter.ChooseActionsListener,
                MenuItemCompat.OnActionExpandListener,
                SearchView.OnQueryTextListener,
                SearchView.OnCloseListener{

    private static final String TAG = "ChooseActionsActivity";

    private Toolbar mToolbar;
    private MenuItem mSearchItem;
    private SearchView mSearchView;

    private Goal mGoal;
    private ChooseActionsAdapter mAdapter;
    private View mHeaderView;
    private CompassApplication mApplication;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_actions);

        mApplication = (CompassApplication)getApplication();
        Behavior behavior = (Behavior)getIntent().getSerializableExtra("behavior");
        mGoal = (Goal)getIntent().getSerializableExtra("goal");
        Category category = (Category)getIntent().getSerializableExtra("category");

        List<Behavior> behaviors = mApplication.getUserData().getBehaviors();
        int index = behaviors.indexOf(behavior);
        if (index != -1){
            behavior = behaviors.get(index);
        }

        mToolbar = (Toolbar)findViewById(R.id.choose_actions_toolbar);
        mToolbar.setTitle(behavior.getTitle());
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mHeaderView = findViewById(R.id.choose_actions_material_view);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.choose_actions_list);
        HeaderLayoutManagerFixed manager = new HeaderLayoutManagerFixed(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(new SpacingItemDecoration(this, 10));
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);

        mAdapter = new ChooseActionsAdapter(this, this, mApplication, recyclerView, behavior);

        recyclerView.setAdapter(mAdapter);
        if (category != null && !category.getColor().isEmpty()) {
            mHeaderView.setBackgroundColor(Color.parseColor(category.getColor()));
            mToolbar.setBackgroundColor(Color.parseColor(category.getColor()));
        }

        new ActionLoaderTask(this).execute(mApplication.getToken(), String.valueOf(behavior.getId()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        mSearchItem = menu.findItem(R.id.filter);
        MenuItemCompat.setOnActionExpandListener(mSearchItem, this);

        mSearchView = (SearchView)mSearchItem.getActionView();
        mSearchView.setIconified(false);
        mSearchView.setOnCloseListener(this);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.clearFocus();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item){
        mSearchView.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item){
        mSearchView.setQuery("", false);
        mSearchView.clearFocus();
        return true;
    }

    @Override
    public boolean onClose(){
        mSearchItem.collapseActionView();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query){
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText){
        Log.d("Search", newText);
        mAdapter.filter(newText);
        return false;
    }

    /**
     * Checks whether the provided string has one of the following formats, X being a number:
     * <p/>
     * (XXX) XXX-XXX
     * XXX-XXX-XXXX
     *
     * @param resource the resource to be checked.
     * @return true if the resource is a phone number, false otherwise.
     */
    private boolean isPhoneNumber(String resource) {
        return resource.matches("[(][0-9]{3}[)] [0-9]{3}[-][0-9]{4}") ||
                resource.matches("[0-9]{3}[-][0-9]{3}[-][0-9]{4}");
    }

    @Override
    public void moreInfo(Action action){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChooseActionsActivity.this);
        if (!action.getHTMLMoreInfo().isEmpty()){
            builder.setMessage(Html.fromHtml(action.getHTMLMoreInfo(), null, new CompassTagHandler(this)));
        }
        else{
            builder.setMessage(action.getMoreInfo());
        }
        builder.setTitle(action.getTitle());
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void editReminder(Action action) {
        List<Action> actions = mApplication.getActions();

        Intent intent = new Intent(getApplicationContext(), TriggerActivity.class);
        intent.putExtra("goal", mGoal);
        //Need to pass the action that contains the trigger set by the user (if any), not the
        //  action in the master list, which likely won't contain that information.
        intent.putExtra("action", actions.get(actions.indexOf(action)));
        startActivity(intent);
    }

    @Override
    public void addAction(Action action){
        Toast.makeText(getApplicationContext(), getText(R.string.action_saving), Toast.LENGTH_SHORT).show();
        new AddActionTask(this, this, mGoal, action).execute();
    }

    @Override
    public void deleteAction(Action action){
        //Make sure we find the action that contains the user's mapping id.
        if (action.getMappingId() <= 0){
            for (Action a:mApplication.getActions()){
                if (action.getId() == a.getId()){
                    action.setMappingId(a.getMappingId());
                    break;
                }
            }
        }

        Log.e(TAG, "Deleting Action, id = " + action.getId() + ", user_action id = "
                + action.getMappingId() + ", " + action.getTitle());

        if (action.getMappingId() > 0){
            String actionMappingId = String.valueOf(action.getMappingId());
            new DeleteActionTask(this, this, actionMappingId).execute();

            // Remove from the application's collection
            mApplication.removeAction(action);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void doItNow(Action action){
        String resource = action.getExternalResource();
        //If a link
        if (resource.startsWith("http")){
            //If an app
            if (resource.startsWith("http://play.google.com/store/apps/") ||
                    resource.startsWith("https://play.google.com/store/apps/")){
                String id = resource.substring(resource.indexOf('/', 32));
                //Try, if the user does not have the store installed, launch as web link
                try{
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://" + id)));
                }
                catch (ActivityNotFoundException anfx){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(resource)));
                }
            }
            //Otherwise opened with the browser
            else{
                Uri uri = Uri.parse(resource);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        }
        //If a phone number
        else if (isPhoneNumber(resource)){
            //First of all, the number needs to be extracted from the resource
            String number = "";
            for (int i = 0; i < resource.length(); i++){
                char digit = resource.charAt(i);
                if (digit >= '0' && digit <= '9'){
                    number += digit;
                }
            }
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number)));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == Constants.VIEW_BEHAVIOR_REQUEST_CODE){
            setResult(resultCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void actionsLoaded(List<Action> actions){
        if (actions != null && !actions.isEmpty()){
            mAdapter.setActions(actions);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void actionAdded(Action action){
        Toast.makeText(getApplicationContext(),
                getString(R.string.action_added, action.getTitle()),
                Toast.LENGTH_SHORT).show();

        // Add to the application's collection
        mApplication.addAction(action);
        mAdapter.notifyDataSetChanged();

        // launch trigger stuff
        Intent intent = new Intent(getApplicationContext(), TriggerActivity.class);
        intent.putExtra("goal", mGoal);
        intent.putExtra("action", action);
        startActivity(intent);
    }

    @Override
    public void actionDeleted(){
        Toast.makeText(getApplicationContext(), getString(R.string.action_deleted), Toast.LENGTH_SHORT).show();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onScroll(float percentage, float offset){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            Drawable color = mToolbar.getBackground();
            color.setAlpha(Math.round(percentage*255));
            mToolbar.setBackground(color);
        }
        mHeaderView.setTranslationY(-offset*0.5f);
    }
}
