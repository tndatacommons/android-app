package org.tndata.android.grow.fragment;

import java.util.ArrayList;

import org.tndata.android.grow.GrowApplication;
import org.tndata.android.grow.R;
import org.tndata.android.grow.model.Action;
import org.tndata.android.grow.model.Behavior;
import org.tndata.android.grow.task.ActionLoaderTask;
import org.tndata.android.grow.task.ActionLoaderTask.ActionLoaderListener;
import org.tndata.android.grow.ui.ActionCellView;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BehaviorFragment extends Fragment implements ActionLoaderListener {
    private Behavior mBehavior;
    private LinearLayout mActionsContainer;
    private BehaviorFragmentListener mCallback;
    private ArrayList<Action> mActionList;

    public interface BehaviorFragmentListener {
        public void learnMore();

        public void cancel();

        public void addAction(Action action);
    }

    public static BehaviorFragment newInstance(Behavior behavior) {
        BehaviorFragment fragment = new BehaviorFragment();
        Bundle args = new Bundle();
        args.putSerializable("behavior", behavior);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBehavior = getArguments() != null ? ((Behavior) getArguments().get(
                "behavior")) : new Behavior();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_behavior, container, false);
        TextView titleTextView = (TextView) v
                .findViewById(R.id.behavior_title_textview);
        titleTextView.setText(mBehavior.getTitle());
        Button noThanksButton = (Button) v
                .findViewById(R.id.behavior_no_thanks_button);
        noThanksButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.cancel();
            }
        });
        Button learnMoreButton = (Button) v
                .findViewById(R.id.behavior_learn_more_button);
        learnMoreButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.learnMore();
            }
        });
        mActionsContainer = (LinearLayout) v
                .findViewById(R.id.behavior_actions_container);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadActions();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity); // This makes sure that the container activity
        // has implemented the callback interface. If not, it throws an
        // exception
        try {
            mCallback = (BehaviorFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LearnMoreListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private void loadActions() {
        new ActionLoaderTask(this).executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR,
                ((GrowApplication) getActivity().getApplication()).getToken(),
                String.valueOf(mBehavior.getId()));
    }

    @Override
    public void actionsLoaded(ArrayList<Action> actions) {
        mActionList = actions;
        if (mActionList != null) {
            drawActions();
        }
    }

    private void drawActions() {
        for (Action action : mActionList) {
            Log.d("Action", "*draw name:" + action.getTitle());
            ActionCellView acv = new ActionCellView(getActivity()
                    .getApplicationContext());
            acv.setAction(action);    
            acv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (v instanceof ActionCellView) {
                        Action a = ((ActionCellView) v).getAction();
                        mCallback.addAction(a);
                    }

                }
            });
            mActionsContainer.addView(acv); 
        }
    }
}
