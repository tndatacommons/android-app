package org.tndata.android.compass.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.MyPrioritiesCategoryAdapter;
import org.tndata.android.compass.model.Category;


/**
 * Created by isma on 7/17/15.
 */
public class MyPrioritiesCategoriesFragment
        extends Fragment
        implements
            AdapterView.OnItemClickListener{

    private OnCategorySelectedListener mListener;
    private ListView mCategoryList;


    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        //This makes sure that the container activity has implemented the callback
        //  interface. If not, it throws an exception.
        try{
            mListener = (OnCategorySelectedListener)activity;
        }
        catch (ClassCastException ccx){
            throw new ClassCastException(activity.toString()
                    + " must implement OnCategorySelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_my_priorities_categories, container, false);

        mCategoryList = (ListView)rootView.findViewById(R.id.my_priorities_category_list);
        mCategoryList.setAdapter(new MyPrioritiesCategoryAdapter(getActivity().getApplicationContext(),
                ((CompassApplication)getActivity().getApplication()).getCategories()));
        mCategoryList.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MyPrioritiesCategoryAdapter)mCategoryList.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        mListener.onCategorySelected((Category) mCategoryList.getAdapter().getItem(position));
    }

    public interface OnCategorySelectedListener{
        void onCategorySelected(Category category);
    }
}
