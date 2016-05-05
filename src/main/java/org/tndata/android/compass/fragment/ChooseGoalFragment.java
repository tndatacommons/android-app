package org.tndata.android.compass.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ChooseGoalAdapter;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.model.TDCGoal;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by isma on 5/4/16.
 */
public class ChooseGoalFragment extends Fragment implements ChooseGoalAdapter.ChooseGoalListener{
    public static final String CAT_KEY = "org.tndata.compass.ChooseGoal.Category";
    public static final String GOALS_KEY = "org.tndata.compass.ChooseGoal.Goals";


    public static ChooseGoalFragment newInstance(TDCCategory category, List<TDCGoal> goalz){
        ChooseGoalFragment fragment = new ChooseGoalFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(CAT_KEY, category);
        arguments.putParcelableArrayList(GOALS_KEY, (ArrayList<TDCGoal>)goalz);
        fragment.setArguments(arguments);
        return fragment;
    }


    private ChooseGoalAdapter.ChooseGoalListener mListener;
    private TDCCategory category;
    private List<TDCGoal> goals;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        category = getArguments().getParcelable(CAT_KEY);
        goals = getArguments().getParcelableArrayList(GOALS_KEY);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            mListener = (ChooseGoalAdapter.ChooseGoalListener)context;
        }
        catch (ClassCastException ccx){
            throw new ClassCastException("Why2");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View root, @Nullable Bundle savedInstanceState){
        TextView explanation = (TextView)root.findViewById(R.id.list_explanation);
        explanation.setText(R.string.list_explanation2);
        root.findViewById(R.id.list_explanation_container).setVisibility(View.VISIBLE);

        RecyclerView recyclerView = (RecyclerView)root.findViewById(R.id.list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ChooseGoalAdapter(getContext(), this, category, goals));
    }

    @Override
    public void onGoalSelected(@NonNull TDCGoal goal, TDCCategory category){
        mListener.onGoalSelected(goal, category);
    }
}
