package org.tndata.android.compass.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.tndata.android.compass.R;
import org.tndata.android.compass.fragment.CategoryFragment;
import org.tndata.android.compass.fragment.MyGoalsFragment;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.ui.button.FloatingActionButton;

import java.util.ArrayList;

public class MainViewPagerAdapter extends FragmentStatePagerAdapter {
    private Context mContext;
    private ArrayList<Category> mCategories = new ArrayList<Category>();
    private FloatingActionButton mFloatingActionButton;


    public MainViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    public void setCategories(ArrayList<Category> categories) {
        mCategories = categories;
    }

    public void setFloatingActionButton(FloatingActionButton fab) {
        mFloatingActionButton = fab;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new MyGoalsFragment();
            ((MyGoalsFragment) fragment).setFloatingActionButton(mFloatingActionButton);
        } else {
            fragment = CategoryFragment.newInstance(mCategories
                    .get(position - 1));
            ((CategoryFragment) fragment).setFloatingActionButton(mFloatingActionButton);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return mCategories.size() + 1;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getResources().getString(R.string.main_tab_title)
                    .toUpperCase();
        } else {
            return mCategories.get(position - 1).getTitle().toUpperCase();
        }
    }

    public String getPositionImageUrl(int position) {
        if (position == 0) {
            return null;
        } else {
            return mCategories.get(position - 1).getImageUrl();
        }
    }

    public int getCategoryPosition(Category category) {
        return mCategories.indexOf(category);
    }

}
