package org.tndata.android.compass.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.util.Constants;

public class BehaviorProgressFragment extends Fragment  {

    private Behavior mBehavior;
    private ProgressBar mProgressBar;
    private BehaviorProgressFragmentListener mCallback;


    public interface BehaviorProgressFragmentListener {
        public void saveBehaviorProgress(int progressValue);
    }

    public void setBehavior(Behavior behavior) {
        mBehavior = behavior;
    }

    public static BehaviorProgressFragment newInstance(Behavior behavior) {
        BehaviorProgressFragment fragment = new BehaviorProgressFragment();
        Bundle args = new Bundle();
        args.putSerializable("behavior", behavior);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBehavior = getArguments() == null ? new Behavior() :
                ((Behavior) getArguments().get("behavior"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_behavior_progress, container, false);
        TextView titleTextView = (TextView) v
                .findViewById(R.id.behavior_progress_behavior_label);
        titleTextView.setText(mBehavior.getTitle());

        ImageButton on_track_button = (ImageButton) v.findViewById(
                R.id.behavior_progress_on_track);
        ImageButton off_course_button = (ImageButton) v.findViewById(
                R.id.behavior_progress_off_course);
        ImageButton seeking_button = (ImageButton) v.findViewById(
                R.id.behavior_progress_seeking);
        mProgressBar = (ProgressBar) v.findViewById(R.id.behavior_progress_progressbar);

        on_track_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                mCallback.saveBehaviorProgress(Constants.BEHAVIOR_ON_COURSE);
            }
        });

        off_course_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                mCallback.saveBehaviorProgress(Constants.BEHAVIOR_OFF_COURSE);
            }
        });

        seeking_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                mCallback.saveBehaviorProgress(Constants.BEHAVIOR_SEEKING);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity); // This makes sure that the container activity
        // has implemented the callback interface. If not, it throws an
        // exception
        try {
            mCallback = (BehaviorProgressFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BehaviorProgressFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

}
