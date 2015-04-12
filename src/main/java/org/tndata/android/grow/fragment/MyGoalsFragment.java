package org.tndata.android.grow.fragment;

import java.util.ArrayList;

import org.tndata.android.grow.GrowApplication;
import org.tndata.android.grow.R;
import org.tndata.android.grow.activity.GoalTryActivity;
import org.tndata.android.grow.adapter.MyGoalsAdapter;
import org.tndata.android.grow.adapter.MyGoalsAdapter.OnClickEvent;
import org.tndata.android.grow.model.Goal;
import org.tndata.android.grow.model.MyGoalsViewItem;
import org.tndata.android.grow.model.Survey;
import org.tndata.android.grow.task.SurveyFinderTask;
import org.tndata.android.grow.task.SurveyResponseTask;
import org.tndata.android.grow.ui.button.FloatingActionButton;
import org.tndata.android.grow.util.Constants;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyGoalsFragment extends Fragment implements SurveyFinderTask.SurveyFinderInterface,
        SurveyResponseTask.SurveyResponseListener, MyGoalsAdapter.SurveyCompleteInterface {
    private TextView mErrorTextView;
    private FloatingActionButton mFloatingActionButton;
    private RecyclerView mRecyclerView;
    private MyGoalsAdapter mAdapter;
    private ArrayList<MyGoalsViewItem> mItems = new ArrayList<MyGoalsViewItem>();
    private boolean mBroadcastIsRegistered = false;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean mSurveyShown, mSurveyLoading = false;
    private MyGoalsFragmentListener mCallback;

    public interface MyGoalsFragmentListener {
        public void chooseCategories();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGoals();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_goals, container, false);

        mErrorTextView = (TextView) v
                .findViewById(R.id.my_goals_error_textview);
        mFloatingActionButton = (FloatingActionButton) v.findViewById(R.id.my_goals_fab_button);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.chooseCategories();
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) v
                .findViewById(R.id.my_goals_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyGoalsAdapter(getActivity().getApplicationContext(),
                mItems, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayList<Goal> goals = ((GrowApplication) getActivity()
                .getApplication()).getGoals();
        if (goals != null && !goals.isEmpty()) {
            for (Goal goal : goals) {
                MyGoalsViewItem item = new MyGoalsViewItem();
                item.setGoal(goal);
                mItems.add(item);
            }
        }

        mAdapter = new MyGoalsAdapter(getActivity().getApplicationContext(),
                mItems, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickEvent(new OnClickEvent() {

            @Override
            public void onClick(View v, int position) {
                MyGoalsViewItem item = mItems.get(position);
                if (item.getType() == MyGoalsViewItem.TYPE_GOAL) {
                    Goal goal = item.getGoal();
                    Intent intent = new Intent(getActivity()
                            .getApplicationContext(), GoalTryActivity.class);
                    Log.d("Goal?",
                            "id:" + goal.getId() + " title:" + goal.getTitle());
                    intent.putExtra("goal", goal);
                    startActivity(intent);
                }
            }
        });

        if (mItems.isEmpty()) {
            showError();
        } else {
            showList();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity); // This makes sure that the container activity
        // has implemented the callback interface. If not, it throws an
        // exception
        try {
            mCallback = (MyGoalsFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MyGoalsFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private void registerReceivers() {
        if (mBroadcastIsRegistered == false) {
            getActivity().getApplicationContext().registerReceiver(broadcastReceiver,
                    new IntentFilter(Constants.GOAL_UPDATED_BROADCAST_ACTION));
            mBroadcastIsRegistered = true;
        }
    }

    private void unRegisterReceivers() {
        if (mBroadcastIsRegistered == true) {
            try {
                getActivity().getApplicationContext().unregisterReceiver(broadcastReceiver);
                mBroadcastIsRegistered = false;
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onResume() {
        registerReceivers();
        super.onResume();
        loadSurvey();
    }

    @Override
    public void onPause() {
        unRegisterReceivers();
        super.onPause();
    }

    private void showError() {
        mErrorTextView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void showList() {
        mErrorTextView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void loadSurvey() {
        if (!mSurveyLoading && !mSurveyShown) {
            new SurveyFinderTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    ((GrowApplication) getActivity().getApplication()).getToken());
            mSurveyLoading = true;
        }
    }

    public void updateGoals() {
        ArrayList<Goal> goals = ((GrowApplication) getActivity()
                .getApplication()).getGoals();
        if (goals != null && !goals.isEmpty()) {
            Log.d("Goals?", String.valueOf(goals.size()));
            for (Goal goal : goals) {
                MyGoalsViewItem item = new MyGoalsViewItem();
                item.setGoal(goal);
                mItems.add(item);
            }
            mAdapter.notifyDataSetChanged();
            showList();
        } else {
            showError();
        }
    }

    @Override
    public void surveyFound(Survey survey) {
        mSurveyLoading = false;
        mSurveyShown = true;
        MyGoalsViewItem item = new MyGoalsViewItem();
        item.setSurvey(survey);
        mItems.add(0, item);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void surveyResponseRecorded(Survey survey) {
        int i = 0;
        for (; i < mItems.size(); i++) {
            Survey s = mItems.get(i).getSurvey();
            if (s != null && s.getId() == survey.getId()) {
                mItems.remove(i);
                mSurveyShown = false;
                mAdapter.notifyDataSetChanged();
                break;
            }
        }

    }

    @Override
    public void surveyCompleted(Survey survey) {
        new SurveyResponseTask(getActivity(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, survey);
    }
}
