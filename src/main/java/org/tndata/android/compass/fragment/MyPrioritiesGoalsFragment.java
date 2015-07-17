package org.tndata.android.compass.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.MyPrioritiesGoalAdapter;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.util.ImageLoader;


/**
 * Created by isma on 7/17/15.
 */
public class MyPrioritiesGoalsFragment extends Fragment{
    private Category mCategory;
    private ImageLoader mLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategory = getArguments() != null ? ((Category) getArguments().get(
                "category")) : new Category();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView =  inflater.inflate(R.layout.fragment_my_priorities_goals, container, false);

        mLoader = new ImageLoader(getActivity().getApplicationContext());

        RecyclerView rv = (RecyclerView)rootView.findViewById(R.id.priorities_goals_recyclerview);
        rv.setAdapter(new MyPrioritiesGoalAdapter(getActivity().getApplicationContext(), mCategory.getGoals(), mLoader));
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

    @Override
    public void onPause(){
        mLoader.closeCache();
        super.onPause();
    }
}
