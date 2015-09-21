package org.tndata.android.compass.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;

import java.util.List;


/**
 * Displays a behavior and allows the user to report progress.
 *
 * @author Edited by Ismael Alonso
 * @version 1.2.0
 */
public class BehaviorProgressFragment extends Fragment{
    private Behavior mBehavior;
    private ProgressBar mProgressBar;


    /**
     * Creates an empty fragment.
     *
     * @return the newly created fragment.
     */
    public static BehaviorProgressFragment newInstance(){
        return new BehaviorProgressFragment();
    }

    /**
     * Creates a new fragment and delivers the behavior in a bundle.
     *
     * @param behavior the behavior this fragment represents.
     * @return the newly created fragment.
     */
    public static BehaviorProgressFragment newInstance(@NonNull Behavior behavior){
        Bundle args = new Bundle();
        args.putSerializable("behavior", behavior);

        BehaviorProgressFragment fragment = new BehaviorProgressFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mBehavior = (getArguments() == null) ? null : ((Behavior) getArguments().get("behavior"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_behavior_progress, container, false);

        mProgressBar = (ProgressBar)v.findViewById(R.id.behavior_progress_progress_bar);
        if (mBehavior == null){
            showProgress();
        }
        else{
            TextView behaviorTitle = (TextView)v.findViewById(R.id.behavior_progress_behavior_title);
            behaviorTitle.setText("I want to " + mBehavior.getTitle().substring(0, 1).toLowerCase()
                    + mBehavior.getTitle().substring(1) + ".");

            String label = "Because I care about my ";
            List<Category> categories = mBehavior.getUserCategories();
            if (categories.size() == 1){
                label += categories.get(0).getTitle().toUpperCase();
            }
            else if (categories.size() == 2){
                label += categories.get(0).getTitle().toUpperCase();
                label += " and ";
                label += categories.get(1).getTitle().toUpperCase();
            }
            else if (categories.size() >= 3){
                for (int i = 0; i < categories.size()-1; i++){
                    label += categories.get(i).getTitle().toUpperCase();
                    label += ", ";
                }
                label += "and " + categories.get(categories.size()-1).getTitle().toUpperCase();
            }
            label += ".";
            TextView behaviorLabel = (TextView)v.findViewById(R.id.behavior_progress_behavior_label);
            behaviorLabel.setText(label);
        }

        return v;
    }

    public void showProgress(){
        mProgressBar.setVisibility(View.VISIBLE);
    }
}
