package org.tndata.android.compass.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.util.Constants;


/**
 * Displays a behavior and allows the user to report progress.
 *
 * @author Edited by Ismael Alonso
 * @version 1.1.0
 */
public class BehaviorProgressFragment extends Fragment implements OnClickListener{
    private Behavior mBehavior;
    private ProgressBar mProgressBar;
    private OnProgressSelectedListener mCallback;


    public void setBehavior(Behavior behavior){
        mBehavior = behavior;
    }

    public static BehaviorProgressFragment newInstance(Behavior behavior){
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
    public void onAttach(Activity activity){
        super.onAttach(activity);
        //This makes sure that the container activity has implemented the callback
        //  interface. If not, it throws an exception.
        try{
            mCallback = (OnProgressSelectedListener)activity;
        }
        catch (ClassCastException e){
            throw new ClassCastException(activity.toString()
                    + " must implement BehaviorProgressFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_behavior_progress, container, false);

        TextView title = (TextView)v.findViewById(R.id.behavior_progress_title);
        title.setText(getResources().getText(R.string.behavior_progress_title));

        //String label = getResources().getString(R.string.behavior_progress_behavior_label, mBehavior.get);
        TextView behaviorLabel = (TextView)v.findViewById(R.id.behavior_progress_behavior_label);
        behaviorLabel.setText("Test text");

        TextView behaviorTitle = (TextView)v.findViewById(R.id.behavior_progress_behavior_title);
        behaviorTitle.setText(mBehavior.getTitle());

        mProgressBar = (ProgressBar) v.findViewById(R.id.behavior_progress_progress_bar);

        v.findViewById(R.id.behavior_progress_on_track).setOnClickListener(this);
        v.findViewById(R.id.behavior_progress_seeking).setOnClickListener(this);
        v.findViewById(R.id.behavior_progress_off_course).setOnClickListener(this);

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onClick(View view){
        mProgressBar.setVisibility(View.VISIBLE);
        switch (view.getId()){
            case R.id.behavior_progress_on_track:
                mCallback.onProgressSelected(Constants.BEHAVIOR_ON_COURSE);
                break;

            case R.id.behavior_progress_seeking:
                mCallback.onProgressSelected(Constants.BEHAVIOR_SEEKING);
                break;

            case R.id.behavior_progress_off_course:
                mCallback.onProgressSelected(Constants.BEHAVIOR_OFF_COURSE);
                break;
        }
    }


    /**
     * Listener interface that reports a selection.
     *
     * @author Edited by Ismael Alonso
     * @version 1.1.0
     */
    public interface OnProgressSelectedListener{
        /**
         * Lets the listener know which option was chosen by the user.
         *
         * @param progressValue a representation of the progress selected.
         */
        void onProgressSelected(int progressValue);
    }
}
