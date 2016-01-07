package org.tndata.android.compass.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.tndata.android.compass.R;
import org.tndata.android.compass.activity.MyPrioritiesActivity;
import org.tndata.android.compass.adapter.MyPrioritiesGoalAdapter;
import org.tndata.android.compass.model.UserCategory;


/**
 * Fragment containing a list of expandable goals within a category.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class MyPrioritiesGoalsFragment extends Fragment{
    public static final String USER_CATEGORY_KEY = "org.tndata.compass.MyPrioritiesGoals.UserCategory";


    private UserCategory mUserCategory;
    private MyPrioritiesGoalAdapter mAdapter;


    public static MyPrioritiesGoalsFragment newInstance(UserCategory userCategory){
        Bundle args = new Bundle();
        args.putSerializable(USER_CATEGORY_KEY, userCategory);

        MyPrioritiesGoalsFragment fragment = new MyPrioritiesGoalsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mUserCategory = (UserCategory)getArguments().get(USER_CATEGORY_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView =  inflater.inflate(R.layout.fragment_my_priorities_goals, container, false);

        mAdapter = new MyPrioritiesGoalAdapter(getActivity().getApplicationContext(),
                mUserCategory, (MyPrioritiesActivity) getActivity());

        RecyclerView rv = (RecyclerView)rootView.findViewById(R.id.priorities_goals_recyclerview);
        rv.setAdapter(mAdapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

    /**
     * Updates the adapter data after the data set has changed.
     */
    public void updateAdapterData(){
        mAdapter.updateData();
    }
}
