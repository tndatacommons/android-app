package org.tndata.android.compass.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ChooseGoalAdapter;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.model.TDCGoal;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;

import java.util.List;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * Created by isma on 5/4/16.
 */
public class ChooseGoalFragment extends Fragment implements HttpRequest.RequestCallback, Parser.ParserCallback, ChooseGoalAdapter.ChooseGoalListener{
    public static final String KEY = "org.tndata.compass.ChooseGoal.Category";


    public static ChooseGoalFragment newInstance(TDCCategory category){
        ChooseGoalFragment fragment = new ChooseGoalFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(KEY, category);
        fragment.setArguments(arguments);
        return fragment;
    }


    private ChooseGoalAdapter.ChooseGoalListener mListener;
    private TDCCategory category;
    private RecyclerView recyclerView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        category = getArguments().getParcelable(KEY);
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
        CompassApplication app = (CompassApplication)getActivity().getApplication();
        recyclerView = (RecyclerView)root.findViewById(R.id.list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.d("OB", "Getting " + API.getGoalsUrl(category));
        HttpRequest.get(this, API.getGoalsUrl(category));
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        Parser.parse(result, ParserModels.GoalContentResultSet.class, this);
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){

    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){

    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        List<TDCGoal> goals = ((ParserModels.GoalContentResultSet)result).results;

        recyclerView.setAdapter(new ChooseGoalAdapter(getContext(), this, category, goals));
    }

    @Override
    public void onGoalSelected(@NonNull TDCGoal goal, TDCCategory category){
        mListener.onGoalSelected(goal, category);
    }
}
