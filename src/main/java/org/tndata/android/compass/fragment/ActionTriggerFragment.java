package org.tndata.android.compass.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Goal;

public class ActionTriggerFragment extends Fragment {

    private ActionTriggerFragmentListener mCallback;
    private Goal mGoal;
    private Action mAction;

    public interface ActionTriggerFragmentListener {
        // TODO; define methods that this should imlement
        public void todo();
    }

    public static ActionTriggerFragment newInstance(Goal goal, Action action) {
        ActionTriggerFragment fragment = new ActionTriggerFragment();
        Bundle args = new Bundle();
        args.putSerializable("goal", goal);
        args.putSerializable("action", action);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (ActionTriggerFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ActionTriggerFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoal = getArguments() != null ? ((Goal) getArguments().get(
                "goal")) : new Goal();
        mAction = getArguments() != null ? ((Action) getArguments().get(
                "action")) : new Action();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_action_trigger, container, false);

        TextView titleTextView = (TextView) v.findViewById(R.id.action_title);
        titleTextView.setText(mAction.getTitle());

//        TextView titleTextView = (TextView) v.findViewById(R.id.goal_title_textview);
//        titleTextView.setText(mGoal.getTitle());

        return v;
    }
}
