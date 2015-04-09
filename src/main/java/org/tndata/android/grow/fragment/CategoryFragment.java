package org.tndata.android.grow.fragment;

import org.tndata.android.grow.R;
import org.tndata.android.grow.activity.ChooseGoalsActivity;
import org.tndata.android.grow.model.Category;
import org.tndata.android.grow.ui.button.FloatingActionButton;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class CategoryFragment extends Fragment {
    private Category mCategory;
    private FloatingActionButton mFloatingActionButton;

    public static CategoryFragment newInstance(Category category) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putSerializable("category", category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategory = getArguments() != null ? ((Category) getArguments().get(
                "category")) : new Category();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_category, container, false);
        mFloatingActionButton = (FloatingActionButton) v.findViewById(R.id.category_fab_button);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGoals();
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void addGoals() {
        Intent intent = new Intent(getActivity().getApplicationContext(), ChooseGoalsActivity.class);
        intent.putExtra("category", mCategory);
        startActivity(intent);
    }

}
