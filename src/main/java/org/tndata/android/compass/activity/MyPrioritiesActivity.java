package org.tndata.android.compass.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;
import android.widget.ViewSwitcher;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.MyPrioritiesCategoryAdapter;


/**
 * Created by isma on 7/16/15.
 */
public class MyPrioritiesActivity extends ActionBarActivity{
    private ViewSwitcher mSwitcher;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_priorities);

        mSwitcher = (ViewSwitcher)findViewById(R.id.my_priorities_switcher);
        ListView categoryList = (ListView)findViewById(R.id.my_priorities_category_list);
        categoryList.setAdapter(new MyPrioritiesCategoryAdapter(getApplicationContext(),
                ((CompassApplication)getApplication()).getCategories()));
    }

    @Override
    public void onBackPressed(){
        if (mSwitcher.getDisplayedChild() != 0){
            mSwitcher.showPrevious();
        }
        else{
            super.onBackPressed();
        }
    }
}
