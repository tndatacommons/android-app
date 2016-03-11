package org.tndata.android.compass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.SearchAdapter;
import org.tndata.android.compass.model.SearchResult;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;

import java.util.ArrayList;

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
                HttpRequest.RequestCallback,
                Parser.ParserCallback,
                SearchAdapter.SearchAdapterListener{

    //Task request codes
    private static final int SEARCH_REQUEST_CODE = -1;


    private TextView mSearchHeader;
    private SearchAdapter mSearchAdapter;
    private String mLastSearch;
    private int mLastSearchRequestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        SearchView searchView = (SearchView)findViewById(R.id.search_search);
        mSearchHeader = (TextView)findViewById(R.id.search_message);
        RecyclerView searchList = (RecyclerView)findViewById(R.id.search_list);

        searchView.setOnQueryTextListener(this);
        mSearchAdapter = new SearchAdapter(this, this);
        searchList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        searchList.setAdapter(mSearchAdapter);

        mLastSearchRequestCode = SEARCH_REQUEST_CODE;
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
            mSearchAdapter.updateDataSet(new ArrayList<SearchResult>(), false);
            mLastSearchRequestCode++;
        }
        else{
            mLastSearchRequestCode = HttpRequest.get(this, API.getSearchUrl(newText));
        }
        return false;
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
            mSearchAdapter.updateDataSet(((ParserModels.SearchResultSet)result).results, true);
            mSearchHeader.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSearchResultSelected(SearchResult result){
        if (result.isGoal()){
            startActivity(new Intent(this, ChooseBehaviorsActivity.class)
                    .putExtra(ChooseBehaviorsActivity.GOAL_ID_KEY, result.getId()));
        }
    }

    @Override
    public void onCreateCustomGoalSelected(){
        /*startActivity(new Intent(this, CustomContentManagerActivity.class)
                .putExtra(CustomContentManagerActivity.CUSTOM_GOAL_TITLE_KEY, mLastSearch));
        finish();*/
    }
}
