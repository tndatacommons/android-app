package org.tndata.android.compass.fragment;

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

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.activity.ChooseGoalsActivity;
import org.tndata.android.compass.activity.GoalTryActivity;
import org.tndata.android.compass.adapter.MyGoalsAdapter;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.model.MyGoalsViewItem;
import org.tndata.android.compass.model.Survey;
import org.tndata.android.compass.task.SurveyFinderTask;
import org.tndata.android.compass.task.SurveyResponseTask;
import org.tndata.android.compass.ui.SpacingItemDecoration;
import org.tndata.android.compass.ui.button.FloatingActionButton;
import org.tndata.android.compass.util.Constants;

import java.util.ArrayList;

public class MyGoalsFragment extends Fragment implements SurveyFinderTask.SurveyFinderInterface,
        SurveyResponseTask.SurveyResponseListener, MyGoalsAdapter.MyGoalsAdapterInterface {
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

        public void transitionToCategoryTab(Category category);
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
        mRecyclerView.addItemDecoration(new SpacingItemDecoration(30));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyGoalsAdapter(getActivity(), mItems, this);
        mRecyclerView.setAdapter(mAdapter);
        registerReceivers();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayList<Category> categories = ((CompassApplication) getActivity().getApplication())
                .getCategories();
        if (categories != null && !categories.isEmpty()) {
            Log.d("Categories?", String.valueOf(categories.size()));
            clearAllButSurvey();
            for (Category category : categories) {
                MyGoalsViewItem item = new MyGoalsViewItem();
                item.setCategory(category);
                mItems.add(item);
            }
        }

        mAdapter = new MyGoalsAdapter(getActivity(), mItems, this);
        mRecyclerView.setAdapter(mAdapter);

        if (mItems.isEmpty()) {
            MyGoalsViewItem item = new MyGoalsViewItem(); // the default content item
            mItems.add(item);
            mAdapter.notifyDataSetChanged();
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
        super.onResume();
        loadSurvey();
    }

    @Override
    public void onDestroy() {
        unRegisterReceivers();
        super.onDestroy();
    }

    private void loadSurvey() {
        if (!mSurveyLoading && !mSurveyShown && Constants.ENABLE_SURVEYS) {
            new SurveyFinderTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    ((CompassApplication) getActivity().getApplication()).getToken());
            mSurveyLoading = true;
        }
    }

    public void updateGoals() {
        ArrayList<Category> categories = ((CompassApplication) getActivity().getApplication())
                .getCategories();
        if (categories != null && !categories.isEmpty()) {
            Log.d("Categories?", String.valueOf(categories.size()));
            clearAllButSurvey();
            for (Category category : categories) {
                MyGoalsViewItem item = new MyGoalsViewItem();
                item.setCategory(category);
                mItems.add(item);
            }
            mAdapter.notifyDataSetChanged();
        } else {
            mItems.clear();
            MyGoalsViewItem item = new MyGoalsViewItem(); // the default content item
            mItems.add(item);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void clearAllButSurvey() {
        if (mItems.size() > 0) {
            MyGoalsViewItem item = mItems.remove(0);
            if (Constants.ENABLE_SURVEYS && (
                    (item.getType() == MyGoalsViewItem.TYPE_SURVEY_MULTICHOICE) ||
                    (item.getType() == MyGoalsViewItem.TYPE_SURVEY_BINARY) ||
                    (item.getType() == MyGoalsViewItem.TYPE_SURVEY_LIKERT) ||
                    (item.getType() == MyGoalsViewItem.TYPE_SURVEY_OPENENDED))) {
                mItems.clear();
                mItems.add(item);
            } else {
                mItems.clear();
            }
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
        new SurveyResponseTask(getActivity(), this).executeOnExecutor(AsyncTask
                .THREAD_POOL_EXECUTOR, survey);
    }

    @Override
    public void chooseGoals(Category category) {
        Intent intent = new Intent(getActivity().getApplicationContext(),
                ChooseGoalsActivity.class);
        intent.putExtra("category", category);
        startActivityForResult(intent, Constants.CHOOSE_GOALS_REQUEST_CODE);
    }

    @Override
    public void chooseBehaviors(Goal goal, Category category) {
        Intent intent = new Intent(getActivity()
                .getApplicationContext(), GoalTryActivity.class);
        intent.putExtra("goal", goal);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    @Override
    public void activateCategoryTab(Category category) {
        mCallback.transitionToCategoryTab(category);
    }
}
