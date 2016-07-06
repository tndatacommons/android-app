package org.tndata.android.compass.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.SearchAdapter;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.model.TDCGoal;
import org.tndata.android.compass.model.SearchResult;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;

import java.util.ArrayList;
import java.util.List;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * Performs searches and displays results.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class SearchActivity
        extends AppCompatActivity
        implements
                SearchView.OnQueryTextListener,
                View.OnClickListener,
                DialogInterface.OnCancelListener,
                HttpRequest.RequestCallback,
                Parser.ParserCallback,
                SearchAdapter.SearchAdapterListener{

    //Request codes
    private static final int GOAL_RC = 1528;
    private static final int CUSTOM_GOAL_RC = 4576;


    private ProgressBar mLoading;
    private TextView mSearchHeader;
    private Button mCreateGoal;
    private SearchAdapter mSearchAdapter;
    private String mLastSearch;
    private int mLastSearchRequestCode;

    private TDCCategory mCategory;
    private TDCGoal mGoal;

    private AlertDialog mFeedbackDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mLoading = (ProgressBar)findViewById(R.id.search_loading);
        SearchView searchView = (SearchView)findViewById(R.id.search_search);
        mSearchHeader = (TextView)findViewById(R.id.search_message);
        RecyclerView searchList = (RecyclerView)findViewById(R.id.search_list);
        mCreateGoal = (Button)findViewById(R.id.search_create);

        assert searchView != null;
        searchView.setOnQueryTextListener(this);
        mCreateGoal.setOnClickListener(this);
        mSearchAdapter = new SearchAdapter(this, this);
        assert searchList != null;
        searchList.setLayoutManager(new LinearLayoutManager(this));
        searchList.setAdapter(mSearchAdapter);

        mLastSearchRequestCode = -1;
    }

    @Override
    public boolean onQueryTextSubmit(String query){
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText){
        HttpRequest.cancel(mLastSearchRequestCode);
        mLastSearch = newText;
        if (newText.equals("")){
            mSearchHeader.setVisibility(View.INVISIBLE);
            mCreateGoal.setVisibility(View.GONE);
            mSearchAdapter.updateDataSet(new ArrayList<SearchResult>());
            mLastSearchRequestCode++;
            mLoading.setVisibility(View.INVISIBLE);
        }
        else{
            mLoading.setVisibility(View.VISIBLE);
            mLastSearchRequestCode = HttpRequest.get(this, API.getSearchUrl(newText));
        }
        return false;
    }

    @Override
    public void onClick(View v){
        if (v.getId() == R.id.dialog_enrollment_ok){
            if (mFeedbackDialog != null){
                mFeedbackDialog.cancel();
            }
        }
        else{
            Intent custom = new Intent(this, CustomContentActivity.class)
                    .putExtra(CustomContentActivity.CUSTOM_GOAL_TITLE_KEY, mLastSearch);
            startActivityForResult(custom, CUSTOM_GOAL_RC);
        }
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mLastSearchRequestCode){
            Parser.parse(result, ParserModels.SearchResultSet.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){

    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){

    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.SearchResultSet){
            List<SearchResult> results = ((ParserModels.SearchResultSet)result).results;
            mSearchAdapter.updateDataSet(results);
            mLoading.setVisibility(View.INVISIBLE);
            mSearchHeader.setVisibility(View.VISIBLE);
            mCreateGoal.setVisibility(View.VISIBLE);
            if (results.isEmpty()){
                mSearchHeader.setText(R.string.search_header_empty);
            }
            else{
                mSearchHeader.setText(R.string.search_header);
            }
        }
    }

    @Override
    public void onSearchResultSelected(SearchResult result){
        if (result.isGoal()){
            mCategory = null;
            CompassApplication app = (CompassApplication)getApplication();
            for (long categoryId:result.getGoal().getCategoryIdSet()){
                mCategory = app.getPublicCategories().get(categoryId);
                if (mCategory != null){
                    break;
                }
            }

            if (mCategory != null){
                mGoal = result.getGoal();
                Intent intent = new Intent(this, GoalActivity.class)
                        .putExtra(GoalActivity.CATEGORY_KEY, mCategory)
                        .putExtra(GoalActivity.GOAL_KEY, mGoal);
                startActivityForResult(intent, GOAL_RC);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == GOAL_RC && resultCode == RESULT_OK){
            HttpRequest.post(null, API.getPostGoalUrl(mGoal), API.getPostGoalBody(mCategory));

            ViewGroup rootView = (ViewGroup)findViewById(android.R.id.content);
            LayoutInflater inflater = LayoutInflater.from(this);
            View dialogRootView = inflater.inflate(R.layout.dialog_enrollment, rootView, false);
            dialogRootView.findViewById(R.id.dialog_enrollment_ok).setOnClickListener(this);

            mFeedbackDialog = new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setView(dialogRootView)
                    .setOnCancelListener(this)
                    .create();
            mFeedbackDialog.show();
        }
        else if (requestCode == CUSTOM_GOAL_RC){
            setResult(resultCode, data);
            finish();
        }
        else{
            mCategory = null;
            mGoal = null;
        }
    }

    @Override
    public void onCancel(DialogInterface dialog){
        finish();
    }
}
