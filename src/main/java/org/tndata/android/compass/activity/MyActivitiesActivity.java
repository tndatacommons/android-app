package org.tndata.android.compass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ChooseCategoryAdapter;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.model.UserCategory;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * Activity that displays a list of user-selected categories for the user to choose
 * and review actions under that category.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class MyActivitiesActivity
        extends AppCompatActivity
        implements
                HttpRequest.RequestCallback,
                Parser.ParserCallback,
                ChooseCategoryAdapter.ChooseCategoryAdapterListener{

    private RecyclerView mList;
    private ProgressBar mLoading;

    private List<TDCCategory> mCategoryList;
    private Map<Long, UserCategory> mUserCategoryMap;
    private int mGetCategoriesRC;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_activities);

        Toolbar toolbar = (Toolbar)findViewById(R.id.my_activities_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_white_24dp);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mList = (RecyclerView)findViewById(R.id.my_activities_category_list);
        mLoading = (ProgressBar)findViewById(R.id.my_activities_loading);

        mList.setLayoutManager(new LinearLayoutManager(this));

        mGetCategoriesRC = HttpRequest.get(this, API.getUserCategoriesUrl());
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetCategoriesRC){
            Parser.parse(result, ParserModels.UserCategoryResultSet.class, this);
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){

    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.UserCategoryResultSet){
            mCategoryList = new ArrayList<>();
            mUserCategoryMap = new HashMap<>();
            for (UserCategory userCategory:((ParserModels.UserCategoryResultSet)result).results){
                if (userCategory.getCategory().isSelectedByDefault()) continue;
                mCategoryList.add(userCategory.getCategory());
                mUserCategoryMap.put(userCategory.getContentId(), userCategory);
            }
        }

    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        if (result instanceof ParserModels.UserCategoryResultSet){
            mLoading.setVisibility(View.GONE);
            mList.setAdapter(new ChooseCategoryAdapter(this, this, mCategoryList));
        }
    }

    @Override
    public void onCategorySelected(TDCCategory category){
        UserCategory userCategory = mUserCategoryMap.get(category.getId());
        startActivity(new Intent(this, ReviewActionsActivity.class)
                .putExtra(ReviewActionsActivity.USER_CATEGORY_KEY, userCategory));
    }
}
