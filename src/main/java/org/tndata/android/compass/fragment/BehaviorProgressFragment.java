package org.tndata.android.compass.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;

import java.util.ArrayList;


/**
 * Displays a behavior and allows the user to report progress.
 *
 * @author Edited by Ismael Alonso
 * @version 1.1.0
 */
public class BehaviorProgressFragment extends Fragment{
    private Behavior mBehavior;


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
        mBehavior = (getArguments() == null) ?
                new Behavior() : ((Behavior) getArguments().get("behavior"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_behavior_progress, container, false);

        String label = "My ";
        ArrayList<Category> categories = mBehavior.getUserCategories();
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
        label += ". That's why I want to:";
        TextView behaviorLabel = (TextView)v.findViewById(R.id.behavior_progress_behavior_label);
        behaviorLabel.setText(label);

        TextView behaviorTitle = (TextView)v.findViewById(R.id.behavior_progress_behavior_title);
        behaviorTitle.setText(mBehavior.getTitle());

        return v;
    }
}
