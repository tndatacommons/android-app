package org.tndata.android.compass.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ChooseCategoryAdapter;
import org.tndata.android.compass.model.TDCCategory;


/**
 * Created by isma on 5/4/16.
 */
public class OnBoardingCategoryFragment
        extends Fragment
        implements
                ChooseCategoryAdapter.ChooseCategoryAdapterListener,
                View.OnClickListener{

    private CategoryListener mListener;

    private Button mNext;


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            mListener = (CategoryListener)context;
        }
        catch (ClassCastException ccx){
            throw new IllegalStateException("why");
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

        TextView explanation = (TextView)root.findViewById(R.id.list_explanation);
        explanation.setText(R.string.list_explanation);
        root.findViewById(R.id.list_explanation_container).setVisibility(View.VISIBLE);

        RecyclerView list = (RecyclerView)root.findViewById(R.id.list_recycler_view);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setAdapter(new ChooseCategoryAdapter(getContext(), this, app.getFeaturedCategories()));

        mNext = (Button)root.findViewById(R.id.list_button);
        mNext.setVisibility(View.VISIBLE);
        mNext.setOnClickListener(this);
    }

    public void onContentSelected(){
        if (mNext != null){
            mNext.setText(R.string.onboarding_category_finish);
        }
    }

    @Override
    public void onCategorySelected(TDCCategory category){
        mListener.onCategorySelected(category);
    }

    @Override
    public void onClick(View v){
        v.setOnClickListener(null);
        mListener.onNext();
    }

    public interface CategoryListener{
        void onCategorySelected(TDCCategory category);
        void onNext();
    }
}
