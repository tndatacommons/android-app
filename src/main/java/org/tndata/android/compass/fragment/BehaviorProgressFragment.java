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

        //String label = getResources().getString(R.string.behavior_progress_behavior_label, mBehavior.get);
        TextView behaviorLabel = (TextView)v.findViewById(R.id.behavior_progress_behavior_label);
        behaviorLabel.setText("Test text");

        TextView behaviorTitle = (TextView)v.findViewById(R.id.behavior_progress_behavior_title);
        behaviorTitle.setText(mBehavior.getTitle());

        return v;
    }
}
